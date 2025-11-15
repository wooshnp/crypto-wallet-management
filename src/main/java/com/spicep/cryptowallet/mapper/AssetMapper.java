package com.spicep.cryptowallet.mapper;

import com.spicep.cryptowallet.dto.response.AssetResponse;
import com.spicep.cryptowallet.entity.Asset;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    AssetResponse toResponse(Asset asset);

    List<AssetResponse> toAssetResponseList(List<Asset> assets);

}
