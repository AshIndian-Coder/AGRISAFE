package com.agro.trace.blockchain.service;

/**
 * Blockchain Integration Boundary.
 * 
 * This interface defines the contract for blockchain integration.
 * In the SIH prototype, this is a mock/stub implementation.
 * In production, this would connect to Polygon Amoy Testnet via Web3 libraries.
 * 
 * DO NOT implement actual blockchain calls - only the integration boundary.
 */
public interface BlockchainGateway {

    /**
     * Record a lot creation event on-chain.
     */
    BlockchainTransactionReference recordLotCreated(String lotId, String farmerRef, String dataHash);

    /**
     * Record a custody transfer event on-chain.
     */
    BlockchainTransactionReference recordCustodyTransfer(String objectId, String fromCustodian, String toCustodian, String dataHash);

    /**
     * Record a test result hash on-chain for integrity.
     */
    BlockchainTransactionReference recordTestHash(String testRecordId, String testResult, String dataHash);

    /**
     * Record a lot merge event (manufacturing).
     */
    BlockchainTransactionReference recordLotMerged(String manufacturerLotId, String[] inputLotIds, String dataHash);

    /**
     * Record a bundle creation event.
     */
    BlockchainTransactionReference recordBundleCreated(String bundleId, String manufacturerLotId, String dataHash);

    /**
     * Record a retailer receipt event.
     */
    BlockchainTransactionReference recordRetailerReceipt(String bundleId, String retailerRef, String dataHash);

    /**
     * Record a quarantine action.
     */
    BlockchainTransactionReference recordQuarantine(String entityId, String reason, String dataHash);

    /**
     * Record a flag/investigation anchor.
     */
    BlockchainTransactionReference recordFlag(String flagId, String flagType, String dataHash);

    /**
     * Verify integrity by comparing on-chain hash with off-chain data.
     */
    boolean verifyIntegrity(String dataHash, String blockchainTxHash);
}

record BlockchainTransactionReference(
        String transactionHash,
        String blockNumber,
        String status,
        String network
) {}