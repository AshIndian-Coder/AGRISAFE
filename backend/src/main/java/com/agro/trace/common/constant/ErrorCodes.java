package com.agro.trace.common.constant;

public final class ErrorCodes {

    // Authentication & Authorization
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTH_PIN_INVALID = "PIN_INVALID";
    public static final String AUTH_PIN_LOCKED = "PIN_LOCKED";
    public static final String AUTH_OTP_INVALID = "OTP_INVALID";
    public static final String AUTH_OTP_EXPIRED = "OTP_EXPIRED";
    public static final String AUTH_OTP_RATE_LIMITED = "OTP_RATE_LIMITED";
    public static final String AUTH_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String AUTH_TOKEN_INVALID = "TOKEN_INVALID";
    public static final String AUTH_UNAUTHORIZED_ROLE = "UNAUTHORIZED_ROLE";
    public static final String AUTH_SESSION_EXPIRED = "SESSION_EXPIRED";
    public static final String AUTH_REFRESH_TOKEN_INVALID = "REFRESH_TOKEN_INVALID";

    // Registration
    public static final String REG_DUPLICATE_AADHAAR = "DUPLICATE_AADHAAR";
    public static final String REG_DUPLICATE_PF = "DUPLICATE_PF";
    public static final String REG_DUPLICATE_GST = "DUPLICATE_GST";
    public static final String REG_DUPLICATE_PAN = "DUPLICATE_PAN";
    public static final String REG_DUPLICATE_EMPLOYEE_ID = "DUPLICATE_EMPLOYEE_ID";
    public static final String REG_DUPLICATE_REFERENCE = "DUPLICATE_REFERENCE";
    public static final String REG_DUPLICATE_REGISTRATION = "DUPLICATE_REGISTRATION";
    public static final String REG_INVALID_AADHAAR = "INVALID_AADHAAR";
    public static final String REG_INVALID_PF = "INVALID_PF";
    public static final String REG_INVALID_GST = "INVALID_GST";
    public static final String REG_INVALID_PAN = "INVALID_PAN";
    public static final String REG_GOVERNMENT_VERIFICATION_FAILED = "GOVERNMENT_VERIFICATION_FAILED";
    public static final String REG_ORGANIZATION_NOT_APPROVED = "ORGANIZATION_NOT_APPROVED";

    // QR
    public static final String QR_NOT_FOUND = "QR_NOT_FOUND";
    public static final String QR_ALREADY_CONSUMED = "QR_ALREADY_CONSUMED";
    public static final String QR_EXPIRED = "QR_EXPIRED";
    public static final String QR_INVALID_STATE = "QR_INVALID_STATE";
    public static final String QR_ALREADY_ACTIVE = "QR_ALREADY_ACTIVE";

    // GPS
    public static final String GPS_REQUIRED = "GPS_REQUIRED";
    public static final String GPS_OUTSIDE_ALLOWED_AREA = "GPS_OUTSIDE_ALLOWED_AREA";
    public static final String GPS_LOW_ACCURACY = "GPS_LOW_ACCURACY";

    // Lot
    public static final String LOT_NOT_FOUND = "LOT_NOT_FOUND";
    public static final String LOT_INVALID_STATE = "INVALID_STATE_TRANSITION";
    public static final String LOT_QUARANTINED = "LOT_QUARANTINED";
    public static final String LOT_RECALLED = "PRODUCT_RECALLED";
    public static final String LOT_CANNOT_DELETE = "LOT_CANNOT_DELETE";
    public static final String LOT_ALREADY_ACCEPTED = "LOT_ALREADY_ACCEPTED";

    // Package
    public static final String PACKAGE_NOT_FOUND = "PACKAGE_NOT_FOUND";
    public static final String PACKAGE_QUANTITY_MISMATCH = "QUANTITY_MISMATCH";
    public static final String PACKAGE_ALREADY_VERIFIED = "PACKAGE_ALREADY_VERIFIED";

    // Testing
    public static final String TEST_FAILED = "TEST_FAILED";
    public static final String TEST_DATA_ANOMALY = "TEST_DATA_ANOMALY";
    public static final String TEST_MANDATORY_MISSING = "TEST_MANDATORY_MISSING";
    public static final String TEST_RESULT_IMMUTABLE = "TEST_RESULT_IMMUTABLE";
    public static final String STANDARD_NOT_FOUND = "STANDARD_NOT_FOUND";

    // Manufacturing
    public static final String MANUFACTURER_LOT_NOT_FOUND = "MANUFACTURER_LOT_NOT_FOUND";
    public static final String MANUFACTURER_MERGE_INVALID = "MANUFACTURER_MERGE_INVALID";

    // Business
    public static final String CUSTODY_TRANSFER_INVALID = "CUSTODY_TRANSFER_INVALID";
    public static final String BUNDLE_NOT_FOUND = "BUNDLE_NOT_FOUND";
    public static final String RETAILER_RECEIPT_INVALID = "RETAILER_RECEIPT_INVALID";
    public static final String PRODUCT_BLOCKED = "PRODUCT_BLOCKED";
    public static final String PRODUCT_NOT_VERIFIED = "PRODUCT_NOT_VERIFIED";
    public static final String INVALID_LINEAGE = "INVALID_LINEAGE";

    // Security
    public static final String INTEGRITY_CHECK_FAILED = "INTEGRITY_CHECK_FAILED";
    public static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";
    public static final String FORBIDDEN_ACCESS = "FORBIDDEN_ACCESS";
    public static final String DUPLICATE_ATTEMPT_FLAGGED = "DUPLICATE_ATTEMPT_FLAGGED";

    // Payment
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String PAYMENT_DUPLICATE = "PAYMENT_DUPLICATE";

    // General
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String RATE_LIMITED = "RATE_LIMITED";
    public static final String IDEMPOTENCY_MISMATCH = "IDEMPOTENCY_MISMATCH";
    public static final String BLOCKCHAIN_SYNC_PENDING = "BLOCKCHAIN_SYNC_PENDING";

    private ErrorCodes() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}