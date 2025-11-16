package com.spicep.cryptowallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulatePortfolioAssetInput(
        @NotNull(message = "Symbol is required")
        String symbol,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
        BigDecimal quantity,

        @NotNull(message = "Value is required")
        @DecimalMin(value = "0.0",  inclusive = false, message = "Value must be greater than 0")
        BigDecimal value
) {}
