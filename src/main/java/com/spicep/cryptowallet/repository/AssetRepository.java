package com.spicep.cryptowallet.repository;

import com.spicep.cryptowallet.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {

    @Query("SELECT a FROM Asset a WHERE UPPER(a.symbol) = UPPER(:symbol)")
    List<Asset> findBySymbolIgnoreCase(String symbol);

    @Query("SELECT DISTINCT a.symbol FROM Asset a")
    List<String> findDistinctSymbols();

    @Query("SELECT a FROM Asset a WHERE a.wallet.id = :walletId AND UPPER(a.symbol) = UPPER(:symbol)")
    List<Asset> findByWalletIdAndSymbol(UUID walletId, String symbol);
}
