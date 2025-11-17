package com.spicep.cryptowallet.controller;

import com.spicep.cryptowallet.dto.request.SimulatePortfolioRequest;
import com.spicep.cryptowallet.dto.response.SimulationResponse;
import com.spicep.cryptowallet.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Portfolio Simulation", description = "Endpoints for simulating portfolio performance and analysis")
public class SimulationController {

    private final SimulationService simulationService;

    @Operation(
            summary = "Simulate portfolio performance",
            description = "Simulates portfolio performance from a past date to present. " +
                    "Calculates the current value, identifies best and worst performing assets with their " +
                    "percentage changes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulation completed successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found on CoinCap API"),
            @ApiResponse(responseCode = "400", description = "Invalid input or date"),
            @ApiResponse(responseCode = "502", description = "CoinCap API error")
    })
    @PostMapping("/portfolio")
    public ResponseEntity<SimulationResponse> simulatePortfolio(@Valid @RequestBody SimulatePortfolioRequest input) {
        return ResponseEntity.ok(simulationService.simulatePortfolio(input));
    }

}
