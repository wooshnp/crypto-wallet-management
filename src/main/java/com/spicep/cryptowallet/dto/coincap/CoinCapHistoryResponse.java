package com.spicep.cryptowallet.dto.coincap;

import java.util.List;

public record CoinCapHistoryResponse(
        List<CoinCapHistoryData> data,
        Long timestamp
) {}
