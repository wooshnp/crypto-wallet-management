package com.spicep.cryptowallet.controller;

import com.spicep.cryptowallet.dto.request.AddAssetRequest;
import com.spicep.cryptowallet.dto.request.CreateWalletRequest;
import com.spicep.cryptowallet.dto.request.UpdateAssetRequest;
import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Wallet Management", description = "Endpoints for managing cryptocurrency wallets and assets")
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Create a new wallet", description = "Creates a new wallet for a user with a unique email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wallet created sucessfully"),
            @ApiResponse(responseCode = "409", description = "Wallet already exists for this email")
    })
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(input));
    }

    @Operation(summary = "Get wallet", description = "Returns wallet by id with assets and totals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getWallet(walletId));
    }

    @Operation(summary = "Add asset", description = "Adds an asset to a wallet validating price on CoinCap")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset added"),
            @ApiResponse(responseCode = "404", description = "Wallet or asset not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/{walletId}/assets")
    public ResponseEntity<WalletResponse> addAsset(@PathVariable UUID walletId,
                                                   @Valid @RequestBody AddAssetRequest input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.addAsset(walletId, input));
    }

    @Operation(
            summary = "Update asset quantity",
            description = "Updates the quantity of an existing asset in the wallet. " +
                    "Also refreshes the asset's price from CoinCap API to reflect current market value."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset updated successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet or asset not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{walletId}/assets/{assetId}")
    public ResponseEntity<WalletResponse> updateAsset(@PathVariable UUID walletId, @PathVariable UUID assetId,
                                                      @Valid @RequestBody UpdateAssetRequest input) {
        return ResponseEntity.ok(walletService.updateAsset(walletId, assetId, input));
    }

}
