package com.agro.trace.blockchain.service;

import java.math.BigInteger;

/**
 * Blockchain Integration Boundary.
 *
 * Method names/signatures below are a 1:1 mirror of the deployed
 * SupplyChain.sol contract's external functions. Do not rename these
 * independently of the contract - if the contract changes, this interface
 * (and both implementations) must change with it.
 *
 * Contract: SupplyChain.sol
 * Network: Polygon Amoy Testnet
 * Deployed address: 0x052dDa611de283Bcb37C3BCC1c7d1067cF5B38d4
 */
public interface BlockchainGateway {

    /**
     * Mirrors: createRequest(string productName, uint256 quantity, string metadata) returns (uint256)
     */
    BlockchainTransactionReference createRequest(String productName, BigInteger quantity, String metadata);

    /**
     * Mirrors: deleteRequest(uint256 requestId)
     */
    BlockchainTransactionReference deleteRequest(BigInteger requestId);

    /**
     * Mirrors: transferOwnership(uint256 requestId, uint256 productId, address newOwner) returns (uint256)
     * Pass productId = 0 to convert an accepted request into a Product (first transfer).
     * Pass productId != 0 to move an existing Product to a new owner.
     */
    BlockchainTransactionReference transferOwnership(BigInteger requestId, BigInteger productId, String newOwner);

    /**
     * Mirrors: recordAuthorityInspection(uint256 productId, string result, string metadata) returns (uint256)
     */
    BlockchainTransactionReference recordAuthorityInspection(BigInteger productId, String result, String metadata);

    /**
     * Mirrors: recordManufacturerInspection(uint256 productId, string result, string metadata) returns (uint256)
     */
    BlockchainTransactionReference recordManufacturerInspection(BigInteger productId, String result, String metadata);

    /**
     * Mirrors: recallProduct(uint256 productId, string reason) returns (uint256)
     */
    BlockchainTransactionReference recallProduct(BigInteger productId, String reason);

    /**
     * Mirrors: getRequest(uint256 requestId) view returns (...)
     * Returns raw decoded values in contract field order, or null if the call fails.
     */
    Object[] getRequest(BigInteger requestId);

    /**
     * Mirrors: getProduct(uint256 productId) view returns (...)
     */
    Object[] getProduct(BigInteger productId);

    /**
     * Mirrors: getProductOwner(uint256 productId) view returns (address)
     */
    String getProductOwner(BigInteger productId);
}

record BlockchainTransactionReference(
        String transactionHash,
        String blockNumber,
        String status,
        String network
) {}
