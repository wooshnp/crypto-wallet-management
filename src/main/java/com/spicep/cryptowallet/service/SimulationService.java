package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.dto.request.SimulatePortfolioAssetInput;
import com.spicep.cryptowallet.dto.request.SimulatePortfolioRequest;
import com.spicep.cryptowallet.dto.response.AssetPerformance;
import com.spicep.cryptowallet.dto.response.SimulationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SimulationService {

    private final CoinCapService coinCapService;

    @Value("${simulation.use-market-price.enable:false}")
    private boolean useMarketPrice;

    /**
     * Simulates portfolio performance from a past date to present
     *
     * @param request Contains the date and list of assets with their original values
     * @return Simulation result with total value and best/worst performing assets
     */
    public SimulationResponse simulatePortfolio(SimulatePortfolioRequest request) {
        log.info("Simulating portfolio performance from date: {} (useMarketPrice={})", request.date(), useMarketPrice);

        // Validate date is not in the future
        if (request.date().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Simulation date cannot be in the future");
        }

        Map<String, AssetPerformance> performances = new HashMap<>();
        var totalCurrentValue = BigDecimal.ZERO;

        for (SimulatePortfolioAssetInput asset : request.assets()) {
            // If useMarketPrice=true, user should not provide value
            if (useMarketPrice && asset.value() != null) {
                throw new IllegalArgumentException(
                        "Cannot provide value when simulation is configured to use market prices. " +
                                "Either disable it or remove the value field."
                );
            }

            // If useMarketPrice=false, user must provide value
            if (!useMarketPrice && asset.value() == null) {
                throw new IllegalArgumentException(
                        "Must provide value when simulation is configured for manual pricing. " +
                                "Either enable it or provide the value field."
                );
            }

            AssetPerformance performance = calculateAssetPerformance(asset.symbol(), asset.quantity(),
                    asset.value(), request.date()
            );

            performances.put(asset.symbol(), performance);
            totalCurrentValue = totalCurrentValue.add(performance.currentValue());

            log.debug("Asset {} - Original: ${}, Current: ${}, Performance: {}%", asset.symbol(),
                    performance.originalValue(), performance.currentValue(), performance.performancePercentage());
        }

        var bestAsset = performances.entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue().performancePercentage())).orElseThrow();

        var worstAsset = performances.entrySet().stream()
                .min(Comparator.comparing(e -> e.getValue().performancePercentage())).orElseThrow();

        log.info("Simulation complete - Total: ${}, Best: {} ({}%), Worst: {} ({}%)", totalCurrentValue,
                bestAsset.getKey(), bestAsset.getValue().performancePercentage(), worstAsset.getKey(),
                worstAsset.getValue().performancePercentage());

        return new SimulationResponse(totalCurrentValue.setScale(2, RoundingMode.HALF_UP), bestAsset.getKey(),
                bestAsset.getValue().performancePercentage(), worstAsset.getKey(),
                worstAsset.getValue().performancePercentage());
    }

    /**
     * Calculate individual asset performance based on configuration and user input
     *
     * @param symbol            Symbol of the asset (e.g. "btc")
     * @param quantity          Quantity of the asset the user bought
     * @param userProvidedValue The total value the user paid (only used when useMarketPrice=false)
     * @param targetDate        Date provided by the user
     * @return AssetPerformance metrics showing current value and percentage change
     */
    private AssetPerformance calculateAssetPerformance(
            String symbol,
            BigDecimal quantity,
            BigDecimal userProvidedValue,
            LocalDate targetDate) {

        var symbolUpper = symbol.toUpperCase();

        // Determine baseline original value based on configuration
        BigDecimal originalValue;
        if (useMarketPrice) {
            // Use CoinCap historical price for the target date as baseline
            var historicalPrice = coinCapService.getHistoricalPrice(symbolUpper, targetDate);
            originalValue = historicalPrice.multiply(quantity);
        } else {
            // Use user-provided value (already validated as non-null)
            originalValue = userProvidedValue;
        }

        // If targetDate is today, get current price, otherwise historical
        var currentPrice = targetDate.equals(LocalDate.now()) ? coinCapService.getCurrentPrice(symbolUpper)
                : coinCapService.getHistoricalPrice(symbolUpper, targetDate);

        // Calculate current value: quantity * currentPrice
        var currentValue = currentPrice.multiply(quantity);

        // Calculate performance percentage: ((currentValue - originalValue) / originalValue) * 100
        var valueChange = currentValue.subtract(originalValue);
        var performancePercentage = valueChange.divide(originalValue, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        return new AssetPerformance(originalValue.setScale(2, RoundingMode.HALF_UP),
                currentValue.setScale(2, RoundingMode.HALF_UP), performancePercentage
        );
    }

}
