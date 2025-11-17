package com.spicep.cryptowallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spicep.cryptowallet.dto.request.AddAssetRequest;
import com.spicep.cryptowallet.dto.request.CreateWalletRequest;
import com.spicep.cryptowallet.dto.request.UpdateAssetRequest;
import com.spicep.cryptowallet.dto.response.AssetResponse;
import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@WebMvcTest(WalletController.class)
@ContextConfiguration(classes = {WalletController.class})
class WalletControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Test
    @DisplayName("Creates a wallet successfully")
    void createWallet_validRequest_returnsCreated() throws Exception {
        var walletId = UUID.randomUUID().toString();
        var request = new CreateWalletRequest("nuno@example.com");
        var response = new WalletResponse(walletId, "nuno@example.com", BigDecimal.ZERO, List.of());

        when(walletService.createWallet(any(CreateWalletRequest.class))).thenReturn(response);

        var result = mockMvc.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        
        assertThat(result).hasStatus(201);
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(walletId);
        assertThat(result).bodyJson().extractingPath("$.email").asString().isEqualTo("nuno@example.com");
        assertThat(result).bodyJson().extractingPath("$.total").asNumber().isEqualTo(0);
        assertThat(result).bodyJson().extractingPath("$.assets").asArray().isEmpty();
    }

    @Test
    @DisplayName("Validation fails with invalid email")
    void createWallet_invalidEmail_returnsBadRequest() throws Exception {
        var request = new CreateWalletRequest("invalid-email.com");

        var result = mockMvc.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        
        assertThat(result).hasStatus(400);
    }

    @Test
    @DisplayName("Returns wallet successfully")
    void getWallet_existingWallet_returnsOk() {
        var walletId = UUID.randomUUID();
        var asset = new AssetResponse(
                "BTC",
                new BigDecimal("1.0"),
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                new BigDecimal("110.00")
        );
        var response = new WalletResponse(
                walletId.toString(),
                "nuno@example.com",
                new BigDecimal("110.00"),
                List.of(asset)
        );

        when(walletService.getWallet(walletId)).thenReturn(response);
        
        var result = mockMvc.get().uri("/api/v1/wallets/{id}", walletId);
        
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(walletId.toString());
        assertThat(result).bodyJson().extractingPath("$.email").asString().isEqualTo("nuno@example.com");
        assertThat(result).bodyJson().extractingPath("$.total").asNumber().isEqualTo(110.00);
        assertThat(result).bodyJson().extractingPath("$.assets").asArray().hasSize(1);
        assertThat(result).bodyJson().extractingPath("$.assets[0].symbol").asString().isEqualTo("BTC");
        assertThat(result).bodyJson().extractingPath("$.assets[0].quantity").asNumber().isEqualTo(1.0);
        assertThat(result).bodyJson().extractingPath("$.assets[0].price").asNumber().isEqualTo(110.00);
    }

    @Test
    @DisplayName("Adds asset successfully")
    void addAsset_validRequest_returnsCreated() throws Exception {
        var walletId = UUID.randomUUID();
        var request = new AddAssetRequest("BTC", new BigDecimal("1.0"), new BigDecimal("100.00"));
        var asset = new AssetResponse(
                "BTC",
                new BigDecimal("1.0"),
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                new BigDecimal("110.00")
        );
        var response = new WalletResponse(
                walletId.toString(),
                "nuno@example.com",
                new BigDecimal("110.00"),
                List.of(asset)
        );

        when(walletService.addAsset(eq(walletId), any(AddAssetRequest.class))).thenReturn(response);

        var result = mockMvc.post()
                .uri("/api/v1/wallets/{id}/assets", walletId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        assertThat(result).hasStatus(201);
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(walletId.toString());
        assertThat(result).bodyJson().extractingPath("$.assets[0].symbol").asString().isEqualTo("BTC");
    }

    @Test
    @DisplayName("Updates asset successfully")
    void updateAsset_validRequest_returnsOk() throws Exception {
        var walletId = UUID.randomUUID();
        var assetId = UUID.randomUUID();
        var request = new UpdateAssetRequest(new BigDecimal("2.0"));
        var asset = new AssetResponse(
                "BTC",
                new BigDecimal("2.0"),
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                new BigDecimal("220.00")
        );
        var response = new WalletResponse(
                walletId.toString(),
                "nuno@example.com",
                new BigDecimal("220.00"),
                List.of(asset)
        );

        when(walletService.updateAsset(eq(walletId), eq(assetId), any(UpdateAssetRequest.class)))
                .thenReturn(response);

        var result = mockMvc.put()
                .uri("/api/v1/wallets/{walletId}/assets/{assetId}", walletId, assetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").asString().isEqualTo(walletId.toString());
        assertThat(result).bodyJson().extractingPath("$.total").asNumber().isEqualTo(220.00);
        assertThat(result).bodyJson().extractingPath("$.assets[0].quantity").asNumber().isEqualTo(2.0);
    }
}
