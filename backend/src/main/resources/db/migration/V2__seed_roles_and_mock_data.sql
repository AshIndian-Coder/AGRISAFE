-- V2: Seed roles and mock government registries

INSERT INTO roles (name, description) VALUES
('ROLE_FARMER', 'Agricultural farmer/producer'),
('ROLE_COLLECTING_AGENT', 'Collection/receiving agent'),
('ROLE_TESTING_AGENT', 'Quality testing agent'),
('ROLE_NODAL_CENTER_AGENT', 'Nodal center operator'),
('ROLE_SUPPLIER', 'Supplier/middleman'),
('ROLE_MANUFACTURER_EMPLOYEE', 'Manufacturing company employee'),
('ROLE_DISTRIBUTOR_EMPLOYEE', 'Distribution company employee'),
('ROLE_RETAILER', 'Retail business'),
('ROLE_GOVERNMENT_EMPLOYEE', 'Government department employee'),
('ROLE_GOVERNMENT_INVESTIGATOR', 'Government investigator'),
('ROLE_SYSTEM_ADMIN', 'System administrator');

-- Mock Aadhaar Registry
INSERT INTO mock_aadhaar_registry (aadhaar_reference, masked_aadhaar, registered_mobile, person_name, date_of_birth, address, status, verification_status) VALUES
('AADHAR-REF-001', 'XXXX-XXXX-0001', '9876543210', 'Rajesh Kumar Patel', '1985-03-15', 'Village Bhopalpur, District Indore, Madhya Pradesh', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-002', 'XXXX-XXXX-0002', '9876543211', 'Sunita Devi Yadav', '1990-07-22', 'Village Rampur, District Lucknow, Uttar Pradesh', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-003', 'XXXX-XXXX-0003', '9876543212', 'Amar Singh Rathore', '1978-11-08', 'Village Kishangarh, District Ajmer, Rajasthan', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-004', 'XXXX-XXXX-0004', '9876543213', 'Priya Sharma', '1992-05-30', 'Village Mohali, District SAS Nagar, Punjab', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-005', 'XXXX-XXXX-0005', '9876543214', 'Mohan Lal Verma', '1982-09-12', 'Village Chhindwara, District Nagpur, Maharashtra', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-010', 'XXXX-XXXX-0010', '9876543220', 'Vikram Singh Chauhan', '1988-01-25', '123 Collector Colony, Bhopal, Madhya Pradesh', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-011', 'XXXX-XXXX-0011', '9876543221', 'Anita Deshmukh', '1991-08-14', '456 Officer Enclave, Nagpur, Maharashtra', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-012', 'XXXX-XXXX-0012', '9876543222', 'Suresh Reddy', '1985-04-19', '789 Staff Quarters, Hyderabad, Telangana', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-020', 'XXXX-XXXX-0020', '9876543230', 'Arvind Gupta', '1980-06-15', 'Milk Colony, Anand, Gujarat', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-021', 'XXXX-XXXX-0021', '9876543231', 'Deepak Joshi', '1987-12-03', 'Oil Mill Road, Solapur, Maharashtra', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-030', 'XXXX-XXXX-0030', '9876543240', 'Kavita Nair', '1993-09-20', 'Rice Mill Area, Thanjavur, Tamil Nadu', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-031', 'XXXX-XXXX-0031', '9876543241', 'Ramesh Bhatia', '1986-02-28', 'Spice Market, Kochi, Kerala', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-040', 'XXXX-XXXX-0040', '9876543250', 'Prakash Rao', '1984-07-10', 'Tea Garden Road, Munnar, Kerala', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-041', 'XXXX-XXXX-0041', '9876543251', 'Lakshmi Narayanan', '1990-11-05', 'Coffee Estate, Chikmagalur, Karnataka', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-050', 'XXXX-XXXX-0050', '9876543260', 'Manjit Kaur', '1983-04-17', 'Wheat Farm, Ludhiana, Punjab', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-051', 'XXXX-XXXX-0051', '9876543261', 'Gurpreet Singh', '1979-08-29', 'Cotton Field, Bhatinda, Punjab', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-100', 'XXXX-XXXX-0100', '9988776655', 'Amit Verma', '1995-05-20', 'Retail Market, Connaught Place, New Delhi', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-101', 'XXXX-XXXX-0101', '9988776656', 'Neha Gupta', '1992-12-10', 'Shop No 5, Local Market, Mumbai, Maharashtra', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-200', 'XXXX-XXXX-0200', '9977665544', 'Rohit Malhotra', '1988-03-25', 'Factory Area, MIDC, Aurangabad, Maharashtra', 'ACTIVE', 'VERIFIED'),
('AADHAR-REF-201', 'XXXX-XXXX-0201', '9977665545', 'Sneha Kapoor', '1991-07-14', 'Industrial Zone, Peenya, Bangalore, Karnataka', 'ACTIVE', 'VERIFIED');

-- Mock PF Registry
INSERT INTO mock_pf_registry (pf_reference, identity_reference, employee_name, organization_reference, designation, employment_status, registered_mobile) VALUES
('PF-AG-001', 'AADHAR-REF-010', 'Vikram Singh Chauhan', 'GOV-MP-001', 'Agriculture Officer', 'ACTIVE', '9876543220'),
('PF-AG-002', 'AADHAR-REF-011', 'Anita Deshmukh', 'GOV-MH-001', 'Food Safety Officer', 'ACTIVE', '9876543221'),
('PF-AG-003', 'AADHAR-REF-012', 'Suresh Reddy', 'GOV-TS-001', 'Quality Inspector', 'ACTIVE', '9876543222'),
('PF-COL-001', 'AADHAR-REF-020', 'Arvind Gupta', 'ORG-COL-001', 'Collection Agent', 'ACTIVE', '9876543230'),
('PF-COL-002', 'AADHAR-REF-021', 'Deepak Joshi', 'ORG-COL-002', 'Collection Agent', 'ACTIVE', '9876543231'),
('PF-SUP-001', 'AADHAR-REF-030', 'Kavita Nair', 'ORG-SUP-001', 'Supplier Manager', 'ACTIVE', '9876543240'),
('PF-SUP-002', 'AADHAR-REF-031', 'Ramesh Bhatia', 'ORG-SUP-002', 'Supplier Coordinator', 'ACTIVE', '9876543241'),
('PF-MFG-001', 'AADHAR-REF-200', 'Rohit Malhotra', 'ORG-MFG-001', 'Production Manager', 'ACTIVE', '9977665544'),
('PF-MFG-002', 'AADHAR-REF-201', 'Sneha Kapoor', 'ORG-MFG-002', 'Quality Lead', 'ACTIVE', '9977665545'),
('PF-DIST-001', 'AADHAR-REF-040', 'Prakash Rao', 'ORG-DIST-001', 'Distribution Manager', 'ACTIVE', '9876543250'),
('PF-DIST-002', 'AADHAR-REF-041', 'Lakshmi Narayanan', 'ORG-DIST-002', 'Logistics Coordinator', 'ACTIVE', '9876543251');

-- Mock Government Employees
INSERT INTO mock_government_employees (employee_reference, pf_reference, identity_reference, department, designation, jurisdiction, active) VALUES
('GOV-EMP-001', 'PF-AG-001', 'AADHAR-REF-010', 'Department of Agriculture', 'Agriculture Officer', 'Madhya Pradesh', TRUE),
('GOV-EMP-002', 'PF-AG-002', 'AADHAR-REF-011', 'Food Safety Department', 'Food Safety Officer', 'Maharashtra', TRUE),
('GOV-EMP-003', 'PF-AG-003', 'AADHAR-REF-012', 'Quality Control', 'Quality Inspector', 'Telangana', TRUE);