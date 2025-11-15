package com.spicep.cryptowallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CryptoWalletManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoWalletManagementApplication.class, args);
	}

}
