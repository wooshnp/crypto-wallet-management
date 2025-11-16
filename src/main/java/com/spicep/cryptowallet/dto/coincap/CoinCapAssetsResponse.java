package com.spicep.cryptowallet.dto.coincap;

import java.util.List;

public record CoinCapAssetsResponse(
        List<CoinCapAsset> data,
        Long timestamp
) {}
