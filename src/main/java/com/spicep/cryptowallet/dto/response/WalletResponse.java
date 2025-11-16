package com.spicep.cryptowallet.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record WalletResponse(String id, String email, BigDecimal total, List<AssetResponse> assets) {}
