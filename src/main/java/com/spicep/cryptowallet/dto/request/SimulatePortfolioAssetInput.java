package com.spicep.cryptowallet.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatePortfolioAssetInput {

    @NotNull(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Quantity is required")
    private Double quantity;

    @NotNull(message = "Value is required")
    private Double value;

}
