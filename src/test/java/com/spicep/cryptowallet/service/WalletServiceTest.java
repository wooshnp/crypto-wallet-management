package com.spicep.cryptowallet.service;

import com.spicep.cryptowallet.dto.request.AddAssetRequest;
import com.spicep.cryptowallet.dto.request.CreateWalletRequest;
import com.spicep.cryptowallet.dto.response.WalletResponse;
import com.spicep.cryptowallet.entity.User;
import com.spicep.cryptowallet.entity.Wallet;
import com.spicep.cryptowallet.exception.AssetNotFoundException;
import com.spicep.cryptowallet.exception.WalletAlreadyExistsException;
import com.spicep.cryptowallet.exception.WalletNotFoundException;
import com.spicep.cryptowallet.mapper.WalletMapper;
import com.spicep.cryptowallet.repository.AssetRepository;
import com.spicep.cryptowallet.repository.PriceHistoryRepository;
import com.spicep.cryptowallet.repository.UserRepository;
import com.spicep.cryptowallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private PriceHistoryRepository priceHistoryRepository;
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private CoinCapService coinCapService;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(userRepository, walletRepository, assetRepository, priceHistoryRepository,
                walletMapper, coinCapService);
    }

    @Test
    @DisplayName("When creating wallet it should throw when email already exists")
    void createWallet_throwsWhenEmailExists() {
        when(userRepository.existsByEmail("nuno@example.com")).thenReturn(true);

        assertThatThrownBy(() -> walletService.createWallet(new CreateWalletRequest("nuno@example.com")))
                .isInstanceOf(WalletAlreadyExistsException.class);
    }

    @Test
    @DisplayName("When adding a new asset it should set acquisition price from user and current price from CoinCap")
    void addAsset_setsAcquisitionAndCurrentPrice() {
        var walletId = UUID.randomUUID();
        var wallet = Wallet.builder().id(walletId).user(User.builder().id(UUID.randomUUID()).email("nuno@example.com").build()).build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Collections.emptyList());
        when(coinCapService.validateAssetPrice("btc", new BigDecimal("100.00"))).thenReturn(new BigDecimal("110.00"));

        var captor = ArgumentCaptor.forClass(Wallet.class);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletMapper.toResponse(any())).thenReturn(new WalletResponse(walletId.toString(), "nuno@example.com", BigDecimal.ZERO, Collections.emptyList()));

        walletService.addAsset(walletId, new AddAssetRequest("btc", new BigDecimal("1.5"), new BigDecimal("100.00")));

        verify(walletRepository).save(captor.capture());
        var savedWallet = captor.getValue();
        assertThat(savedWallet.getAssets()).hasSize(1);
        var asset = savedWallet.getAssets().getFirst();
        assertThat(asset.getAcquisitionPrice()).isEqualByComparingTo("100.00");
        assertThat(asset.getCurrentPrice()).isEqualByComparingTo("110.00");
    }

    @Test
    @DisplayName("When adding a new asset it should reject duplicate symbols in same wallet")
    void addAsset_throwsWhenAssetExists() {
        var walletId = UUID.randomUUID();
        var existingAssetId = UUID.randomUUID();
        var user = User.builder().id(UUID.randomUUID()).email("nuno@example.com").build();
        var wallet = Wallet.builder().id(walletId).user(user).build();

        var existingAsset = com.spicep.cryptowallet.entity.Asset.builder()
                .id(existingAssetId)
                .symbol("BTC")
                .quantity(new BigDecimal("1.0"))
                .acquisitionPrice(new BigDecimal("90.00"))
                .currentPrice(new BigDecimal("95.00"))
                .wallet(wallet)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findByWalletIdAndSymbol(walletId, "BTC")).thenReturn(Collections.singletonList(existingAsset));
        when(coinCapService.validateAssetPrice("btc", new BigDecimal("120.00"))).thenReturn(new BigDecimal("125.00"));

        assertThatThrownBy(() -> walletService.addAsset(walletId, new AddAssetRequest("btc", new BigDecimal("0.5"), new BigDecimal("120.00"))))
                .isInstanceOf(com.spicep.cryptowallet.exception.AssetAlreadyExistsException.class);
    }

    @Test
    @DisplayName("When getting a wallet it should throw when not found")
    void getWallet_throwsWhenNotFound() {
        var walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getWallet(walletId))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    @DisplayName("When getting a wallet it should map the response")
    void getWallet_returnsMappedResponse() {
        var walletId = UUID.randomUUID();
        var wallet = Wallet.builder().id(walletId).user(User.builder().email("nuno@example.com").build()).build();
        var expected = new WalletResponse(walletId.toString(), "nuno@example.com", BigDecimal.ZERO, Collections.emptyList());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletMapper.toResponse(eq(wallet))).thenReturn(expected);

        var result = walletService.getWallet(walletId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("When updating an asset it should refresh quantity and current price")
    void updateAsset_updatesQuantityAndPrice() {
        var walletId = UUID.randomUUID();
        var assetId = UUID.randomUUID();
        var wallet = Wallet.builder().id(walletId).user(User.builder().email("nuno@example.com").build()).build();
        var asset = com.spicep.cryptowallet.entity.Asset.builder()
                .id(assetId)
                .wallet(wallet)
                .symbol("BTC")
                .quantity(new BigDecimal("1.0"))
                .acquisitionPrice(new BigDecimal("80.00"))
                .currentPrice(new BigDecimal("90.00"))
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(coinCapService.getCurrentPrice("BTC")).thenReturn(new BigDecimal("120.00"));
        when(walletMapper.toResponse(wallet)).thenReturn(new WalletResponse(walletId.toString(), "nuno@example.com", BigDecimal.ZERO, Collections.emptyList()));

        walletService.updateAsset(walletId, assetId, new com.spicep.cryptowallet.dto.request.UpdateAssetRequest(new BigDecimal("2.5")));

        verify(assetRepository).save(asset);
        assertThat(asset.getQuantity()).isEqualByComparingTo("2.5");
        assertThat(asset.getCurrentPrice()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("When updating an asset it should throw if asset not in wallet")
    void updateAsset_throwsWhenAssetNotInWallet() {
        var walletId = UUID.randomUUID();
        var assetId = UUID.randomUUID();
        var wallet = Wallet.builder().id(walletId).user(User.builder().email("nuno@example.com").build()).build();
        var otherWallet = Wallet.builder().id(UUID.randomUUID()).user(User.builder().email("x@y.com").build()).build();
        var asset = com.spicep.cryptowallet.entity.Asset.builder()
                .id(assetId)
                .wallet(otherWallet)
                .symbol("BTC")
                .quantity(new BigDecimal("1.0"))
                .acquisitionPrice(new BigDecimal("80.00"))
                .currentPrice(new BigDecimal("90.00"))
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> walletService.updateAsset(walletId, assetId, new com.spicep.cryptowallet.dto.request.UpdateAssetRequest(new BigDecimal("2.5"))))
                .isInstanceOf(AssetNotFoundException.class);
    }
}
