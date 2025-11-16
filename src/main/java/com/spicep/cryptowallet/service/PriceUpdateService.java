package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.config.PriceUpdateProperties;
import com.spicep.cryptowallet.entity.Asset;
import com.spicep.cryptowallet.entity.PriceHistory;
import com.spicep.cryptowallet.repository.AssetRepository;
import com.spicep.cryptowallet.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceUpdateService {

    private final AssetRepository assetRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final CoinCapService coinCapService;
    private final PriceUpdateProperties properties;

    private final Executor priceUpdateExecutor;

    /**
     * Scheduled task that updates prices for all assets in the system
     */
    @Scheduled(fixedDelayString = "${wallet.price-update.interval:60000}")
    public void updatePrices() {
        if (!properties.isEnable()) {
            return;
        }

        log.info("Starting scheduled price update");

        var uniqueSymbols = assetRepository.findDistinctSymbols();

        if (uniqueSymbols.isEmpty()) {
            log.debug("No assets found in database, skipping price update");
            return;
        }

        log.info("Found {} unique symbols to update", uniqueSymbols.size());

        List<CompletableFuture<Void>> futures = uniqueSymbols.stream()
                .map(symbol -> CompletableFuture.runAsync(() -> {
                    try {
                        updatePriceForSymbol(symbol);
                    } catch (Exception e) {
                        log.error("Failed to update price for symbol: {}", symbol, e);
                    }
                }, priceUpdateExecutor))
                .toList();

        // Blocks moving forward until all tasks are finished
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Completed price update for {} symbols", uniqueSymbols.size());
    }

    /**
     * Updates price for a symbol: fetches from CoinCap, saves history, updates assets
     */
    public void updatePriceForSymbol(String symbol) {

        var currentPrice = coinCapService.getCurrentPrice(symbol);

        var priceHistory = PriceHistory.create(symbol, currentPrice);
        priceHistoryRepository.save(priceHistory);

        var assets = assetRepository.findBySymbolIgnoreCase(symbol);

        assets.forEach(asset -> asset.updatePrice(currentPrice));
        assetRepository.saveAll(assets);

        log.info("Updated price for {} - ${} (affected {} assets)", symbol, currentPrice, assets.size());
    }
}
