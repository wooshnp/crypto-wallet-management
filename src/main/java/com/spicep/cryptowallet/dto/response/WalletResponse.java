package com.spicep.cryptowallet.dto.response;

import com.spicep.cryptowallet.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private String id;
    private String email;
    private BigDecimal total;
    private List<AssetResponse> assets;

}
