package com.spicep.cryptowallet.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    private WalletNotFoundException(String message) {
        super(message);
    }

    public static WalletNotFoundException notFound(UUID walletId) {
        return new WalletNotFoundException("Wallet %s was not found".formatted(walletId));
    }
}
