package com.agro.trace.manufacturing.service;

import com.agro.trace.bundles.domain.Bundle;
import com.agro.trace.bundles.repository.BundleRepository;
import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.common.exception.InvalidStateTransitionException;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.manufacturing.domain.ManufacturerLot;
import com.agro.trace.manufacturing.domain.ManufacturerLotInput;
import com.agro.trace.manufacturing.dto.ManufacturerLotCreateRequest;
import com.agro.trace.manufacturing.dto.ManufacturerLotResponse;
import com.agro.trace.manufacturing.repository.ManufacturerLotInputRepository;
import com.agro.trace.manufacturing.repository.ManufacturerLotRepository;
import com.agro.trace.qr.service.QrService;
import com.agro.trace.traceability.service.TraceEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManufacturerService {

    private final ManufacturerLotRepository manufacturerLotRepository;
    private final ManufacturerLotInputRepository manufacturerLotInputRepository;
    private final LotRepository lotRepository;
    private final BundleRepository bundleRepository;
    private final QrService qrService;
    private final TraceEventService traceEventService;

    @Transactional
    public ManufacturerLotResponse createManufacturerLot(ManufacturerLotCreateRequest request, String employeeUuid) {
        // Verify all input lots exist and are in valid state
        for (String inputLotId : request.inputLotIds()) {
            Lot lot = lotRepository.findByLotId(inputLotId)
                    .orElseThrow(() -> new EntityNotFoundException("Input Lot", inputLotId));
            if (lot.getStatus() != LotStatus.AT_MANUFACTURER && lot.getStatus() != LotStatus.PACKAGED) {
                throw new InvalidStateTransitionException(
                        "Input lot " + inputLotId + " is not in MANUFACTURER state");
            }
        }

        String mfgLotId = "MFR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ManufacturerLot mfgLot = new ManufacturerLot();
        mfgLot.setManufacturerLotId(mfgLotId);
        mfgLot.setProductId(request.productId());
        mfgLot.setManufacturerEmployeeUuid(employeeUuid);
        mfgLot.setProductionQuantity(request.productionQuantity());
        mfgLot.setUnit(request.unit());
        mfgLot.setProcessingDate(Instant.now());
        mfgLot.setFacilityName(request.facilityName());
        mfgLot.setStatus(LotStatus.PROCESSED);
        mfgLot.setTestingStatus("NOT_TESTED");

        mfgLot = manufacturerLotRepository.save(mfgLot);

        // Record inputs and consume their QRs
        for (String inputLotId : request.inputLotIds()) {
            // Consume the QR of each input lot/package
            Lot inputLot = lotRepository.findByLotId(inputLotId).orElse(null);
            if (inputLot != null && inputLot.getQrId() != null) {
                try {
                    qrService.consumeQr(inputLot.getQrId(), employeeUuid, null, null);
                } catch (Exception e) {
                    log.warn("QR consumption failed for input lot {}: {}", inputLotId, e.getMessage());
                }
            }

            ManufacturerLotInput input = new ManufacturerLotInput();
            input.setManufacturerLotId(mfgLotId);
            input.setInputLotId(inputLotId);
            input.setInputType("LOT");
            manufacturerLotInputRepository.save(input);

            // Update input lot status
            lotRepository.findByLotId(inputLotId).ifPresent(lot -> {
                lot.setStatus(LotStatus.PROCESSED);
                lotRepository.save(lot);
            });
        }

        // Generate QR
        String qrId = qrService.generateQr("MANUFACTURER_LOT", mfgLotId, "PROCESSED");
        mfgLot.setQrId(qrId);
        mfgLot = manufacturerLotRepository.save(mfgLot);

        // Trace event
        traceEventService.recordEvent(
                ActionType.MANUFACTURER_LOT_CREATED, "MANUFACTURER_LOT", mfgLotId,
                employeeUuid, "MANUFACTURER_EMPLOYEE",
                null, LotStatus.PROCESSED.name(),
                null, null, qrId, null
        );

        log.info("Manufacturer lot created: {} from {} inputs", mfgLotId, request.inputLotIds().size());
        return toResponse(mfgLot);
    }

    @Transactional
    public List<ManufacturerLotResponse.BundleResponse> createBundles(
            String manufacturerLotId, String bundleType, int bundleCount, String employeeUuid) {
        ManufacturerLot mfgLot = manufacturerLotRepository.findByManufacturerLotId(manufacturerLotId)
                .orElseThrow(() -> new EntityNotFoundException("ManufacturerLot", manufacturerLotId));

        if (mfgLot.getStatus() != LotStatus.PROCESSED && mfgLot.getStatus() != LotStatus.MANUFACTURER_TEST_PASSED) {
            throw new InvalidStateTransitionException(
                    "Manufacturer lot must be in PROCESSED/TEST_PASSED state, current: " + mfgLot.getStatus());
        }

        List<ManufacturerLotResponse.BundleResponse> bundles = new ArrayList<>();

        for (int i = 0; i < bundleCount; i++) {
            String bundleId = "BDL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Bundle bundle = new Bundle();
            bundle.setBundleId(bundleId);
            bundle.setManufacturerLotId(manufacturerLotId);
            bundle.setBundleType(bundleType != null ? bundleType : "CARTON");
            bundle.setQuantity(BigDecimal.ONE);
            bundle.setUnit("UNIT");
            bundle.setStatus(LotStatus.BUNDLED);
            bundle.setCurrentCustodianUuid(employeeUuid);
            bundle.setCurrentCustodianRole("MANUFACTURER_EMPLOYEE");

            bundle = bundleRepository.save(bundle);

            String qrId = qrService.generateQr("BUNDLE", bundleId, "BUNDLED");
            bundle.setQrId(qrId);
            bundle = bundleRepository.save(bundle);

            bundles.add(new ManufacturerLotResponse.BundleResponse(
                    bundleId, bundleType, BigDecimal.ONE, "UNIT", qrId, LotStatus.BUNDLED, bundle.getCreatedAt()));

            traceEventService.recordEvent(
                    ActionType.BUNDLE_CREATED, "BUNDLE", bundleId,
                    employeeUuid, "MANUFACTURER_EMPLOYEE",
                    null, LotStatus.BUNDLED.name(),
                    null, null, qrId, null
            );
        }

        mfgLot.setStatus(LotStatus.BUNDLED);
        manufacturerLotRepository.save(mfgLot);

        log.info("{} bundles created for manufacturer lot {}", bundleCount, manufacturerLotId);
        return bundles;
    }

    public ManufacturerLotResponse getManufacturerLot(String manufacturerLotId) {
        ManufacturerLot mfgLot = manufacturerLotRepository.findByManufacturerLotId(manufacturerLotId)
                .orElseThrow(() -> new EntityNotFoundException("ManufacturerLot", manufacturerLotId));
        return toResponse(mfgLot);
    }

    public List<ManufacturerLotResponse> getManufacturerLots(String employeeUuid) {
        return manufacturerLotRepository.findByManufacturerEmployeeUuid(employeeUuid).stream()
                .map(this::toResponse)
                .toList();
    }

    private ManufacturerLotResponse toResponse(ManufacturerLot mfgLot) {
        List<String> inputIds = manufacturerLotInputRepository
                .findByManufacturerLotId(mfgLot.getManufacturerLotId()).stream()
                .map(ManufacturerLotInput::getInputLotId)
                .toList();

        List<ManufacturerLotResponse.BundleResponse> bundles = bundleRepository
                .findByManufacturerLotId(mfgLot.getManufacturerLotId()).stream()
                .map(b -> new ManufacturerLotResponse.BundleResponse(
                        b.getBundleId(), b.getBundleType(), b.getQuantity(), b.getUnit(),
                        b.getQrId(), b.getStatus(), b.getCreatedAt()))
                .toList();

        return new ManufacturerLotResponse(
                mfgLot.getManufacturerLotId(), mfgLot.getProductId(),
                mfgLot.getManufacturerEmployeeUuid(), mfgLot.getProductionQuantity(),
                mfgLot.getUnit(), mfgLot.getFacilityName(), mfgLot.getStatus(),
                mfgLot.getTestingStatus(), mfgLot.getQrId(), inputIds,
                mfgLot.isRecalled(), null, mfgLot.getCreatedAt(), bundles
        );
    }
}