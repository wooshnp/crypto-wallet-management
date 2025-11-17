package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.dto.request.SimulatePortfolioAssetInput;
import com.spicep.cryptowallet.dto.request.SimulatePortfolioRequest;
import com.spicep.cryptowallet.exception.SimulationValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private CoinCapService coinCapService;

    private SimulationService simulationService;

    @BeforeEach
    void setup() {
        simulationService = new SimulationService(coinCapService);
    }

    @Test
    @DisplayName("Simulator rejects future dates")
    void simulatePortfolio_throwsOnFutureDate() {
        var request = new SimulatePortfolioRequest(LocalDate.now().plusDays(1),
                List.of(new SimulatePortfolioAssetInput("BTC", BigDecimal.ONE, BigDecimal.TEN)));

        assertThatThrownBy(() -> simulationService.simulatePortfolio(request))
                .isInstanceOf(SimulationValidationException.class);
    }

    @Test
    @DisplayName("Simulator uses user-provided value as baseline when market price flag is false")
    void simulatePortfolio_withUserValueBaseline() {
        ReflectionTestUtils.setField(simulationService, "useMarketPrice", false);

        var request = new SimulatePortfolioRequest(LocalDate.now(),
                List.of(new SimulatePortfolioAssetInput("BTC", BigDecimal.ONE, new BigDecimal("100"))));

        when(coinCapService.getCurrentPrice(anyString())).thenReturn(new BigDecimal("150"));

        var result = simulationService.simulatePortfolio(request);

        assertThat(result.total()).isEqualByComparingTo("150.00");
        assertThat(result.bestPerformance()).isEqualByComparingTo("50.00");
        assertThat(result.worstPerformance()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Simulator uses historical price as baseline when market price flag is true")
    void simulatePortfolio_withMarketPriceBaseline() {
        ReflectionTestUtils.setField(simulationService, "useMarketPrice", true);

        var date = LocalDate.of(2020, 1, 1);
        var request = new SimulatePortfolioRequest(date,
                List.of(new SimulatePortfolioAssetInput("ETH", BigDecimal.ONE, null)));

        when(coinCapService.getHistoricalPrice("ETH", date)).thenReturn(new BigDecimal("50"));
        when(coinCapService.getCurrentPrice("ETH")).thenReturn(new BigDecimal("100"));

        var result = simulationService.simulatePortfolio(request);

        assertThat(result.total()).isEqualByComparingTo("100.00");
        assertThat(result.bestPerformance()).isEqualByComparingTo("100.00");
        assertThat(result.worstPerformance()).isEqualByComparingTo("100.00");
    }
}
