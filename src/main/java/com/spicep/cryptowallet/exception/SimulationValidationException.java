package com.spicep.cryptowallet.exception;

public class SimulationValidationException extends RuntimeException {

    public SimulationValidationException(String message) {
        super(message);
    }

    public static SimulationValidationException valueProvidedWhenUsingMarketPrice() {
        return new SimulationValidationException(
            "Cannot provide value when simulation is configured to use market prices. " +
            "Either disable it or remove the value field from your request."
        );
    }

    public static SimulationValidationException valueMissingWhenNotUsingMarketPrice() {
        return new SimulationValidationException(
            "Must provide value when simulation is configured for manual pricing. " +
            "Either enable it or provide the value field in your request."
        );
    }

    public static SimulationValidationException futureDateNotAllowed() {
        return new SimulationValidationException(
            "Simulation date cannot be in the future. Please provide a date that is today or in the past."
        );
    }
}
