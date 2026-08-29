-- V4: Seed FSSAI food standards reference data
-- Source: FSSAI Food Safety and Standards (Food Products Standards and Food Additives) Regulations, 2011
-- Each entry includes source metadata for traceability

-- Milk Standard (Regulation 2.1 - Dairy)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Milk Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.1', '2.1.1 - Milk', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

-- Add standard requirements for Milk (Cow Milk)
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'MILK-FAT', 'Milk Fat Content', 'Fat', '%', 3.0, 6.0, TRUE, 'IS 1479 (Part I) - Methods of test for dairy industry', 'Cow milk minimum 3.0% fat per FSSAI Reg 2.1.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Milk Standards 2011' AND p.product_code='MILK-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'MILK-SNF', 'Milk Solids-Not-Fat', 'SNF', '%', 8.5, NULL, TRUE, 'IS 1479 (Part I) - Methods of test for dairy industry', 'Cow milk minimum 8.5% SNF per FSSAI Reg 2.1.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Milk Standards 2011' AND p.product_code='MILK-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'MILK-DENSITY', 'Milk Density', 'Density', 'g/mL', 1.026, 1.035, TRUE, 'IS 1479 (Part I) - Methods of test for dairy industry', 'Cow milk density range per FSSAI standards'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Milk Standards 2011' AND p.product_code='MILK-001';

-- Oils Standard (Regulation 2.2 - Fats, Oils and Fat Emulsions)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Oils Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.2', '2.2.1 - Oils/Fats', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'OIL-DENSITY', 'Oil Density', 'Specific Gravity', 'g/mL', 0.90, 0.93, TRUE, 'IS 548 - Methods of test for oils and fats', 'Refined vegetable oil density range per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Oils Standards 2011' AND p.product_code='OIL-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'OIL-FREE-FA', 'Free Fatty Acids', 'FFA as Oleic Acid', '%', NULL, 0.25, TRUE, 'IS 548 - Methods of test for oils and fats', 'Max 0.25% FFA for refined oils per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Oils Standards 2011' AND p.product_code='OIL-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'OIL-MOISTURE', 'Oil Moisture Content', 'Moisture & Volatile Matter', '%', NULL, 0.10, TRUE, 'IS 548 - Methods of test for oils and fats', 'Max 0.10% moisture for refined oils per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Oils Standards 2011' AND p.product_code='OIL-001';

-- Cereals Standard (Regulation 2.3 - Cereals and Cereal Products)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Cereals Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.3', '2.3.1 - Cereals', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

-- Rice Requirements
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Rice', 'Moisture', '%', NULL, 14.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 14% moisture for rice per FSSAI Reg 2.3.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='RICE-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-FOREIGN', 'Foreign Matter in Rice', 'Foreign Matter', '%', NULL, 1.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 1% foreign matter per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='RICE-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-DAMAGED', 'Damaged Grains in Rice', 'Damaged/Discoloured', '%', NULL, 5.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 5% damaged grains per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='RICE-001';

-- Wheat Requirements
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Wheat', 'Moisture', '%', NULL, 14.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 14% moisture for wheat per FSSAI Reg 2.3.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='WHEAT-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-FOREIGN', 'Foreign Matter in Wheat', 'Foreign Matter', '%', NULL, 1.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 1% foreign matter per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='WHEAT-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-DAMAGED', 'Damaged Grains in Wheat', 'Damaged/Discoloured', '%', NULL, 5.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 5% damaged grains per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='WHEAT-001';

-- Jowar & Bajra Requirements
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Jowar', 'Moisture', '%', NULL, 14.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 14% moisture per FSSAI standards'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='JOWAR-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-FOREIGN', 'Foreign Matter in Jowar', 'Foreign Matter', '%', NULL, 1.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 1% foreign matter per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='JOWAR-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Bajra', 'Moisture', '%', NULL, 14.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 14% moisture per FSSAI standards'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='BAJRA-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-FOREIGN', 'Foreign Matter in Bajra', 'Foreign Matter', '%', NULL, 1.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 1% foreign matter per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='BAJRA-001';

-- Spices Standard (Regulation 2.4 - Spices and Condiments)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Spices Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.4', '2.4.1 - Spices', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

-- Turmeric
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-MOISTURE', 'Moisture in Turmeric', 'Moisture', '%', NULL, 10.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 10% moisture for turmeric per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='TURMERIC-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-CURCUMIN', 'Curcumin Content', 'Curcumin', '%', 2.0, NULL, TRUE, 'IS 1797 - Methods of test for spices', 'Min 2% curcumin for turmeric per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='TURMERIC-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-ASH', 'Total Ash in Turmeric', 'Total Ash', '%', NULL, 8.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 8% total ash for turmeric per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='TURMERIC-001';

-- Chilli Powder
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-MOISTURE', 'Moisture in Chilli', 'Moisture', '%', NULL, 10.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 10% moisture for chilli per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='CHILLI-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-ASH', 'Total Ash in Chilli', 'Total Ash', '%', NULL, 8.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 8% total ash for chilli per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='CHILLI-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-ACID-INSOL-ASH', 'Acid Insoluble Ash in Chilli', 'Acid Insoluble Ash', '%', NULL, 1.5, TRUE, 'IS 1797 - Methods of test for spices', 'Max 1.5% acid insoluble ash for chilli per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='CHILLI-001';

-- Black Pepper
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-MOISTURE', 'Moisture in Black Pepper', 'Moisture', '%', NULL, 12.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 12% moisture for black pepper per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='PEPPER-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'SPICE-VOLATILE-OIL', 'Volatile Oil in Black Pepper', 'Volatile Oil', '%', 1.0, NULL, TRUE, 'IS 1797 - Methods of test for spices', 'Min 1% volatile oil for black pepper per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='PEPPER-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'FOREIGN-MATTER', 'Foreign Matter in Black Pepper', 'Foreign Matter', '%', NULL, 1.0, TRUE, 'IS 1797 - Methods of test for spices', 'Max 1% foreign matter for black pepper per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Spices Standards 2011' AND p.product_code='PEPPER-001';

-- Tea Standard (Regulation 2.7 - Tea, Coffee and Chicory)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Tea Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.7', '2.7.1 - Tea', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'BEV-MOISTURE', 'Moisture in Tea', 'Moisture', '%', NULL, 7.0, TRUE, 'IS 13854 - Methods of test for tea', 'Max 7% moisture in tea per FSSAI Reg 2.7.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Tea Standards 2011' AND p.product_code='TEA-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'BEV-TOTAL-ASH', 'Total Ash in Tea', 'Total Ash', '%', 3.0, 8.0, TRUE, 'IS 13854 - Methods of test for tea', 'Total ash 3-8% for tea per FSSAI Reg 2.7.1'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Tea Standards 2011' AND p.product_code='TEA-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'BEV-CAFFEINE', 'Caffeine in Tea', 'Caffeine', '%', 1.0, NULL, FALSE, 'IS 13854 - Methods of test for tea', 'Min 1% caffeine in tea per FSSAI (non-mandatory threshold)'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Tea Standards 2011' AND p.product_code='TEA-001';

-- Coffee Standard (Regulation 2.7 - Coffee)
INSERT INTO food_standards (uuid, standard_name, regulation_name, chapter, section_reference, source_url, source_document, standard_version, effective_from, retrieved_at, active) VALUES
(UUID(), 'FSSAI Coffee Standards 2011', 'FSS (Food Products Standards and Food Additives) Regulations, 2011', '2.7', '2.7.2 - Coffee', 'https://www.fssai.gov.in/upload/uploadfiles/files/Compendium_Regulation_English_1_09_2020.pdf', 'FSSAI Compendium of Regulations 2011', 'v1.0', '2011-08-05', '2026-01-15', TRUE);

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'BEV-MOISTURE', 'Moisture in Coffee', 'Moisture', '%', NULL, 5.0, TRUE, 'IS 13854 - Methods of test for coffee', 'Max 5% moisture in coffee per FSSAI Reg 2.7.2'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Coffee Standards 2011' AND p.product_code='COFFEE-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'BEV-TOTAL-ASH', 'Total Ash in Coffee', 'Total Ash', '%', 2.0, 5.0, TRUE, 'IS 13854 - Methods of test for coffee', 'Total ash 2-5% for coffee per FSSAI Reg 2.7.2'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Coffee Standards 2011' AND p.product_code='COFFEE-001';

-- Pulses Standard (Under Cereals - applicable to Dal as they fall under grains category)
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Dal/Pulses', 'Moisture', '%', NULL, 14.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 14% moisture for pulses per FSSAI standards'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='DAL-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-FOREIGN', 'Foreign Matter in Dal/Pulses', 'Foreign Matter', '%', NULL, 2.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 2% foreign matter for pulses per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='DAL-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-DAMAGED', 'Damaged Grains in Dal', 'Damaged/Discoloured', '%', NULL, 5.0, TRUE, 'IS 4333 - Methods of test for cereals', 'Max 5% damaged grains for pulses per FSSAI'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Cereals Standards 2011' AND p.product_code='DAL-001';

-- Groundnut (Oilseeds - applicable FSSAI standards under Regulation 2.2)
INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'CEREAL-MOISTURE', 'Moisture in Groundnut', 'Moisture', '%', NULL, 8.0, TRUE, 'IS 548 - Methods for oilseeds testing', 'Max 8% moisture for groundnut per FSSAI standards'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Oils Standards 2011' AND p.product_code='GROUNDNUT-001';

INSERT INTO standard_requirements (uuid, standard_id, product_id, test_code, test_name, parameter, unit, minimum_value, maximum_value, mandatory, test_method_reference, notes)
SELECT UUID(), fs.id, p.id, 'FOREIGN-MATTER', 'Foreign Matter in Groundnut', 'Foreign Matter', '%', NULL, 2.0, TRUE, 'IS 548 - Methods for oilseeds testing', 'Max 2% foreign matter for groundnut'
FROM food_standards fs, products p WHERE fs.standard_name='FSSAI Oils Standards 2011' AND p.product_code='GROUNDNUT-001';