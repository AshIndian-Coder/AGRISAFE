package com.agro.trace.packages.service;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.common.exception.InvalidStateTransitionException;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.packages.dto.PackageResponse;
import com.agro.trace.packages.dto.PackageSplitRequest;
import com.agro.trace.qr.service.QrService;
import com.agro.trace.traceability.service.TraceEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageService {

    private final com.agro.trace.packages.repository.PackageRepository packageRepository;
    private final LotRepository lotRepository;
    private final QrService qrService;
    private final TraceEventService traceEventService;

    @Transactional
    public List<PackageResponse> splitLot(String lotId, PackageSplitRequest request, String agentUuid) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (lot.getStatus() != LotStatus.ACCEPTED && lot.getStatus() != LotStatus.AT_NODAL_CENTER) {
            throw new InvalidStateTransitionException("Lot must be in ACCEPTED or AT_NODAL_CENTER state to split");
        }

        // Validate quantities sum
        BigDecimal totalQuantity = request.quantities().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalQuantity.compareTo(lot.getQuantity()) > 0) {
            throw new BusinessException("QUANTITY_MISMATCH",
                    "Sum of package quantities (" + totalQuantity + ") exceeds lot quantity (" + lot.getQuantity() + ")");
        }

        List<com.agro.trace.packages.domain.Package> packages = new ArrayList<>();

        for (BigDecimal qty : request.quantities()) {
            String packageId = "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            com.agro.trace.packages.domain.Package pkg = new com.agro.trace.packages.domain.Package();
            pkg.setPackageId(packageId);
            pkg.setLotId(lotId);
            pkg.setQuantity(qty);
            pkg.setUnit(lot.getUnit());
            pkg.setPackageType(request.packageType() != null ? request.packageType() : "STANDARD");
            pkg.setStatus(LotStatus.AT_NODAL_CENTER);
            pkg.setCurrentCustodianUuid(agentUuid);
            pkg.setCurrentCustodianRole("NODAL_CENTER_AGENT");
            pkg.setNotes(request.notes());

            pkg = packageRepository.save(pkg);

            // Generate QR for the package
            String qrId = qrService.generateQr("PACKAGE", packageId, "AT_NODAL_CENTER");
            pkg.setQrId(qrId);
            pkg = packageRepository.save(pkg);

            packages.add(pkg);

            // Trace event
            traceEventService.recordEvent(
                    ActionType.PACKAGE_SPLIT, "PACKAGE", packageId,
                    agentUuid, "NODAL_CENTER_AGENT",
                    null, LotStatus.AT_NODAL_CENTER.name(),
                    null, null, qrId, null
            );
        }

        // Update lot status
        lot.setStatus(LotStatus.PACKAGED);
        lotRepository.save(lot);

        log.info("Lot {} split into {} packages by {}", lotId, packages.size(), agentUuid);
        return packages.stream().map(this::toResponse).toList();
    }

    @Transactional
    public PackageResponse verifyPackage(String packageId, String agentUuid, BigDecimal latitude, BigDecimal longitude, String qrId) {
        com.agro.trace.packages.domain.Package pkg = packageRepository.findByPackageId(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package", packageId));

        // Consume QR

        // BLOCK if package is quarantined or recalled
        if (pkg.isQuarantined()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Package " + packageId + " is QUARANTINED. Cannot proceed.");
        }
        if (pkg.isRecalled()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Package " + packageId + " is RECALLED. Cannot proceed.");
        }

        qrService.consumeQr(qrId != null ? qrId : pkg.getQrId(), agentUuid, latitude, longitude);

        pkg.setStatus(LotStatus.AT_SUPPLIER);
        pkg.setCurrentCustodianUuid(agentUuid);
        pkg.setCurrentCustodianRole("SUPPLIER");

        // Generate new QR
        String newQrId = qrService.generateQr("PACKAGE", packageId, "AT_SUPPLIER");
        pkg.setQrId(newQrId);
        pkg = packageRepository.save(pkg);

        traceEventService.recordEvent(
                ActionType.PACKAGE_VERIFIED, "PACKAGE", packageId,
                agentUuid, "SUPPLIER",
                null, LotStatus.AT_SUPPLIER.name(),
                latitude, longitude, newQrId, null
        );

        log.info("Package {} verified by agent {}", packageId, agentUuid);
        return toResponse(pkg);
    }

    public PackageResponse getPackage(String packageId) {
        com.agro.trace.packages.domain.Package pkg = packageRepository.findByPackageId(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package", packageId));
        return toResponse(pkg);
    }

    public List<PackageResponse> getLotPackages(String lotId) {
        return packageRepository.findByLotId(lotId).stream()
                .map(this::toResponse)
                .toList();
    }

    private PackageResponse toResponse(com.agro.trace.packages.domain.Package pkg) {
        return new PackageResponse(
                pkg.getPackageId(),
                pkg.getLotId(),
                pkg.getQuantity(),
                pkg.getUnit(),
                pkg.getPackageType(),
                pkg.getStatus(),
                pkg.getCurrentCustodianUuid(),
                pkg.getCurrentCustodianRole(),
                pkg.getQrId(),
                pkg.getTestingStatus(),
                pkg.isQuarantined(),
                pkg.isRecalled(),
                pkg.getNotes(),
                pkg.getCreatedAt()
        );
    }
}