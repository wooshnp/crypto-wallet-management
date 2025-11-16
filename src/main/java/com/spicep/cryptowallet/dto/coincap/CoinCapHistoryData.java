package com.spicep.cryptowallet.dto.coincap;

public record CoinCapHistoryData(
        String priceUsd,
        Long time,
        String date
) {}
