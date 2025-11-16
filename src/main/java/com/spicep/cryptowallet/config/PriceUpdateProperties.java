package com.spicep.cryptowallet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wallet.price-update")
@Data
public class PriceUpdateProperties {

    //Enable/disable scheduled price updates
    private boolean enable = true;

    //Interval between price update runs (in milliseconds)
    private long interval = 60000;

    //Maximum number of concurrent threads for price updates
    private int maxThreads = 3;
}
