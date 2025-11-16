package com.spicep.cryptowallet.exception;

public class WalletAlreadyExistsException extends RuntimeException {

    private WalletAlreadyExistsException(String message) {
        super(message);
    }

    public static WalletAlreadyExistsException alreadyExists(String email) {
        return new WalletAlreadyExistsException(
                "Wallet already exists for email %s".formatted(email));
    }
}
