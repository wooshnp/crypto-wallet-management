package com.spicep.cryptowallet.exception;

import java.time.LocalDate;

public class AssetNotFoundException extends RuntimeException {

    private AssetNotFoundException(String message) {
        super(message);
    }

    private AssetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AssetNotFoundException notFoundOnCoinCap(String symbol) {
        return new AssetNotFoundException("Asset not found on CoinCap: %s".formatted(symbol));
    }

    public static AssetNotFoundException invalidResponse(String symbol) {
        return new AssetNotFoundException("Asset not found: %s".formatted(symbol));
    }

    public static AssetNotFoundException noHistoricalData(String symbol, LocalDate date) {
        return new AssetNotFoundException(
                "No historical price data found for %s on %s".formatted(symbol, date));
    }

    public static AssetNotFoundException withCause(String symbol, LocalDate date, Throwable cause) {
        return new AssetNotFoundException(
                "Asset not found or no historical data for: %s on %s".formatted(symbol, date),
                cause);
    }
}
