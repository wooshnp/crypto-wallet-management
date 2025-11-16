package com.spicep.cryptowallet.dto.response;

import java.math.BigDecimal;

public record AssetPerformance(BigDecimal originalValue,BigDecimal currentValue,BigDecimal performancePercentage) {
}
