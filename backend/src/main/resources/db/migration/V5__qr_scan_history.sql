CREATE TABLE qr_scan_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                 uuid VARCHAR(36) NOT NULL UNIQUE,

                                 qr_id VARCHAR(64) NOT NULL,
                                 rotating_code_hash VARCHAR(128) NOT NULL,

                                 scan_timestamp TIMESTAMP(6) NOT NULL,

                                 scanned_by_uuid VARCHAR(36),
                                 scanned_by_role VARCHAR(64),

                                 latitude DECIMAL(10, 7),
                                 longitude DECIMAL(10, 7),

                                 stage VARCHAR(32),
                                 object_type VARCHAR(32),
                                 object_id VARCHAR(64),

                                 result VARCHAR(20) NOT NULL,

                                 created_at TIMESTAMP(6) NOT NULL,
                                 updated_at TIMESTAMP(6) NOT NULL,

                                 created_by VARCHAR(36),
                                 updated_by VARCHAR(36),

                                 version BIGINT,
                                 active BOOLEAN NOT NULL DEFAULT TRUE,

                                 CONSTRAINT fk_qr_scan_history_qr
                                     FOREIGN KEY (qr_id)
                                         REFERENCES qr_credentials(qr_id),

                                 INDEX idx_qr_scan_qr (qr_id),
                                 INDEX idx_qr_scan_time (scan_timestamp),
                                 INDEX idx_qr_scan_actor (scanned_by_uuid)
);