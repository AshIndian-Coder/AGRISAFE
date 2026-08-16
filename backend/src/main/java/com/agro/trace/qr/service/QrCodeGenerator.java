package com.agro.trace.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Generates QR code PNG images from QR ID strings.
 * Used to create printable/scannable QR codes for the physical supply chain.
 */
@Service
@Slf4j
public class QrCodeGenerator {

    private static final int DEFAULT_SIZE = 400;

    /**
     * Generate a QR code PNG image as a byte array.
     * @param qrId The QR identifier to encode
     * @return PNG image bytes
     */
    public byte[] generateQrCodeImage(String qrId) {
        return generateQrCodeImage(qrId, DEFAULT_SIZE);
    }

    /**
     * Generate a QR code PNG image with custom size.
     * @param qrId The QR identifier to encode
     * @param size Width/height in pixels
     * @return PNG image bytes
     */
    public byte[] generateQrCodeImage(String qrId, int size) {
        try {
            // The QR content is the full verification URL
            String content = "https://agrotrace.gov.in/verify/" + qrId;

            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, 2);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

            log.debug("Generated QR code image for: {}", qrId);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code image for: {}", qrId, e);
            throw new RuntimeException("Failed to generate QR code image", e);
        }
    }
}