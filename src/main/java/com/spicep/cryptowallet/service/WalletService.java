package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.dto.request.AddAssetRequest;
import com.spicep.cryptowallet.dto.request.CreateWalletRequest;
import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.entity.User;
import com.spicep.cryptowallet.entity.Wallet;
import com.spicep.cryptowallet.exception.WalletAlreadyExistsException;
import com.spicep.cryptowallet.exception.WalletNotFoundException;
import com.spicep.cryptowallet.mapper.WalletMapper;
import com.spicep.cryptowallet.repository.UserRepository;
import com.spicep.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw WalletAlreadyExistsException.alreadyExists(input.getEmail());
        }

        var user = User.builder().email(input.getEmail()).build();
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

    public WalletResponse addAsset(UUID walletId, AddAssetRequest input) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> WalletNotFoundException.notFound(walletId));

//        var currentPrice;
        //todo: connect coinapiservice (almost done) to this implementation
        return walletMapper.toResponse(wallet);
    }

}
