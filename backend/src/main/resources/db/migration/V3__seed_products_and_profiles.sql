-- V3: Seed products, varieties, test definitions, and test profiles

-- Products
INSERT INTO products (uuid, product_code, product_name, category, sub_category, default_unit, requires_packaging, requires_manufacturing, fssai_applicable, regulatory_standard_type, description, active) VALUES
(UUID(), 'MILK-001', 'Milk', 'Dairy', 'Liquid Milk', 'Litre', TRUE, TRUE, TRUE, 'FSSAI', 'Fresh milk from dairy farms', TRUE),
(UUID(), 'OIL-001', 'Oil', 'Fats & Oils', 'Edible Oil', 'Litre', TRUE, TRUE, TRUE, 'FSSAI', 'Edible cooking oil', TRUE),
(UUID(), 'RICE-001', 'Rice', 'Cereals', 'Rice Grain', 'Kg', TRUE, FALSE, TRUE, 'FSSAI', 'Rice grain agricultural produce', TRUE),
(UUID(), 'WHEAT-001', 'Wheat', 'Cereals', 'Wheat Grain', 'Kg', TRUE, FALSE, TRUE, 'FSSAI', 'Wheat grain agricultural produce', TRUE),
(UUID(), 'COTTON-001', 'Cotton', 'Fibre Crop', 'Raw Cotton', 'Kg', TRUE, FALSE, FALSE, 'NON_FOOD', 'Raw cotton fibre agricultural produce', TRUE),
(UUID(), 'DAL-001', 'Dal / Pulses', 'Pulses', 'Legumes', 'Kg', TRUE, FALSE, TRUE, 'FSSAI', 'Processed pulses and legumes', TRUE),
(UUID(), 'TURMERIC-001', 'Turmeric', 'Spices', 'Turmeric Rhizome', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Turmeric spice agricultural produce', TRUE),
(UUID(), 'CHILLI-001', 'Chilli Powder', 'Spices', 'Chilli Powder', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Ground chilli powder spice', TRUE),
(UUID(), 'TEA-001', 'Tea', 'Beverages', 'Tea Leaves', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Processed tea leaves', TRUE),
(UUID(), 'COFFEE-001', 'Coffee', 'Beverages', 'Coffee Beans', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Processed coffee beans', TRUE),
(UUID(), 'JOWAR-001', 'Jowar', 'Cereals', 'Sorghum', 'Kg', TRUE, FALSE, TRUE, 'FSSAI', 'Sorghum grain agricultural produce', TRUE),
(UUID(), 'BAJRA-001', 'Bajra', 'Cereals', 'Pearl Millet', 'Kg', TRUE, FALSE, TRUE, 'FSSAI', 'Pearl millet grain agricultural produce', TRUE),
(UUID(), 'RUBBER-001', 'Rubber Sap', 'Plantation', 'Natural Rubber', 'Kg', TRUE, TRUE, FALSE, 'NON_FOOD', 'Natural rubber sap from plantations', TRUE),
(UUID(), 'GROUNDNUT-001', 'Groundnut', 'Oilseeds', 'Peanut', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Groundnut agricultural produce', TRUE),
(UUID(), 'PEPPER-001', 'Black Pepper', 'Spices', 'Black Pepper Corns', 'Kg', TRUE, TRUE, TRUE, 'FSSAI', 'Black pepper spice agricultural produce', TRUE);

-- Milk varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'MILK-COW', 'Cow Milk', TRUE FROM products p WHERE p.product_code='MILK-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'MILK-BUF', 'Buffalo Milk', TRUE FROM products p WHERE p.product_code='MILK-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'MILK-GOAT', 'Goat Milk', TRUE FROM products p WHERE p.product_code='MILK-001';

-- Oil varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'OIL-MUST', 'Mustard Oil', TRUE FROM products p WHERE p.product_code='OIL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'OIL-SUNF', 'Sunflower Oil', TRUE FROM products p WHERE p.product_code='OIL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'OIL-PALM', 'Palm Oil', TRUE FROM products p WHERE p.product_code='OIL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'OIL-GROUN', 'Groundnut Oil', TRUE FROM products p WHERE p.product_code='OIL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'OIL-COCO', 'Coconut Oil', TRUE FROM products p WHERE p.product_code='OIL-001';

-- Rice varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'RICE-BASM', 'Basmati Rice', TRUE FROM products p WHERE p.product_code='RICE-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'RICE-PONN', 'Ponni Rice', TRUE FROM products p WHERE p.product_code='RICE-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'RICE-SONA', 'Sona Masoori', TRUE FROM products p WHERE p.product_code='RICE-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'RICE-IR20', 'IR-20 Rice', TRUE FROM products p WHERE p.product_code='RICE-001';

-- Wheat varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'WHT-SHAR', 'Sharbati Wheat', TRUE FROM products p WHERE p.product_code='WHEAT-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'WHT-LOKW', 'Lokwan Wheat', TRUE FROM products p WHERE p.product_code='WHEAT-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'WHT-DWR', 'DWR Wheat', TRUE FROM products p WHERE p.product_code='WHEAT-001';

-- Dal varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'DAL-TOOR', 'Toor Dal / Pigeon Pea', TRUE FROM products p WHERE p.product_code='DAL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'DAL-MOONG', 'Moong Dal / Green Gram', TRUE FROM products p WHERE p.product_code='DAL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'DAL-URAD', 'Urad Dal / Black Gram', TRUE FROM products p WHERE p.product_code='DAL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'DAL-CHANA', 'Chana Dal / Bengal Gram', TRUE FROM products p WHERE p.product_code='DAL-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'DAL-MASOOR', 'Masoor Dal / Red Lentil', TRUE FROM products p WHERE p.product_code='DAL-001';

-- Turmeric varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TUR-ALLE', 'Alleppey Turmeric', TRUE FROM products p WHERE p.product_code='TURMERIC-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TUR-EROD', 'Erode Turmeric', TRUE FROM products p WHERE p.product_code='TURMERIC-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TUR-SALEM', 'Salem Turmeric', TRUE FROM products p WHERE p.product_code='TURMERIC-001';

-- Groundnut varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'GN-BOLD', 'Bold Groundnut', TRUE FROM products p WHERE p.product_code='GROUNDNUT-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'GN-JAVA', 'Java Groundnut', TRUE FROM products p WHERE p.product_code='GROUNDNUT-001';

-- Black Pepper varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'PEP-MAL', 'Malabar Pepper', TRUE FROM products p WHERE p.product_code='PEPPER-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'PEP-TELL', 'Tellicherry Pepper', TRUE FROM products p WHERE p.product_code='PEPPER-001';

-- Tea varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TEA-ASM', 'Assam Tea', TRUE FROM products p WHERE p.product_code='TEA-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TEA-DARJ', 'Darjeeling Tea', TRUE FROM products p WHERE p.product_code='TEA-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'TEA-NILG', 'Nilgiri Tea', TRUE FROM products p WHERE p.product_code='TEA-001';

-- Coffee varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'COF-ARAB', 'Arabica Coffee', TRUE FROM products p WHERE p.product_code='COFFEE-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'COF-ROBU', 'Robusta Coffee', TRUE FROM products p WHERE p.product_code='COFFEE-001';

-- Cotton varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'COT-BT', 'Bt Cotton', TRUE FROM products p WHERE p.product_code='COTTON-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'COT-ORG', 'Organic Cotton', TRUE FROM products p WHERE p.product_code='COTTON-001';

-- Jowar varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'JWR-MLT', 'Maldandi Jowar', TRUE FROM products p WHERE p.product_code='JOWAR-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'JWR-SHN', 'Shallu Jowar', TRUE FROM products p WHERE p.product_code='JOWAR-001';

-- Bajra varieties
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'BJR-HYB', 'Hybrid Bajra', TRUE FROM products p WHERE p.product_code='BAJRA-001';
INSERT INTO product_varieties (uuid, product_id, variety_code, variety_name, active)
SELECT UUID(), p.id, 'BJR-LOC', 'Local Bajra', TRUE FROM products p WHERE p.product_code='BAJRA-001';

-- Test Definitions
INSERT INTO test_definitions (uuid, test_code, test_name, test_category, unit, description, active) VALUES
(UUID(), 'MOISTURE', 'Moisture Content', 'General Quality', '%', 'Moisture percentage in agricultural produce', TRUE),
(UUID(), 'FOREIGN-MATTER', 'Foreign Matter', 'Purity', '%', 'Percentage of foreign matter/impurities', TRUE),
(UUID(), 'DAMAGED-GRAIN', 'Damaged Grain', 'Quality', '%', 'Percentage of damaged/discoloured grains', TRUE),
(UUID(), 'MILK-FAT', 'Milk Fat Content', 'Dairy', '%', 'Percentage milk fat content', TRUE),
(UUID(), 'MILK-SNF', 'Milk Solids-Not-Fat', 'Dairy', '%', 'Solids not fat content in milk', TRUE),
(UUID(), 'MILK-DENSITY', 'Milk Density', 'Dairy', 'g/mL', 'Density of milk at standard temperature', TRUE),
(UUID(), 'OIL-DENSITY', 'Oil Density/Specific Gravity', 'Oils & Fats', 'g/mL', 'Density of oil at reference temperature', TRUE),
(UUID(), 'OIL-FREE-FA', 'Free Fatty Acids', 'Oils & Fats', '%', 'Free fatty acid content as oleic acid', TRUE),
(UUID(), 'OIL-MOISTURE', 'Oil Moisture Content', 'Oils & Fats', '%', 'Moisture and volatile matter in oil', TRUE),
(UUID(), 'SPICE-MOISTURE', 'Spice Moisture Content', 'Spices', '%', 'Moisture content in spices', TRUE),
(UUID(), 'SPICE-ASH', 'Total Ash Content', 'Spices', '%', 'Total ash content', TRUE),
(UUID(), 'SPICE-ACID-INSOL-ASH', 'Acid Insoluble Ash', 'Spices', '%', 'Acid insoluble ash content', TRUE),
(UUID(), 'SPICE-CURCUMIN', 'Curcumin Content', 'Spices', '%', 'Curcumin percentage in turmeric', TRUE),
(UUID(), 'SPICE-VOLATILE-OIL', 'Volatile Oil Content', 'Spices', '%', 'Volatile oil content in spices', TRUE),
(UUID(), 'BEV-MOISTURE', 'Beverage Moisture', 'Beverages', '%', 'Moisture in processed tea/coffee', TRUE),
(UUID(), 'BEV-CAFFEINE', 'Caffeine Content', 'Beverages', '%', 'Caffeine percentage', TRUE),
(UUID(), 'BEV-TOTAL-ASH', 'Total Ash (Beverages)', 'Beverages', '%', 'Total ash content in beverages', TRUE),
(UUID(), 'CEREAL-MOISTURE', 'Cereal Moisture', 'Cereals', '%', 'Moisture in cereal grains', TRUE),
(UUID(), 'CEREAL-FOREIGN', 'Cereal Foreign Matter', 'Cereals', '%', 'Foreign matter in cereal grains', TRUE),
(UUID(), 'CEREAL-DAMAGED', 'Damaged Grains', 'Cereals', '%', 'Damaged/discoloured grain percentage', TRUE),
(UUID(), 'VISUAL-QUALITY', 'Visual Quality Inspection', 'Quality', 'Grade', 'Subjective visual quality assessment', TRUE);

-- Test Profiles
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Milk Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='MILK-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Oil Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='OIL-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Rice Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='RICE-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Wheat Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='WHEAT-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Dal Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='DAL-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Turmeric Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='TURMERIC-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Chilli Powder Quality', p.id, TRUE FROM products p WHERE p.product_code='CHILLI-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Tea Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='TEA-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Coffee Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='COFFEE-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Jowar Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='JOWAR-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Bajra Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='BAJRA-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Groundnut Standard Quality', p.id, TRUE FROM products p WHERE p.product_code='GROUNDNUT-001';
INSERT INTO test_profiles (uuid, profile_name, product_id, active)
SELECT UUID(), 'Black Pepper Quality', p.id, TRUE FROM products p WHERE p.product_code='PEPPER-001';

-- Profile-Test mappings
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Milk Standard Quality' AND td.test_code='MILK-FAT';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Milk Standard Quality' AND td.test_code='MILK-SNF';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Milk Standard Quality' AND td.test_code='MILK-DENSITY';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Oil Standard Quality' AND td.test_code='OIL-DENSITY';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Oil Standard Quality' AND td.test_code='OIL-FREE-FA';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Oil Standard Quality' AND td.test_code='OIL-MOISTURE';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Rice Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Rice Standard Quality' AND td.test_code='CEREAL-FOREIGN';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Rice Standard Quality' AND td.test_code='CEREAL-DAMAGED';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Wheat Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Wheat Standard Quality' AND td.test_code='CEREAL-FOREIGN';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Wheat Standard Quality' AND td.test_code='CEREAL-DAMAGED';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Turmeric Standard Quality' AND td.test_code='SPICE-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Turmeric Standard Quality' AND td.test_code='SPICE-CURCUMIN';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Turmeric Standard Quality' AND td.test_code='SPICE-ASH';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Chilli Powder Quality' AND td.test_code='SPICE-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Chilli Powder Quality' AND td.test_code='SPICE-ASH';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Chilli Powder Quality' AND td.test_code='SPICE-ACID-INSOL-ASH';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Tea Standard Quality' AND td.test_code='BEV-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Tea Standard Quality' AND td.test_code='BEV-TOTAL-ASH';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, FALSE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Tea Standard Quality' AND td.test_code='BEV-CAFFEINE';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Coffee Standard Quality' AND td.test_code='BEV-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Coffee Standard Quality' AND td.test_code='BEV-TOTAL-ASH';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, FALSE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Coffee Standard Quality' AND td.test_code='BEV-CAFFEINE';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Jowar Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Jowar Standard Quality' AND td.test_code='CEREAL-FOREIGN';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Bajra Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Bajra Standard Quality' AND td.test_code='CEREAL-FOREIGN';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Groundnut Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Groundnut Standard Quality' AND td.test_code='FOREIGN-MATTER';

INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Black Pepper Quality' AND td.test_code='SPICE-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Black Pepper Quality' AND td.test_code='SPICE-VOLATILE-OIL';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Black Pepper Quality' AND td.test_code='FOREIGN-MATTER';

-- Dal profile
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 1 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Dal Standard Quality' AND td.test_code='CEREAL-MOISTURE';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 2 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Dal Standard Quality' AND td.test_code='CEREAL-FOREIGN';
INSERT INTO profile_test_mapping (profile_id, test_definition_id, mandatory, sort_order)
SELECT tp.id, td.id, TRUE, 3 FROM test_profiles tp, test_definitions td WHERE tp.profile_name='Dal Standard Quality' AND td.test_code='CEREAL-DAMAGED';