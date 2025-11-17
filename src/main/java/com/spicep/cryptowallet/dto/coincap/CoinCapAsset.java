package com.spicep.cryptowallet.dto.coincap;

public record CoinCapAsset(
        String id,
        String rank,
        String symbol,
        String name,
        String priceUsd,
        String supply,
        String maxSupply,
        String marketCapUsd,
        String volumeUsd24Hr,
        String changePercent24Hr,
        String vwap24Hr
) {}
