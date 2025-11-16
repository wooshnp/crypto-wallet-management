package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.client.CoinCapClient;
import com.spicep.cryptowallet.dto.coincap.CoinCapAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final CoinCapClient coinCapClient;

    /**
     * Get list of all available crypto symbols from CoinCap
     * @return A simple list of crypto symbols
     */
    @Cacheable(cacheNames = "availableSymbols")
    public List<String> getAvailableSymbols() {
        log.info("Fetching available symbols from CoinCap (cache miss)");

        var response = coinCapClient.getAssets("", 2000);

        return response.data().stream().map(CoinCapAsset::symbol).distinct().sorted().toList();
    }

    /**
     * Search for cryptocurrencies on CoinCap with pagination
     * Cached based on search term and limit
     *
     * @param search Optional search term (can be null)
     * @param limit Number of results (default 100, max 2000)
     */
    @Cacheable(cacheNames = "availableAssets", key = "#search + '-' + #limit")
    public List<CoinCapAsset> getAvailableAssets(String search, Integer limit) {
        log.info("Fetching available assets from CoinCap: search={}, limit={} (cache miss)", search, limit);

        var response = coinCapClient.getAssets(search, limit);

        return response.data();
    }
}
