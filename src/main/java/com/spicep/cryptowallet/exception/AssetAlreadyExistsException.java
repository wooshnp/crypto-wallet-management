package com.spicep.cryptowallet.exception;

import java.util.UUID;

public class AssetAlreadyExistsException extends RuntimeException {

    private AssetAlreadyExistsException(String message) {
        super(message);
    }

    public static AssetAlreadyExistsException forWallet(String symbol, UUID walletId) {
        return new AssetAlreadyExistsException(
                "Asset %s already exists in wallet %s".formatted(symbol, walletId)
        );
    }
}
