package com.spicep.cryptowallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotNull
    @Column(nullable = false)
    private String symbol;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @NotNull
    @Positive
    @Column(name = "current_price", nullable = false, precision = 20, scale = 2)
    private BigDecimal currentPrice;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updatePrice(BigDecimal newPrice) {
        this.currentPrice = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the total value of this asset (quantity * current price)
     * @return Total value in USD
     */
    public BigDecimal getValue() {
        return quantity.multiply(currentPrice);
    }
}
