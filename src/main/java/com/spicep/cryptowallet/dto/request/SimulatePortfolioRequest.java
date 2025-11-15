package com.spicep.cryptowallet.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulatePortfolioRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotEmpty(message = "Assets list cannot be empty")
    @Valid
    private List<SimulatePortfolioAssetInput> assets;

}
