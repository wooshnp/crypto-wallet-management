package com.spicep.cryptowallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cryptoWalletAPI() {
        return new OpenAPI().info(new Info()
                .title("Crypto Wallet Management API")
                .description("API for managing crypto wallets and simulations")
                .version("v1"));
    }
}
