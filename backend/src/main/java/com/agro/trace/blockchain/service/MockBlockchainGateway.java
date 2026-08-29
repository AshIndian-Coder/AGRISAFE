package com.agro.trace.blockchain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Mock blockchain gateway used when app.blockchain.enabled=false.
 * Mirrors SupplyChain.sol's function names 1:1 - see BlockchainGateway.
 */
@Service
@ConditionalOnProperty(name = "app.blockchain.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class MockBlockchainGateway implements BlockchainGateway {

    @Override
    public BlockchainTransactionReference createRequest(String productName, BigInteger quantity, String metadata) {
        log.info("[BLOCKCHAIN MOCK] createRequest: productName={}, quantity={}", productName, quantity);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference deleteRequest(BigInteger requestId) {
        log.info("[BLOCKCHAIN MOCK] deleteRequest: requestId={}", requestId);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference transferOwnership(BigInteger requestId, BigInteger productId, String newOwner) {
        log.info("[BLOCKCHAIN MOCK] transferOwnership: requestId={}, productId={}, newOwner={}", requestId, productId, newOwner);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordAuthorityInspection(BigInteger productId, String result, String metadata) {
        log.info("[BLOCKCHAIN MOCK] recordAuthorityInspection: productId={}, result={}", productId, result);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recordManufacturerInspection(BigInteger productId, String result, String metadata) {
        log.info("[BLOCKCHAIN MOCK] recordManufacturerInspection: productId={}, result={}", productId, result);
        return mockTxRef();
    }

    @Override
    public BlockchainTransactionReference recallProduct(BigInteger productId, String reason) {
        log.info("[BLOCKCHAIN MOCK] recallProduct: productId={}, reason={}", productId, reason);
        return mockTxRef();
    }

    @Override
    public Object[] getRequest(BigInteger requestId) {
        log.info("[BLOCKCHAIN MOCK] getRequest: requestId={}", requestId);
        return new Object[]{requestId, "0x0000000000000000000000000000000000000000", "MOCK", BigInteger.ZERO, "", false, BigInteger.ZERO, BigInteger.ZERO};
    }

    @Override
    public Object[] getProduct(BigInteger productId) {
        log.info("[BLOCKCHAIN MOCK] getProduct: productId={}", productId);
        return new Object[]{productId, "MOCK", BigInteger.ZERO, "0x0000000000000000000000000000000000000000", BigInteger.ZERO, false, true};
    }

    @Override
    public String getProductOwner(BigInteger productId) {
        log.info("[BLOCKCHAIN MOCK] getProductOwner: productId={}", productId);
        return "0x0000000000000000000000000000000000000000";
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
