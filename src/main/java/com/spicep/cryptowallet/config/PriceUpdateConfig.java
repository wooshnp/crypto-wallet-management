package com.spicep.cryptowallet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class PriceUpdateConfig {

    private final PriceUpdateProperties properties;

    @Bean(name = "priceUpdateExecutor")
    public Executor priceUpdateExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getMaxThreads());
        executor.setMaxPoolSize(properties.getMaxThreads());
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("price-update-");
        executor.initialize();

        log.info("Initialized price update executor with max {} threads", properties.getMaxThreads());
        return executor;
    }

}
