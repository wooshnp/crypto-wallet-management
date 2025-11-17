package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.client.CoinCapClient;
import com.spicep.cryptowallet.dto.coincap.CoinCapAsset;
import com.spicep.cryptowallet.dto.coincap.CoinCapAssetResponse;
import com.spicep.cryptowallet.dto.coincap.CoinCapHistoryData;
import com.spicep.cryptowallet.dto.coincap.CoinCapHistoryResponse;
import com.spicep.cryptowallet.exception.AssetNotFoundException;
import com.spicep.cryptowallet.exception.CoinCapApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import feign.Request;
import feign.Response;
import feign.Util;
import feign.FeignException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinCapServiceTest {

    @Mock
    private CoinCapClient coinCapClient;
    @Mock
    private CoinCapResolver coinCapResolver;

    private CoinCapService service;

    @Test
    @DisplayName("When validating an asset it returns CoinCap price for symbol")
    void validateAssetPrice_success() {
        service = new CoinCapService(coinCapClient, coinCapResolver);
        when(coinCapResolver.resolveCoinCapId("btc")).thenReturn("bitcoin");
        var asset = new CoinCapAsset("bitcoin", "1", "BTC", "Bitcoin", "100.00", null, null, null, null, null, null);
        when(coinCapClient.getAsset("bitcoin")).thenReturn(new CoinCapAssetResponse(asset, 0L));

        var price = service.validateAssetPrice("btc", BigDecimal.ZERO);

        assertThat(price).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("When validating an asset it throws AssetNotFound when CoinCap returns 404")
    void validateAssetPrice_notFound() {
        service = new CoinCapService(coinCapClient, coinCapResolver);
        when(coinCapResolver.resolveCoinCapId("bad")).thenReturn("bad");
        var request = Request.create(Request.HttpMethod.GET, "https://api.coincap.io/v2/assets/bad",
                Map.of(), null, StandardCharsets.UTF_8, null);
        var notFound = FeignException.errorStatus("getAsset", Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(Map.of())
                .build());
        when(coinCapClient.getAsset("bad")).thenThrow(notFound);

        assertThatThrownBy(() -> service.validateAssetPrice("bad", null))
                .isInstanceOf(AssetNotFoundException.class);
    }

    @Test
    @DisplayName("When validating an asset it wraps other Feign errors as CoinCapApiException")
    void validateAssetPrice_apiError() {
        service = new CoinCapService(coinCapClient, coinCapResolver);
        when(coinCapResolver.resolveCoinCapId("btc")).thenReturn("btc");
        var request = Request.create(Request.HttpMethod.GET, "https://api.coincap.io/v2/assets/btc",
                Map.of(), null, StandardCharsets.UTF_8, null);
        var error = FeignException.errorStatus("getAsset", Response.builder()
                .status(500)
                .reason("error")
                .request(request)
                .headers(Map.of())
                .build());
        when(coinCapClient.getAsset("btc")).thenThrow(error);

        assertThatThrownBy(() -> service.validateAssetPrice("btc", null))
                .isInstanceOf(CoinCapApiException.class);
    }

    @Test
    @DisplayName("When fetching asset historical price it returns first price from history list")
    void getHistoricalPrice_success() {
        service = new CoinCapService(coinCapClient, coinCapResolver);
        when(coinCapResolver.resolveCoinCapId("eth")).thenReturn("ethereum");
        var date = LocalDate.ofEpochDay(0);
        var startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        var endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        var history = new CoinCapHistoryResponse(List.of(new CoinCapHistoryData("200.00", 0L, "2020-01-01")), 0L);
        when(coinCapClient.getAssetHistory("ethereum", "d1", startOfDay, endOfDay)).thenReturn(history);

        var price = service.getHistoricalPrice("eth", date);

        assertThat(price).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("When fetching asset historical price throws when history is empty")
    void getHistoricalPrice_noData() {
        service = new CoinCapService(coinCapClient, coinCapResolver);
        when(coinCapResolver.resolveCoinCapId("eth")).thenReturn("ethereum");
        var date = LocalDate.ofEpochDay(0);
        var startOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        var endOfDay = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        var history = new CoinCapHistoryResponse(List.of(), 0L);
        when(coinCapClient.getAssetHistory("ethereum", "d1", startOfDay, endOfDay)).thenReturn(history);

        assertThatThrownBy(() -> service.getHistoricalPrice("eth", date))
                .isInstanceOf(AssetNotFoundException.class);
    }
}
