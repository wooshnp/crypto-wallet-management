package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.dto.request.AddAssetRequest;
import com.spicep.cryptowallet.dto.request.CreateWalletRequest;
import com.spicep.cryptowallet.dto.request.UpdateAssetRequest;
import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.entity.Asset;
import com.spicep.cryptowallet.entity.PriceHistory;
import com.spicep.cryptowallet.entity.User;
import com.spicep.cryptowallet.entity.Wallet;
import com.spicep.cryptowallet.exception.AssetAlreadyExistsException;
import com.spicep.cryptowallet.exception.AssetNotFoundException;
import com.spicep.cryptowallet.exception.WalletAlreadyExistsException;
import com.spicep.cryptowallet.exception.WalletNotFoundException;
import com.spicep.cryptowallet.mapper.WalletMapper;
import com.spicep.cryptowallet.repository.AssetRepository;
import com.spicep.cryptowallet.repository.PriceHistoryRepository;
import com.spicep.cryptowallet.repository.UserRepository;
import com.spicep.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    private final WalletMapper walletMapper;

    private final CoinCapService coinCapService;

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest input) {
        if (userRepository.existsByEmail(input.email())) {
            throw WalletAlreadyExistsException.alreadyExists(input.email());
        }

        var user = User.builder().email(input.email()).build();
        var wallet = Wallet.builder().user(user).build();
        user.setWallet(wallet);

        var savedUser = userRepository.save(user);

        return walletMapper.toResponse(savedUser.getWallet());
    }

    public WalletResponse getWallet(UUID walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> WalletNotFoundException.notFound(walletId));
        return walletMapper.toResponse(wallet);
    }

    @Transactional
    public WalletResponse addAsset(UUID walletId, AddAssetRequest input) {
        var wallet = walletRepository.findById(walletId).orElseThrow(() -> WalletNotFoundException.notFound(walletId));

        var symbolUpper = input.symbol().toUpperCase();
        var currentPrice = coinCapService.validateAssetPrice(input.symbol(), input.price());

        // Record price in history
        var priceHistory = PriceHistory.create(symbolUpper, currentPrice);
        priceHistoryRepository.save(priceHistory);

        // Check if asset with this symbol already exists in wallet
        var existingAssets = assetRepository.findByWalletIdAndSymbol(walletId, symbolUpper);

        if (!existingAssets.isEmpty()) {
            throw AssetAlreadyExistsException.forWallet(symbolUpper, walletId);
        } else {
            var asset = Asset.builder().symbol(symbolUpper).quantity(input.quantity()).acquisitionPrice(input.price())
                    .currentPrice(currentPrice).build();
            wallet.addAsset(asset);
            walletRepository.save(wallet);

            log.info("Added new asset {} to wallet {}: quantity {}", symbolUpper, walletId, input.quantity());
        }

        // Refresh wallet to get updated totals
        var updatedWallet = walletRepository.findById(walletId)
                .orElseThrow(() -> WalletNotFoundException.notFound(walletId));

        return walletMapper.toResponse(updatedWallet);
    }

    @Transactional
    public WalletResponse updateAsset(UUID walletId, UUID assetId, UpdateAssetRequest input) {
        var wallet = walletRepository.findById(walletId).orElseThrow(() -> WalletNotFoundException.notFound(walletId));

        var asset = assetRepository.findById(assetId).orElseThrow(() -> AssetNotFoundException.notFound(assetId));

        // Verify asset belongs to this wallet
        if (!asset.getWallet().getId().equals(walletId)) {
            throw AssetNotFoundException.notFoundInWallet(assetId, walletId);
        }

        var currentPrice = coinCapService.getCurrentPrice(asset.getSymbol());

        // Record price in history
        var priceHistory = PriceHistory.create(asset.getSymbol(), currentPrice);
        priceHistoryRepository.save(priceHistory);

        // Update asset
        asset.setQuantity(input.quantity());
        asset.updatePrice(currentPrice);
        assetRepository.save(asset);

        log.info("Updated asset {} in wallet {}: new quantity {}", asset.getSymbol(), walletId, input.quantity());

        var updatedWallet = walletRepository.findById(walletId)
                .orElseThrow(() -> WalletNotFoundException.notFound(walletId));

        return walletMapper.toResponse(updatedWallet);
    }

}
