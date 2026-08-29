package com.agro.trace.blockchain.service;

import com.agro.trace.blockchain.config.ThirdwebProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Real blockchain gateway backed by the thirdweb Transactions API
 * (https://api.thirdweb.com/v1/contracts/write, /contracts/read, /transactions/{id}).
 *
 * Talks to the SupplyChain.sol contract deployed on Polygon Amoy at
 * app.blockchain.thirdweb.contract-address, signing/sending via a thirdweb
 * server (backend) wallet identified by the project secret key.
 *
 * Enabled with: app.blockchain.enabled=true
 */
@Service
@ConditionalOnProperty(name = "app.blockchain.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ThirdwebBlockchainGateway implements BlockchainGateway {

    private final ThirdwebProperties properties;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public BlockchainTransactionReference createRequest(String productName, BigInteger quantity, String metadata) {
        return writeAndWait(
                "function createRequest(string productName, uint256 quantity, string metadata) returns (uint256)",
                List.of(productName, quantity, metadata)
        );
    }

    @Override
    public BlockchainTransactionReference deleteRequest(BigInteger requestId) {
        return writeAndWait(
                "function deleteRequest(uint256 requestId)",
                List.of(requestId)
        );
    }

    @Override
    public BlockchainTransactionReference transferOwnership(BigInteger requestId, BigInteger productId, String newOwner) {
        return writeAndWait(
                "function transferOwnership(uint256 requestId, uint256 productId, address newOwner) returns (uint256)",
                List.of(requestId, productId, newOwner)
        );
    }

    @Override
    public BlockchainTransactionReference recordAuthorityInspection(BigInteger productId, String result, String metadata) {
        return writeAndWait(
                "function recordAuthorityInspection(uint256 productId, string result, string metadata) returns (uint256)",
                List.of(productId, result, metadata)
        );
    }

    @Override
    public BlockchainTransactionReference recordManufacturerInspection(BigInteger productId, String result, String metadata) {
        return writeAndWait(
                "function recordManufacturerInspection(uint256 productId, string result, string metadata) returns (uint256)",
                List.of(productId, result, metadata)
        );
    }

    @Override
    public BlockchainTransactionReference recallProduct(BigInteger productId, String reason) {
        return writeAndWait(
                "function recallProduct(uint256 productId, string reason) returns (uint256)",
                List.of(productId, reason)
        );
    }

    @Override
    public Object[] getRequest(BigInteger requestId) {
        return read(
                "function getRequest(uint256 requestId) view returns (uint256,address,string,uint256,string,bool,uint256,uint256)",
                List.of(requestId)
        );
    }

    @Override
    public Object[] getProduct(BigInteger productId) {
        return read(
                "function getProduct(uint256 productId) view returns (uint256,string,uint256,address,uint256,bool,bool)",
                List.of(productId)
        );
    }

    @Override
    public String getProductOwner(BigInteger productId) {
        Object[] result = read(
                "function getProductOwner(uint256 productId) view returns (address)",
                List.of(productId)
        );
        return result != null && result.length > 0 ? String.valueOf(result[0]) : null;
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private BlockchainTransactionReference writeAndWait(String methodSignature, List<Object> params) {
        try {
            ObjectNode call = objectMapper.createObjectNode();
            call.put("contractAddress", properties.getContractAddress());
            call.put("method", methodSignature);
            call.set("params", objectMapper.valueToTree(params));

            ArrayNode calls = objectMapper.createArrayNode();
            calls.add(call);

            ObjectNode body = objectMapper.createObjectNode();
            body.set("calls", calls);
            body.put("chainId", properties.getChainId());
            body.put("from", properties.getWalletAddress());

            HttpResponse<String> response = post("/contracts/write", body);

            if (response.statusCode() >= 300) {
                log.error("[thirdweb] write failed ({}): {}", response.statusCode(), response.body());
                return new BlockchainTransactionReference(null, null, "FAILED", "POLYGON_AMOY_TESTNET");
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode transactionIds = root.path("result").path("transactionIds");
            if (!transactionIds.isArray() || transactionIds.isEmpty()) {
                log.error("[thirdweb] write response missing transactionIds: {}", response.body());
                return new BlockchainTransactionReference(null, null, "FAILED", "POLYGON_AMOY_TESTNET");
            }

            String transactionId = transactionIds.get(0).asText();
            return pollUntilSettled(transactionId);

        } catch (Exception e) {
            log.error("[thirdweb] write call threw for method {}: {}", methodSignature, e.getMessage(), e);
            return new BlockchainTransactionReference(null, null, "FAILED", "POLYGON_AMOY_TESTNET");
        }
    }

    private BlockchainTransactionReference pollUntilSettled(String transactionId) throws Exception {
        long deadline = System.currentTimeMillis() + properties.getPollTimeoutMs();

        while (System.currentTimeMillis() < deadline) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(properties.getApiBaseUrl() + "/transactions/" + transactionId))
                    .header("x-secret-key", properties.getSecretKey())
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode result = objectMapper.readTree(response.body()).path("result");
            String status = result.path("status").asText("");

            if ("CONFIRMED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                return new BlockchainTransactionReference(
                        result.path("transactionHash").asText(null),
                        result.path("confirmedAtBlockNumber").asText(null),
                        status.toUpperCase(),
                        "POLYGON_AMOY_TESTNET"
                );
            }

            Thread.sleep(properties.getPollIntervalMs());
        }

        log.warn("[thirdweb] transaction {} did not settle within {}ms, returning PENDING", transactionId, properties.getPollTimeoutMs());
        return new BlockchainTransactionReference(null, null, "PENDING", "POLYGON_AMOY_TESTNET");
    }

    private Object[] read(String methodSignature, List<Object> params) {
        try {
            ObjectNode call = objectMapper.createObjectNode();
            call.put("contractAddress", properties.getContractAddress());
            call.put("method", methodSignature);
            call.set("params", objectMapper.valueToTree(params));

            ArrayNode calls = objectMapper.createArrayNode();
            calls.add(call);

            ObjectNode body = objectMapper.createObjectNode();
            body.set("calls", calls);
            body.put("chainId", properties.getChainId());

            HttpResponse<String> response = post("/contracts/read", body);

            if (response.statusCode() >= 300) {
                log.error("[thirdweb] read failed ({}): {}", response.statusCode(), response.body());
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode first = root.path("result").get(0);
            if (first == null || !first.path("success").asBoolean(false)) {
                log.error("[thirdweb] read call unsuccessful: {}", response.body());
                return null;
            }

            return flattenData(first.path("data"));

        } catch (Exception e) {
            log.error("[thirdweb] read call threw for method {}: {}", methodSignature, e.getMessage(), e);
            return null;
        }
    }

    /** The contract getters return tuples; decode however the API represents them (array, object, or scalar). */
    private Object[] flattenData(JsonNode data) {
        if (data.isArray()) {
            List<Object> values = new ArrayList<>();
            for (JsonNode node : data) {
                values.add(node.isTextual() ? node.asText() : node.isBoolean() ? node.asBoolean() : node.isNumber() ? node.asText() : node.toString());
            }
            return values.toArray();
        }
        if (data.isObject()) {
            List<Object> values = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
            while (fields.hasNext()) {
                JsonNode node = fields.next().getValue();
                values.add(node.isTextual() ? node.asText() : node.toString());
            }
            return values.toArray();
        }
        return new Object[]{data.isTextual() ? data.asText() : data.toString()};
    }

    private HttpResponse<String> post(String path, ObjectNode body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.getApiBaseUrl() + path))
                .header("Content-Type", "application/json")
                .header("x-secret-key", properties.getSecretKey())
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
