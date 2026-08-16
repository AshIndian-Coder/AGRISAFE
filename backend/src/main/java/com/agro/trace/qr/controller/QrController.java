package com.agro.trace.qr.controller;

import com.agro.trace.qr.dto.QrScanRequest;
import jakarta.validation.Valid;
import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.qr.domain.QrCredential;
import com.agro.trace.qr.service.QrCodeGenerator;
import com.agro.trace.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;
    private final QrCodeGenerator qrCodeGenerator;

    /**
     * Get QR code details (backend state).
     */
    @GetMapping("/{qrId}")
    public ResponseEntity<ApiResponse<QrCredential>> getQrDetails(@PathVariable String qrId) {
        var qr = qrService.getQrDetails(qrId);
        return ResponseEntity.ok(ApiResponse.success(qr));
    }

    /**
     * Generate and return a QR code PNG image for the given QR ID.
     * Frontend can display this image or download it for printing.
     * URL format: /api/v1/qr/{qrId}/image
     */
    @GetMapping(value = "/{qrId}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrImage(@PathVariable String qrId) {
        // Verify QR exists
        qrService.getQrDetails(qrId);

        byte[] image = qrCodeGenerator.generateQrCodeImage(qrId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);
        headers.setCacheControl("public, max-age=3600");

        return ResponseEntity.ok().headers(headers).body(image);
    }
    /**
     * Get the current 30-second rotating backend code.
     * The physical QR image remains unchanged.
     */
    @GetMapping("/{qrId}/current-code")
    public ResponseEntity<ApiResponse<String>> getCurrentRotatingCode(
            @PathVariable String qrId
    ) {
        String code = qrService.getCurrentRotatingCode(qrId);

        return ResponseEntity.ok(
                ApiResponse.success(code)
        );
    }
    /**
     * Validate the current state of a QR (without consuming it).
     */
    @GetMapping("/{qrId}/validate")
    public ResponseEntity<ApiResponse<QrCredential>> validateQr(@PathVariable String qrId) {
        var qr = qrService.validateQrStatus(qrId);
        return ResponseEntity.ok(ApiResponse.success(qr));
    }
    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<QrCredential>> scanRotatingQr(
            @Valid @RequestBody QrScanRequest request
    ) {

        QrCredential qr =
                qrService.verifyAndConsumeRotatingQr(
                        request.qrId(),
                        request.rotatingCode(),
                        request.scannedByUuid(),
                        request.scannedByRole(),
                        request.latitude(),
                        request.longitude()
                );

        return ResponseEntity.ok(
                ApiResponse.success(qr)
        );
    }
}