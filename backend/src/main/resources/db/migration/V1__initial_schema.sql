-- ============================================================
-- AGRO TRACE - INITIAL DATABASE SCHEMA
-- Version: V1
-- Description: Core tables for agricultural supply chain traceability
-- ============================================================

-- -----------------------------------------------------------
-- 1. USERS & AUTHENTICATION
-- -----------------------------------------------------------

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    aadhaar_reference VARCHAR(64) UNIQUE,
    masked_aadhaar VARCHAR(16),
    pf_reference VARCHAR(64) UNIQUE,
    employee_id VARCHAR(64),
    gst_number VARCHAR(32),
    pan_number VARCHAR(16),
    mobile_number VARCHAR(16),
    name VARCHAR(255),
    email VARCHAR(255),
    pin_hash VARCHAR(512) NOT NULL,
    pin_attempts INT NOT NULL DEFAULT 0,
    pin_locked_until DATETIME(6),
    otp_hash VARCHAR(512),
    otp_expires_at DATETIME(6),
    otp_sent_count INT NOT NULL DEFAULT 0,
    otp_sent_window_start DATETIME(6),
    identity_reference VARCHAR(64),
    user_type VARCHAR(32) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    mobile_verified BOOLEAN NOT NULL DEFAULT FALSE,
    registration_complete BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at DATETIME(6),
    last_login_ip VARCHAR(64),
    organization_id BIGINT,
    functional_type VARCHAR(32),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_users_uuid (uuid),
    INDEX idx_users_mobile (mobile_number),
    INDEX idx_users_type (user_type),
    INDEX idx_users_org (organization_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    user_uuid VARCHAR(36) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_rt_token (token(255)),
    INDEX idx_rt_user (user_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 2. ORGANIZATIONS
-- -----------------------------------------------------------

CREATE TABLE organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    organization_id VARCHAR(64) NOT NULL UNIQUE,
    legal_name VARCHAR(255) NOT NULL,
    trade_name VARCHAR(255),
    gst_number VARCHAR(32) UNIQUE,
    pan_number VARCHAR(16) UNIQUE,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    registration_reference VARCHAR(64) UNIQUE,
    org_type VARCHAR(32) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    country VARCHAR(100) DEFAULT 'India',
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    approved_by VARCHAR(36),
    approved_at DATETIME(6),
    rejection_reason VARCHAR(1000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_org_org_id (organization_id),
    INDEX idx_org_status (status),
    INDEX idx_org_type (org_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE organization_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    organization_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_reference VARCHAR(255),
    document_url VARCHAR(1000),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 3. PRODUCTS
-- -----------------------------------------------------------

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    product_code VARCHAR(32) NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    sub_category VARCHAR(100),
    default_unit VARCHAR(20),
    requires_packaging BOOLEAN NOT NULL DEFAULT TRUE,
    requires_manufacturing BOOLEAN NOT NULL DEFAULT FALSE,
    fssai_applicable BOOLEAN NOT NULL DEFAULT TRUE,
    regulatory_standard_type VARCHAR(50) DEFAULT 'FSSAI',
    description VARCHAR(2000),
    icon_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_product_code (product_code),
    INDEX idx_product_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_varieties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    variety_code VARCHAR(32) NOT NULL,
    variety_name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_variety_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 4. PRICING
-- -----------------------------------------------------------

CREATE TABLE pricing_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    variety_id BIGINT,
    price DECIMAL(15,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    source VARCHAR(50) NOT NULL DEFAULT 'MOCK',
    source_reference VARCHAR(128),
    recorded_at DATETIME(6) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_pricing_product (product_id),
    INDEX idx_pricing_time (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 5. LOTS
-- -----------------------------------------------------------

CREATE TABLE lots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    lot_id VARCHAR(64) NOT NULL UNIQUE,
    farmer_uuid VARCHAR(36) NOT NULL,
    product_id BIGINT NOT NULL,
    variety_id BIGINT,
    quantity DECIMAL(15,3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    origin_latitude DECIMAL(10,7),
    origin_longitude DECIMAL(10,7),
    origin_address VARCHAR(500),
    estimated_value DECIMAL(15,2),
    pricing_reference VARCHAR(64),
    current_custodian_uuid VARCHAR(36),
    current_custodian_role VARCHAR(32),
    qr_id VARCHAR(64),
    accepted_at DATETIME(6),
    accepted_by VARCHAR(36),
    nodal_center_id BIGINT,
    blockchain_ref VARCHAR(128),
    data_hash VARCHAR(128),
    recalled BOOLEAN NOT NULL DEFAULT FALSE,
    recalled_at DATETIME(6),
    recall_reason VARCHAR(1000),
    notes VARCHAR(2000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_lot_lot_id (lot_id),
    INDEX idx_lot_farmer (farmer_uuid),
    INDEX idx_lot_status (status),
    INDEX idx_lot_custodian (current_custodian_uuid),
    INDEX idx_lot_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 6. PACKAGES
-- -----------------------------------------------------------

CREATE TABLE packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    package_id VARCHAR(64) NOT NULL UNIQUE,
    lot_id VARCHAR(64) NOT NULL,
    quantity DECIMAL(15,3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    package_type VARCHAR(50),
    status VARCHAR(32) NOT NULL DEFAULT 'AT_NODAL_CENTER',
    current_custodian_uuid VARCHAR(36),
    current_custodian_role VARCHAR(32),
    qr_id VARCHAR(64),
    parent_package_id VARCHAR(64),
    testing_status VARCHAR(32) DEFAULT 'NOT_TESTED',
    blockchain_ref VARCHAR(128),
    data_hash VARCHAR(128),
    recalled BOOLEAN NOT NULL DEFAULT FALSE,
    quarantined BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(2000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_pkg_package_id (package_id),
    INDEX idx_pkg_lot (lot_id),
    INDEX idx_pkg_status (status),
    INDEX idx_pkg_custodian (current_custodian_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 7. QR CREDENTIALS
-- -----------------------------------------------------------

CREATE TABLE qr_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    qr_id VARCHAR(64) NOT NULL UNIQUE,
    object_type VARCHAR(32) NOT NULL,
    object_id VARCHAR(64) NOT NULL,
    stage VARCHAR(32),
    dynamic_secret VARCHAR(512),
    secret_rotated_at DATETIME(6),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    issued_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    expires_at DATETIME(6),
    consumed_at DATETIME(6),
    consumed_by VARCHAR(36),
    consumed_latitude DECIMAL(10,7),
    consumed_longitude DECIMAL(10,7),
    previous_qr_id VARCHAR(64),
    next_qr_id VARCHAR(64),
    blockchain_ref VARCHAR(128),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_qr_qr_id (qr_id),
    INDEX idx_qr_status (status),
    INDEX idx_qr_object (object_type, object_id),
    INDEX idx_qr_consumed (consumed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 8. CUSTODY TRANSFERS
-- -----------------------------------------------------------

CREATE TABLE custody_transfers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    transfer_id VARCHAR(64) NOT NULL UNIQUE,
    object_type VARCHAR(32) NOT NULL,
    object_id VARCHAR(64) NOT NULL,
    from_custodian_uuid VARCHAR(36),
    to_custodian_uuid VARCHAR(36) NOT NULL,
    from_role VARCHAR(32),
    to_role VARCHAR(32),
    transfer_type VARCHAR(50),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    gps_accuracy DOUBLE,
    qr_id VARCHAR(64),
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    transferred_at DATETIME(6) NOT NULL,
    blockchain_ref VARCHAR(128),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_ct_object (object_type, object_id),
    INDEX idx_ct_to (to_custodian_uuid),
    INDEX idx_ct_transfer_id (transfer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 9. TRACE EVENTS
-- -----------------------------------------------------------

CREATE TABLE trace_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    event_id VARCHAR(64) NOT NULL UNIQUE,
    object_type VARCHAR(32) NOT NULL,
    object_id VARCHAR(64) NOT NULL,
    actor_uuid VARCHAR(36) NOT NULL,
    actor_role VARCHAR(32),
    action VARCHAR(50) NOT NULL,
    previous_state VARCHAR(32),
    new_state VARCHAR(32),
    event_timestamp DATETIME(6) NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    gps_accuracy DOUBLE,
    qr_id VARCHAR(64),
    device_info VARCHAR(255),
    test_reference VARCHAR(64),
    blockchain_ref VARCHAR(128),
    metadata_json TEXT,
    event_hash VARCHAR(128),
    trace_id VARCHAR(64),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_te_object (object_type, object_id),
    INDEX idx_te_actor (actor_uuid),
    INDEX idx_te_timestamp (event_timestamp),
    INDEX idx_te_action (action),
    INDEX idx_te_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 10. TESTING
-- -----------------------------------------------------------

CREATE TABLE test_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    profile_name VARCHAR(255) NOT NULL,
    product_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_tp_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE test_definitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    test_code VARCHAR(50) NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    test_category VARCHAR(100),
    unit VARCHAR(50),
    description VARCHAR(2000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_td_code (test_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE profile_test_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    test_definition_id BIGINT NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (profile_id) REFERENCES test_profiles(id),
    FOREIGN KEY (test_definition_id) REFERENCES test_definitions(id),
    UNIQUE KEY uk_profile_test (profile_id, test_definition_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE test_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    test_record_id VARCHAR(64) NOT NULL UNIQUE,
    object_type VARCHAR(32) NOT NULL,
    object_id VARCHAR(64) NOT NULL,
    test_profile_id BIGINT NOT NULL,
    test_definition_id BIGINT,
    tester_uuid VARCHAR(36) NOT NULL,
    measurement_source VARCHAR(20) DEFAULT 'SIMULATED',
    device_id VARCHAR(64),
    measured_value VARCHAR(255),
    unit VARCHAR(50),
    result VARCHAR(20) NOT NULL,
    standard_version_id BIGINT,
    standard_name VARCHAR(255),
    min_threshold VARCHAR(100),
    max_threshold VARCHAR(100),
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    tested_at DATETIME(6) NOT NULL,
    finalized BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    anomaly_flag BOOLEAN NOT NULL DEFAULT FALSE,
    qr_id VARCHAR(64),
    blockchain_ref VARCHAR(128),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_tr_object (object_type, object_id),
    INDEX idx_tr_tester (tester_uuid),
    INDEX idx_tr_result (result),
    INDEX idx_tr_record_id (test_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 11. STANDARDS (FSSAI)
-- -----------------------------------------------------------

CREATE TABLE food_standards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    standard_name VARCHAR(255) NOT NULL,
    regulation_name VARCHAR(500),
    chapter VARCHAR(255),
    section_reference VARCHAR(255),
    source_url VARCHAR(1000),
    source_document VARCHAR(500),
    standard_version VARCHAR(50) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    amendment_reference VARCHAR(255),
    retrieved_at DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_fs_standard (standard_name),
    INDEX idx_fs_version (standard_version),
    INDEX idx_fs_effective (effective_from)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE standard_requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    standard_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    test_code VARCHAR(50),
    test_name VARCHAR(255) NOT NULL,
    parameter VARCHAR(255),
    unit VARCHAR(50),
    minimum_value DECIMAL(15,6),
    maximum_value DECIMAL(15,6),
    allowed_values VARCHAR(1000),
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    test_method_reference VARCHAR(500),
    notes VARCHAR(2000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (standard_id) REFERENCES food_standards(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_sr_standard (standard_id),
    INDEX idx_sr_product (product_id),
    INDEX idx_sr_test (test_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 12. MANUFACTURING
-- -----------------------------------------------------------

CREATE TABLE manufacturer_lots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    manufacturer_lot_id VARCHAR(64) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    manufacturer_org_id BIGINT,
    manufacturer_employee_uuid VARCHAR(36),
    production_quantity DECIMAL(15,3),
    unit VARCHAR(20),
    processing_date DATETIME(6),
    facility_name VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'PROCESSED',
    testing_status VARCHAR(32) DEFAULT 'NOT_TESTED',
    qr_id VARCHAR(64),
    blockchain_ref VARCHAR(128),
    data_hash VARCHAR(128),
    recalled BOOLEAN NOT NULL DEFAULT FALSE,
    recalled_at DATETIME(6),
    recall_reason VARCHAR(1000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_ml_mlot_id (manufacturer_lot_id),
    INDEX idx_ml_product (product_id),
    INDEX idx_ml_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE manufacturer_lot_inputs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    manufacturer_lot_id VARCHAR(64) NOT NULL,
    input_lot_id VARCHAR(64) NOT NULL,
    input_type VARCHAR(32),
    quantity DECIMAL(15,3),
    unit VARCHAR(20),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_mli_mlot (manufacturer_lot_id),
    INDEX idx_mli_input (input_lot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 13. BUNDLES
-- -----------------------------------------------------------

CREATE TABLE bundles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    bundle_id VARCHAR(64) NOT NULL UNIQUE,
    manufacturer_lot_id VARCHAR(64),
    bundle_type VARCHAR(50),
    quantity DECIMAL(15,3),
    unit VARCHAR(20),
    status VARCHAR(32) NOT NULL DEFAULT 'BUNDLED',
    current_custodian_uuid VARCHAR(36),
    current_custodian_role VARCHAR(32),
    qr_id VARCHAR(64),
    blockchain_ref VARCHAR(128),
    data_hash VARCHAR(128),
    recalled BOOLEAN NOT NULL DEFAULT FALSE,
    quarantined BOOLEAN NOT NULL DEFAULT FALSE,
    retailer_received BOOLEAN NOT NULL DEFAULT FALSE,
    retailer_received_at DATETIME(6),
    retailer_uuid VARCHAR(36),
    distributor_verified BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(2000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_bnd_bundle_id (bundle_id),
    INDEX idx_bnd_mlot (manufacturer_lot_id),
    INDEX idx_bnd_status (status),
    INDEX idx_bnd_custodian (current_custodian_uuid),
    INDEX idx_bnd_retailer (retailer_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bundle_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    bundle_id VARCHAR(64) NOT NULL,
    product_unit_id VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_bu_bundle (bundle_id),
    INDEX idx_bu_unit (product_unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 14. PRODUCT UNITS
-- -----------------------------------------------------------

CREATE TABLE product_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    unit_id VARCHAR(64) NOT NULL UNIQUE,
    bundle_id VARCHAR(64),
    manufacturer_lot_id VARCHAR(64),
    product_id BIGINT NOT NULL,
    qr_id VARCHAR(64),
    public_qr_token VARCHAR(64) UNIQUE,
    consumer_verified BOOLEAN NOT NULL DEFAULT FALSE,
    recalled BOOLEAN NOT NULL DEFAULT FALSE,
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_pu_unit_id (unit_id),
    INDEX idx_pu_bundle (bundle_id),
    INDEX idx_pu_token (public_qr_token),
    INDEX idx_pu_verified (consumer_verified)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 15. COMPLAINTS
-- -----------------------------------------------------------

CREATE TABLE complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    complaint_id VARCHAR(64) NOT NULL UNIQUE,
    complainant_uuid VARCHAR(36) NOT NULL,
    complainant_role VARCHAR(32),
    category VARCHAR(50) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    evidence_json TEXT,
    related_lot_id VARCHAR(64),
    related_organization_id BIGINT,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    assigned_officer_uuid VARCHAR(36),
    resolved_at DATETIME(6),
    resolution VARCHAR(2000),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_cmp_complainant (complainant_uuid),
    INDEX idx_cmp_status (status),
    INDEX idx_cmp_lot (related_lot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 16. FLAGS
-- -----------------------------------------------------------

CREATE TABLE flags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    flag_id VARCHAR(64) NOT NULL UNIQUE,
    flag_type VARCHAR(64) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    entity_type VARCHAR(32),
    entity_id VARCHAR(64),
    actor_uuid VARCHAR(36),
    description VARCHAR(2000),
    evidence_json TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    assigned_investigator_uuid VARCHAR(36),
    resolution VARCHAR(2000),
    resolved_at DATETIME(6),
    resolved_by VARCHAR(36),
    blockchain_ref VARCHAR(128),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_flg_flag_id (flag_id),
    INDEX idx_flg_type (flag_type),
    INDEX idx_flg_severity (severity),
    INDEX idx_flg_status (status),
    INDEX idx_flg_entity (entity_type, entity_id),
    INDEX idx_flg_investigator (assigned_investigator_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 17. PAYMENTS
-- -----------------------------------------------------------

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    payment_id VARCHAR(64) NOT NULL UNIQUE,
    lot_id VARCHAR(64),
    payer_uuid VARCHAR(36),
    payee_uuid VARCHAR(36),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    payment_type VARCHAR(32),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    idempotency_key VARCHAR(64) UNIQUE,
    provider_reference VARCHAR(128),
    provider VARCHAR(50) DEFAULT 'MOCK',
    completed_at DATETIME(6),
    failure_reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    INDEX idx_pay_lot (lot_id),
    INDEX idx_pay_idem (idempotency_key),
    INDEX idx_pay_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 18. BLOCKCHAIN OUTBOX
-- -----------------------------------------------------------

CREATE TABLE blockchain_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    event_type VARCHAR(64) NOT NULL,
    aggregate_type VARCHAR(32),
    aggregate_id VARCHAR(64),
    payload_json TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    max_retries INT NOT NULL DEFAULT 5,
    last_error TEXT,
    blockchain_tx_hash VARCHAR(128),
    blockchain_ref VARCHAR(128),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_bo_status (status),
    INDEX idx_bo_type (event_type),
    INDEX idx_bo_aggregate (aggregate_type, aggregate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 19. AUDIT LOGS
-- -----------------------------------------------------------

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_uuid VARCHAR(36),
    actor_role VARCHAR(32),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(32),
    entity_id VARCHAR(64),
    details TEXT,
    timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ip_address VARCHAR(64),
    trace_id VARCHAR(64),
    correlation_id VARCHAR(64),
    data_classification VARCHAR(32),
    INDEX idx_al_actor (actor_uuid),
    INDEX idx_al_action (action),
    INDEX idx_al_timestamp (timestamp),
    INDEX idx_al_entity (entity_type, entity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 20. NOTIFICATIONS
-- -----------------------------------------------------------

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    recipient_uuid VARCHAR(36) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    body TEXT,
    reference_type VARCHAR(32),
    reference_id VARCHAR(64),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sent_at DATETIME(6),
    read_at DATETIME(6),
    provider VARCHAR(50) DEFAULT 'MOCK',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_notif_recipient (recipient_uuid),
    INDEX idx_notif_status (status),
    INDEX idx_notif_type (notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 21. MOCK GOVERNMENT REGISTRIES
-- -----------------------------------------------------------

CREATE TABLE mock_aadhaar_registry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aadhaar_reference VARCHAR(64) NOT NULL UNIQUE,
    masked_aadhaar VARCHAR(16),
    registered_mobile VARCHAR(16),
    person_name VARCHAR(255),
    date_of_birth DATE,
    address VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    verification_status VARCHAR(20) DEFAULT 'VERIFIED',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mock_pf_registry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pf_reference VARCHAR(64) NOT NULL UNIQUE,
    identity_reference VARCHAR(64) NOT NULL,
    employee_name VARCHAR(255),
    organization_reference VARCHAR(64),
    designation VARCHAR(255),
    employment_status VARCHAR(20) DEFAULT 'ACTIVE',
    registered_mobile VARCHAR(16),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mock_government_employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_reference VARCHAR(64) NOT NULL UNIQUE,
    pf_reference VARCHAR(64) NOT NULL,
    identity_reference VARCHAR(64) NOT NULL,
    department VARCHAR(255),
    designation VARCHAR(255),
    jurisdiction VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 22. IDEMPOTENCY KEYS
-- -----------------------------------------------------------

CREATE TABLE idempotency_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    request_method VARCHAR(10),
    request_path VARCHAR(255),
    response_status INT,
    response_body TEXT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    expires_at DATETIME(6) NOT NULL,
    INDEX idx_ik_key (idempotency_key),
    INDEX idx_ik_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;