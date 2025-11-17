package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.client.CoinCapClient;
import com.spicep.cryptowallet.dto.coincap.CoinCapAsset;
import com.spicep.cryptowallet.dto.coincap.CoinCapAssetsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private CoinCapClient coinCapClient;

    @Test
    @DisplayName("getAvailableSymbols returns distinct, sorted symbols from CoinCap")
    void getAvailableSymbols_returnsDistinctSortedSymbols() {
        var response = new CoinCapAssetsResponse(
                List.of(
                        new CoinCapAsset("bitcoin", "1", "BTC", "Bitcoin", "0", null, null, null, null, null, null),
                        new CoinCapAsset("cardano", "2", "ADA", "Cardano", "0", null, null, null, null, null, null),
                        new CoinCapAsset("another", "3", "BTC", "Dup", "0", null, null, null, null, null, null)
                ),
                0L
        );
        when(coinCapClient.getAssets(anyString(), anyInt(), anyInt())).thenReturn(response);

        var service = new AssetService(coinCapClient);
        var symbols = service.getAvailableSymbols();

        assertThat(symbols).containsExactly("ADA", "BTC");
    }

    @Test
    @DisplayName("getAvailableAssets returns assets from CoinCap with given search/limit/offset")
    void getAvailableAssets_returnsClientData() {
        var response = new CoinCapAssetsResponse(
                List.of(new CoinCapAsset("bitcoin", "1", "BTC", "Bitcoin", "100", null, null, null, null, null, null)),
                0L
        );
        when(coinCapClient.getAssets("btc", 10, 0)).thenReturn(response);

        var service = new AssetService(coinCapClient);
        var assets = service.getAvailableAssets("btc", 10, 0);

        assertThat(assets).hasSize(1);
        assertThat(assets.getFirst().symbol()).isEqualTo("BTC");
    }
}
