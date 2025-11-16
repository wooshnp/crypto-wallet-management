package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.client.CoinCapClient;
import com.spicep.cryptowallet.enums.CoinCapInterval;
import com.spicep.cryptowallet.exception.AssetNotFoundException;
import com.spicep.cryptowallet.exception.CoinCapApiException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinCapService {

    private final CoinCapClient coinCapClient;
    private final CoinCapResolver coinCapResolver;

    /**
     * Validates that an asset exists on CoinCap and returns its current price
     *
     * @param symbol        Asset symbol (e.g. "BTC")
     * @param providedPrice User-provided price for validation (optional)
     * @return Current price in USD from CoinCap
     * @throws AssetNotFoundException if symbol not found on CoinCap
     * @throws CoinCapApiException    if API call fails
     */
    public BigDecimal validateAssetPrice(String symbol, BigDecimal providedPrice) {
        try {
            var coinCapId = coinCapResolver.resolveCoinCapId(symbol);
            var response = coinCapClient.getAsset(coinCapId);

            if (response == null || response.data() == null) {
                throw AssetNotFoundException.invalidResponse(symbol);
            }

            var actualPrice = new BigDecimal(response.data().priceUsd());

            if (providedPrice != null) {
                var tolerance = actualPrice.multiply(new BigDecimal("0.05")); // 5% tolerance
                var difference = actualPrice.subtract(providedPrice).abs();

                if (difference.compareTo(tolerance) > 0) {
                    log.warn("Price mismatch for {}: provided={}, actual={}, difference={}%",
                            symbol, providedPrice, actualPrice,
                            difference.divide(actualPrice, 4, RoundingMode.HALF_DOWN)
                                    .multiply(new BigDecimal("100")));
                }
            }

            log.debug("Validated asset {}: price={}", symbol, actualPrice);
            return actualPrice;

        } catch (FeignException.NotFound e) {
            throw AssetNotFoundException.notFoundOnCoinCap(symbol);
        } catch (FeignException e) {
            log.error("Error calling CoinCap API for asset {}", symbol, e);
            throw CoinCapApiException.fetchPriceFailed(symbol, e);
        }
    }

    /**
     * Get historical price for a specific date
     *
     * @param symbol Asset symbol
     * @param date   Date to get price for
     * @return Price in USD on that date
     * @throws AssetNotFoundException if no price data found for that date
     * @throws CoinCapApiException    if API call fails
     */
    public BigDecimal getHistoricalPrice(String symbol, LocalDate date) {
        var startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        var endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        try {
            var coinCapId = coinCapResolver.resolveCoinCapId(symbol);
            var response = coinCapClient.getAssetHistory(coinCapId, CoinCapInterval.DAY_1.getValue(), startOfDay,
                    endOfDay);

            if (response == null || response.data().isEmpty()) {
                throw AssetNotFoundException.noHistoricalData(symbol, date);
            }

            var priceData = response.data().getFirst();
            var price = new BigDecimal(priceData.priceUsd());

            log.debug("Historical price for {} on {}: {}", symbol, date, price);
            return price;

        } catch (FeignException.NotFound e) {
            throw AssetNotFoundException.withCause(symbol, date, e);
        } catch (FeignException e) {
            log.error("Error fetching historical price for {} on {}", symbol, date, e);
            throw CoinCapApiException.fetchHistoricalPriceFailed(symbol, date, e);
        }
    }

    /**
     * Get current price for an asset
     *
     * @param symbol Asset symbol
     * @return Current price in USD
     */
    public BigDecimal getCurrentPrice(String symbol) {
        return validateAssetPrice(symbol, null);
    }

}
