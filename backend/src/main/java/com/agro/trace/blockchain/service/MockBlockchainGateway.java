package com.agro.trace.blockchain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Mock blockchain gateway for SIH prototype.
 * In production, replace with actual Polygon Amoy Testnet integration.
 */
@Service
@ConditionalOnProperty(name = "app.blockchain.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class MockBlockchainGateway implements BlockchainGateway {

    @Override
    public BlockchainTransactionReference recordLotCreated(String lotId, String farmerRef, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordLotCreated: lot={}, farmer={}, hash={}", lotId, farmerRef, dataHash);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordCustodyTransfer(String objectId, String fromCustodian, String toCustodian, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordCustodyTransfer: object={}, from={}, to={}", objectId, fromCustodian, toCustodian);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordTestHash(String testRecordId, String testResult, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordTestHash: test={}, result={}", testRecordId, testResult);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordLotMerged(String manufacturerLotId, String[] inputLotIds, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordLotMerged: mfgLot={}, inputs={}", manufacturerLotId, String.join(",", inputLotIds));
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordBundleCreated(String bundleId, String manufacturerLotId, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordBundleCreated: bundle={}, mfgLot={}", bundleId, manufacturerLotId);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordRetailerReceipt(String bundleId, String retailerRef, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordRetailerReceipt: bundle={}, retailer={}", bundleId, retailerRef);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordQuarantine(String entityId, String reason, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordQuarantine: entity={}, reason={}", entityId, reason);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordFlag(String flagId, String flagType, String dataHash) {
        log.info("[BLOCKCHAIN MOCK] recordFlag: flag={}, type={}", flagId, flagType);
        return mockTxRef();
    }

    @Override
    public boolean verifyIntegrity(String dataHash, String blockchainTxHash) {
        log.info("[BLOCKCHAIN MOCK] verifyIntegrity: hash={}, tx={} -> true", dataHash, blockchainTxHash);
        return true; // Mock: always passes
    }

    private BlockchainTransactionReference mockTxRef() {
        return new BlockchainTransactionReference(
                "0x" + UUID.randomUUID().toString().replace("-", ""),
                "12345678",
                "CONFIRMED",
                "POLYGON_AMOY_TESTNET"
        );
    }
}