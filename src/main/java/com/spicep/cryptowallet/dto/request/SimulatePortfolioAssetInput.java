package com.spicep.cryptowallet.dto.request;

import jakarta.validation.constraints.NotNull;

public record SimulatePortfolioAssetInput(
        @NotNull(message = "Symbol is required")
        String symbol,

        @NotNull(message = "Quantity is required")
        Double quantity,

        @NotNull(message = "Value is required")
        Double value
) {}
