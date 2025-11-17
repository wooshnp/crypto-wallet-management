package com.spicep.cryptowallet.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoinCapClientConfig {

    @Value("${coincap.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (apiKey != null && !apiKey.isEmpty()) {
                requestTemplate.header("Authorization", "Bearer " + apiKey);
            }
        };
    }
}
