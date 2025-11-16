package com.spicep.cryptowallet.controller;

import com.spicep.cryptowallet.dto.coincap.CoinCapAsset;
import com.spicep.cryptowallet.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Discovery", description = "Endpoints for discovering available cryptocurrencies from CoinCap")
public class AssetController {

    private final AssetService assetService;

    @Operation(
            summary = "Get available cryptocurrency symbols",
            description = "Returns a list of all available cryptocurrency symbols from CoinCap. "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved symbols list")
    })
    @GetMapping("/available/symbols")
    public ResponseEntity<List<String>> getAvailableSymbols() {
        return ResponseEntity.ok(assetService.getAvailableSymbols());
    }

    @Operation(
            summary = "Search available cryptocurrencies",
            description = "Search and browse available cryptocurrencies from CoinCap with filtering and pagination. "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved assets"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/available")
    public ResponseEntity<List<CoinCapAsset>> getAvailableAssets(@RequestParam(required = false) String search,
                                                                 @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(assetService.getAvailableAssets(search, limit));
    }
}
