package com.spicep.cryptowallet.exception;

import java.time.LocalDate;

public class CoinCapApiException extends RuntimeException {

    private CoinCapApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CoinCapApiException fetchPriceFailed(String symbol, Throwable cause) {
        return new CoinCapApiException(
                "Failed to fetch price for %s".formatted(symbol),
                cause);
    }

    public static CoinCapApiException fetchHistoricalPriceFailed(String symbol, LocalDate date, Throwable cause) {
        return new CoinCapApiException(
                "Failed to fetch historical price for %s on %s".formatted(symbol, date),
                cause);
    }
}
