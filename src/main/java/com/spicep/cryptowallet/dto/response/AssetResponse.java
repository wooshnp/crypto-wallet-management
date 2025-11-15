package com.spicep.cryptowallet.dto.response;

import com.spicep.cryptowallet.entity.Asset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {

    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal value;

}
