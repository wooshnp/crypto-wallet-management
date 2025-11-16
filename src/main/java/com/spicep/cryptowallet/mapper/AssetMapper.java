package com.spicep.cryptowallet.mapper;

import com.spicep.cryptowallet.dto.response.AssetResponse;
import com.spicep.cryptowallet.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(source = "currentPrice", target = "price")
    @Mapping(expression = "java(asset.getValue())", target = "value")
    AssetResponse toResponse(Asset asset);

    List<AssetResponse> toAssetResponseList(List<Asset> assets);

}
