CREATE TABLE IF NOT EXISTS email_otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    expires_at TIMESTAMP(6) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    attempt_count INT NOT NULL DEFAULT 0,
    purpose VARCHAR(32) NULL,
    INDEX idx_email_otp_email (email),
    INDEX idx_email_otp_purpose (purpose)
);
