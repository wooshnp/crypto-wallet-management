package com.spicep.cryptowallet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "price_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String symbol;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal price;

    @CreatedDate
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new price history entry with the current date
     * @param symbol Cryptocurrency symbol
     * @param price Price in USD
     * @return New PriceHistory instance
     */
    public static PriceHistory create(String symbol, BigDecimal price) {
        return PriceHistory.builder()
                .symbol(symbol)
                .price(price)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
