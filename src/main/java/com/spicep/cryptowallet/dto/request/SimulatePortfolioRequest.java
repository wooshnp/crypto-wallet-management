package com.spicep.cryptowallet.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SimulatePortfolioRequest(
        @NotNull(message = "Date is required")
        LocalDate date,

        @NotEmpty(message = "Assets list cannot be empty")
        @Valid
        List<SimulatePortfolioAssetInput> assets
) {}
