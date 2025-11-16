package com.spicep.cryptowallet.client;

import com.spicep.cryptowallet.config.CoinCapClientConfig;
import com.spicep.cryptowallet.dto.coincap.CoinCapAssetResponse;
import com.spicep.cryptowallet.dto.coincap.CoinCapAssetsResponse;
import com.spicep.cryptowallet.dto.coincap.CoinCapHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "coincap-client",
        url = "${coincap.api.base-url}",
        configuration = CoinCapClientConfig.class
)
public interface CoinCapClient {

    /**
     * Get asset details by symbol/id
     * @param id Asset symbol
     * @return Asset details including current price
     */
    @GetMapping("/assets/{id}")
    CoinCapAssetResponse getAsset(@PathVariable("id") String id);

    /**
     * Get historical price data for an asset
     * @param id Asset symbol
     * @param interval Data interval
     * @param start Start timestamp in milliseconds
     * @param end End timestamp in milliseconds
     * @return Historical price data
     */
    @GetMapping("/assets/{id}/history")
    CoinCapHistoryResponse getAssetHistory(@PathVariable("id") String id, @RequestParam("interval") String interval,
            @RequestParam("start") Long start, @RequestParam("end") Long end
    );

    @GetMapping("/assets")
    CoinCapAssetsResponse getAssets(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset);
}
