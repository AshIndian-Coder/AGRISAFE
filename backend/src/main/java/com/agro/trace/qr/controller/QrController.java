package com.agro.trace.qr.controller;

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
     * Validate the current state of a QR (without consuming it).
     */
    @GetMapping("/{qrId}/validate")
    public ResponseEntity<ApiResponse<QrCredential>> validateQr(@PathVariable String qrId) {
        var qr = qrService.validateQrStatus(qrId);
        return ResponseEntity.ok(ApiResponse.success(qr));
    }
}