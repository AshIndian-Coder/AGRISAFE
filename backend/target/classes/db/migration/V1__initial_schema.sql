-- AgriChain Database Schema
-- MySQL 8.0+

-- ============================================================
-- ORGANIZATIONS
-- ============================================================
CREATE TABLE organizations (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    registration_number VARCHAR(100),
    tax_id VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(254),
    address JSON,
    location JSON,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_organizations_type (type),
    INDEX idx_organizations_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(254),
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'FARMER',
    organization_id CHAR(36),
    preferred_language VARCHAR(5) NOT NULL DEFAULT 'en',
    profile_image_url VARCHAR(2048),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP NULL,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    UNIQUE INDEX idx_users_phone (phone),
    INDEX idx_users_email (email),
    INDEX idx_users_organization (organization_id),
    INDEX idx_users_role (role),
    INDEX idx_users_is_active (is_active),
    
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SESSIONS
-- ============================================================
CREATE TABLE sessions (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    refresh_token_hash VARCHAR(255) NOT NULL,
    device_id VARCHAR(128),
    device_name VARCHAR(128),
    device_platform VARCHAR(20),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP NULL,
    revoked_reason VARCHAR(255),
    last_used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_sessions_user (user_id),
    INDEX idx_sessions_expires (expires_at),
    INDEX idx_sessions_device (device_id),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- OTP CODES
-- ============================================================
CREATE TABLE otp_codes (
    id CHAR(36) PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_otp_phone_purpose (phone, purpose),
    INDEX idx_otp_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- FARMERS
-- ============================================================
CREATE TABLE farmers (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL UNIQUE,
    organization_id CHAR(36),
    aadhaar_number_hash VARCHAR(255),
    farmer_id_number VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20),
    address JSON,
    bank_account_details JSON,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    verification_method VARCHAR(50),
    total_farms INT NOT NULL DEFAULT 0,
    total_area_hectares DECIMAL(12,4) DEFAULT 0,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_farmers_organization (organization_id),
    INDEX idx_farmers_is_verified (is_verified),
    UNIQUE INDEX idx_farmers_farmer_id (farmer_id_number),
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- FARMS
-- ============================================================
CREATE TABLE farms (
    id CHAR(36) PRIMARY KEY,
    farmer_id CHAR(36) NOT NULL,
    organization_id CHAR(36),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    area_hectares DECIMAL(12,4) NOT NULL,
    location JSON NOT NULL,
    address JSON NOT NULL,
    soil_type VARCHAR(100),
    water_source VARCHAR(50),
    certifications JSON,
    khasra_number VARCHAR(100),
    land_record_id VARCHAR(100),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_farms_farmer (farmer_id),
    INDEX idx_farms_organization (organization_id),
    INDEX idx_farms_is_active (is_active),
    
    FOREIGN KEY (farmer_id) REFERENCES farmers(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    
    CONSTRAINT chk_farms_area_positive CHECK (area_hectares > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- PRODUCTS
-- ============================================================
CREATE TABLE products (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    variety VARCHAR(100),
    description TEXT,
    unit VARCHAR(20) NOT NULL,
    hs_code VARCHAR(20),
    shelf_life_days INT,
    storage_requirements TEXT,
    image_url VARCHAR(2048),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_products_category (category),
    INDEX idx_products_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- BATCHES
-- ============================================================
CREATE TABLE batches (
    id CHAR(36) PRIMARY KEY,
    batch_code VARCHAR(50) NOT NULL UNIQUE,
    product_id CHAR(36) NOT NULL,
    farm_id CHAR(36) NOT NULL,
    farmer_id CHAR(36) NOT NULL,
    organization_id CHAR(36),
    current_holder_id CHAR(36),
    quantity DECIMAL(15,4) NOT NULL,
    remaining_quantity DECIMAL(15,4) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    previous_status VARCHAR(30),
    harvest_date TIMESTAMP NOT NULL,
    expected_expiry_date TIMESTAMP NULL,
    farming_method VARCHAR(50),
    cultivation_start_date TIMESTAMP NULL,
    location JSON,
    notes TEXT,
    certifications JSON,
    quality_grade VARCHAR(50),
    price_per_unit DECIMAL(15,4),
    currency VARCHAR(3) DEFAULT 'INR',
    qr_code_url VARCHAR(2048),
    risk_score DECIMAL(5,2),
    risk_score_updated_at TIMESTAMP NULL,
    client_operation_id VARCHAR(128) UNIQUE,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_batches_product (product_id),
    INDEX idx_batches_farm (farm_id),
    INDEX idx_batches_farmer (farmer_id),
    INDEX idx_batches_organization (organization_id),
    INDEX idx_batches_current_holder (current_holder_id),
    INDEX idx_batches_status (status),
    INDEX idx_batches_harvest_date (harvest_date),
    INDEX idx_batches_created_at (created_at),
    
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (farm_id) REFERENCES farms(id),
    FOREIGN KEY (farmer_id) REFERENCES farmers(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (current_holder_id) REFERENCES organizations(id),
    
    CONSTRAINT chk_batches_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_batches_remaining_positive CHECK (remaining_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TRACEABILITY EVENTS
-- ============================================================
CREATE TABLE traceability_events (
    id CHAR(36) PRIMARY KEY,
    batch_id CHAR(36) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_version INT NOT NULL DEFAULT 1,
    actor_id CHAR(36),
    actor_organization_id CHAR(36),
    previous_state VARCHAR(50),
    new_state VARCHAR(50),
    location JSON,
    server_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_timestamp TIMESTAMP NULL,
    data JSON,
    metadata JSON,
    correlation_id VARCHAR(128),
    causation_id VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_traceability_batch (batch_id),
    INDEX idx_traceability_event_type (event_type),
    INDEX idx_traceability_actor (actor_id),
    INDEX idx_traceability_timestamp (server_timestamp),
    INDEX idx_traceability_correlation (correlation_id),
    
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (actor_id) REFERENCES users(id),
    FOREIGN KEY (actor_organization_id) REFERENCES organizations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- HANDOVERS
-- ============================================================
CREATE TABLE handovers (
    id CHAR(36) PRIMARY KEY,
    batch_id CHAR(36) NOT NULL,
    from_organization_id CHAR(36) NOT NULL,
    from_user_id CHAR(36) NOT NULL,
    to_organization_id CHAR(36) NOT NULL,
    to_user_id CHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    previous_status VARCHAR(20),
    quantity DECIMAL(15,4) NOT NULL,
    accepted_quantity DECIMAL(15,4),
    unit VARCHAR(20) NOT NULL,
    scheduled_date TIMESTAMP NULL,
    initiated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    pickup_location JSON,
    delivery_location JSON,
    actual_pickup_location JSON,
    actual_delivery_location JSON,
    transport_mode VARCHAR(50),
    vehicle_number VARCHAR(50),
    driver_phone VARCHAR(20),
    expected_temperature_range JSON,
    actual_condition VARCHAR(50),
    condition_notes TEXT,
    rejection_reason TEXT,
    images JSON,
    notes TEXT,
    idempotency_key VARCHAR(128) UNIQUE,
    metadata JSON,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_handovers_batch (batch_id),
    INDEX idx_handovers_from_org (from_organization_id),
    INDEX idx_handovers_to_org (to_organization_id),
    INDEX idx_handovers_status (status),
    INDEX idx_handovers_scheduled (scheduled_date),
    
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (from_organization_id) REFERENCES organizations(id),
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_organization_id) REFERENCES organizations(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id),
    
    CONSTRAINT chk_handovers_quantity_positive CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- AUDIT LOGS
-- ============================================================
CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    actor_id CHAR(36),
    actor_organization_id CHAR(36),
    action VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id CHAR(36),
    previous_state JSON,
    new_state JSON,
    changes JSON,
    reason TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    trace_id VARCHAR(128),
    request_id VARCHAR(128),
    result VARCHAR(20) NOT NULL,
    error_code VARCHAR(50),
    metadata JSON,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_audit_actor (actor_id),
    INDEX idx_audit_resource (resource_type, resource_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_timestamp (timestamp),
    INDEX idx_audit_trace (trace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Insert default products
-- ============================================================
INSERT INTO products (id, name, category, unit, description) VALUES
(UUID(), 'Wheat', 'Grains', 'QUINTAL', 'Common wheat grain'),
(UUID(), 'Rice (Basmati)', 'Grains', 'QUINTAL', 'Basmati rice'),
(UUID(), 'Tomatoes', 'Vegetables', 'KG', 'Fresh tomatoes'),
(UUID(), 'Onions', 'Vegetables', 'KG', 'Fresh onions'),
(UUID(), 'Potatoes', 'Vegetables', 'KG', 'Fresh potatoes'),
(UUID(), 'Mangoes (Alphonso)', 'Fruits', 'KG', 'Alphonso mangoes'),
(UUID(), 'Apples (Kashmiri)', 'Fruits', 'KG', 'Kashmiri apples'),
(UUID(), 'Milk', 'Dairy', 'LITRE', 'Fresh cow milk'),
(UUID(), 'Sugarcane', 'Cash Crops', 'TON', 'Sugarcane stalks'),
(UUID(), 'Cotton', 'Cash Crops', 'QUINTAL', 'Raw cotton');
