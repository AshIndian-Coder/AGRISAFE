ALTER TABLE qr_credentials ADD COLUMN sibling_group_id VARCHAR(64) NULL;
CREATE INDEX idx_qr_sibling_group ON qr_credentials (sibling_group_id);
