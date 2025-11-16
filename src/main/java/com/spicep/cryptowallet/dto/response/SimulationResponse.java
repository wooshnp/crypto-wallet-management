package com.spicep.cryptowallet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record SimulationResponse(
        BigDecimal total,

        @JsonProperty("best_asset")
        String bestAsset,

        @JsonProperty("best_performance")
        BigDecimal bestPerformance,

        @JsonProperty("worst_asset")
        String worstAsset,

        @JsonProperty("worst_performance")
        BigDecimal worstPerformance
) {}
