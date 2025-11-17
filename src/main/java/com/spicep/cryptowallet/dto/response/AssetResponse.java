package com.spicep.cryptowallet.dto.response;

import java.math.BigDecimal;

public record AssetResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal acquisitionPrice,
        BigDecimal price,
        BigDecimal value
) {}
