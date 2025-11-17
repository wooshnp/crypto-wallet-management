package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.entity.Asset;
import com.spicep.cryptowallet.repository.AssetRepository;
import com.spicep.cryptowallet.repository.PriceHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceUpdateServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private PriceHistoryRepository priceHistoryRepository;
    @Mock
    private CoinCapService coinCapService;

    private PriceUpdateService service;

    @Test
    @DisplayName("When updating prices it should fetch distinct symbols and update assets and history")
    void updatePrices_updatesAssets() {
        Executor executor = Runnable::run; // fast workaround to run threads synchronously due 2 lack of time :(
        service = new PriceUpdateService(assetRepository, priceHistoryRepository, coinCapService, executor);
        ReflectionTestUtils.setField(service, "priceUpdateEnabled", true);

        when(assetRepository.findDistinctSymbols()).thenReturn(List.of("BTC", "ETH"));

        var btc = Asset.builder().symbol("BTC").quantity(new BigDecimal("1")).currentPrice(new BigDecimal("90")).build();
        var eth = Asset.builder().symbol("ETH").quantity(new BigDecimal("2")).currentPrice(new BigDecimal("50")).build();
        when(assetRepository.findBySymbolIgnoreCase("BTC")).thenReturn(List.of(btc));
        when(assetRepository.findBySymbolIgnoreCase("ETH")).thenReturn(List.of(eth));
        when(coinCapService.getCurrentPrice("BTC")).thenReturn(new BigDecimal("100"));
        when(coinCapService.getCurrentPrice("ETH")).thenReturn(new BigDecimal("75"));

        service.updatePrices();

        verify(priceHistoryRepository, times(2)).save(any());
        verify(assetRepository, times(2)).saveAll(any());
        assertThat(btc.getCurrentPrice()).isEqualByComparingTo("100");
        assertThat(eth.getCurrentPrice()).isEqualByComparingTo("75");
    }

    @Test
    @DisplayName("When updating prices has flag set to false it should not start")
    void updatePrices_disabled() {
        Executor executor = Runnable::run;
        service = new PriceUpdateService(assetRepository, priceHistoryRepository, coinCapService, executor);
        ReflectionTestUtils.setField(service, "priceUpdateEnabled", false);

        service.updatePrices();

        verify(assetRepository, never()).findDistinctSymbols();
    }
}
