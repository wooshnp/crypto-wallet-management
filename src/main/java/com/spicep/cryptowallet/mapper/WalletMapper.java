package com.spicep.cryptowallet.mapper;

import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AssetMapper.class})
public interface WalletMapper {

    @Mapping(target = "email", expression = "java(wallet.getUser().getEmail())")
    @Mapping(target = "total", expression = "java(wallet.getTotalValue())")
    WalletResponse toResponse(Wallet wallet);

}
