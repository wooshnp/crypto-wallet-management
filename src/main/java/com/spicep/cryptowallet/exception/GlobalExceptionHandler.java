package com.spicep.cryptowallet.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ProblemDetail handleWalletAlreadyExists(WalletAlreadyExistsException ex) {
        log.warn("Wallet already exists: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
        problemDetail.setTitle("Wallet Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ProblemDetail handleWalletNotFound(WalletNotFoundException ex) {
        log.warn("Wallet not found: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setTitle("Wallet Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public ProblemDetail handleAssetNotFound(AssetNotFoundException ex) {
        log.warn("Asset not found: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
        problemDetail.setTitle("Asset Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AssetAlreadyExistsException.class)
    public ProblemDetail handleAssetAlreadyExists(AssetAlreadyExistsException ex) {
        log.warn("Asset already exists: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
        problemDetail.setTitle("Asset Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(CoinCapApiException.class)
    public ProblemDetail handleCoinCapApiException(CoinCapApiException ex) {
        log.error("CoinCap API error: {}", ex.getMessage(), ex);

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,ex.getMessage());
        problemDetail.setTitle("External API Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Validation failed: {}", errors);

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Validation failed");
        problemDetail.setTitle("Invalid Request");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(SimulationValidationException.class)
    public ProblemDetail handleSimulationValidation(SimulationValidationException ex) {
        log.warn("Simulation validation failed: {}", ex.getMessage());

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Simulation Request");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

     //handles all other exceptions (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        var problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
