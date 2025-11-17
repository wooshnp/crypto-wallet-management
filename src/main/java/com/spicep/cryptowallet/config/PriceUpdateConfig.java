package com.spicep.cryptowallet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class PriceUpdateConfig {

    @Value("${wallet.price-update.max-threads:3}")
    private int maxThreads;

    @Bean(name = "priceUpdateExecutor")
    public Executor priceUpdateExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxThreads);
        executor.setMaxPoolSize(maxThreads);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("price-update-");
        executor.initialize();

        log.info("Initialized price update executor with max {} threads", maxThreads);
        return executor;
    }

}
