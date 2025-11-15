package com.spicep.cryptowallet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {

    private BigDecimal total;

    @JsonProperty("best_asset")
    private String bestAsset;

    @JsonProperty("best_performance")
    private BigDecimal bestPerformance;

    @JsonProperty("worst_asset")
    private String worstAsset;

    @JsonProperty("worst_performance")
    private BigDecimal worstPerformance;
}
