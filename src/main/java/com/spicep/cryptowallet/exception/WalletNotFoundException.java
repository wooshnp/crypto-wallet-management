package com.spicep.cryptowallet.exception;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException(String email) {
        super("Wallet already exists for email %s".formatted(email));
    }
}
