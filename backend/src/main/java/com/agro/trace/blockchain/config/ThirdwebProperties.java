package com.agro.trace.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds app.blockchain.thirdweb.* from application.yml / environment variables.
 */
@Configuration
@ConfigurationProperties(prefix = "app.blockchain.thirdweb")
@Data
public class ThirdwebProperties {

    /** Project secret key from the thirdweb dashboard. Backend-only, never expose. */
    private String secretKey;

    /** Server (backend) wallet address created under Transactions -> Server wallets. */
    private String walletAddress;

    /** Deployed SupplyChain.sol contract address. */
    private String contractAddress;

    /** Polygon Amoy testnet chainId. */
    private long chainId = 80002L;

    /** Base URL for the thirdweb API. */
    private String apiBaseUrl = "https://api.thirdweb.com/v1";

    /** How often to poll a submitted transaction for confirmation. */
    private long pollIntervalMs = 1500L;

    /** Max time to wait for a transaction to confirm before giving up (still returns PENDING, not an error). */
    private long pollTimeoutMs = 30000L;
}
