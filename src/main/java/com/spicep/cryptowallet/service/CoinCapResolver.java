package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.client.CoinCapClient;
import com.spicep.cryptowallet.dto.coincap.CoinCapAsset;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoinCapResolver {

    private final CoinCapClient coinCapClient;

    /**
     * Searches for a coincap asset ID from a symbol and caches the result to avoid repeated calls.
     * Falls back to the lowercased symbol if search finds no exact match
     * @param symbol asset symbol (e.g. "BTC")
     * @return Coincap asset ID to use in the api call
     */
    @Cacheable(cacheNames = "coincapIds", key = "#symbol.toUpperCase()")
    public String resolveCoinCapId(String symbol) {
        try {
            var resp = coinCapClient.getAssets(symbol, 100, 0);
            return resp.data().stream()
                    .filter(a -> symbol.equalsIgnoreCase(a.symbol()))
                    .map(CoinCapAsset::id)
                    .findFirst()
                    .orElse(symbol.toLowerCase());
        } catch (FeignException e) {
            log.warn("CoinCap search failed for {}. Falling back to symbol path.", symbol, e);
            return symbol.toLowerCase();
        }
    }

}
