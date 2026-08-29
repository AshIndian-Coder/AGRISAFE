export type UserRole =
  | 'ROLE_FARMER'
  | 'ROLE_COLLECTING_AGENT'
  | 'ROLE_TESTING_AGENT'
  | 'ROLE_NODAL_CENTER_AGENT'
  | 'ROLE_SUPPLIER'
  | 'ROLE_MANUFACTURER_EMPLOYEE'
  | 'ROLE_DISTRIBUTOR_EMPLOYEE'
  | 'ROLE_RETAILER'
  | 'ROLE_GOVERNMENT_EMPLOYEE'
  | 'ROLE_GOVERNMENT_INVESTIGATOR'
  | 'ROLE_SYSTEM_ADMIN';

export type UserType =
  | 'FARMER'
  | 'COLLECTING_AGENT'
  | 'TESTING_AGENT'
  | 'NODAL_CENTER_AGENT'
  | 'SUPPLIER'
  | 'MANUFACTURER_EMPLOYEE'
  | 'DISTRIBUTOR_EMPLOYEE'
  | 'RETAILER'
  | 'GOVERNMENT_EMPLOYEE'
  | 'GOVERNMENT_INVESTIGATOR'
  | 'SYSTEM_ADMIN';

export type LotStatus =
  | 'CREATED'
  | 'ACCEPTED'
  | 'AT_NODAL_CENTER'
  | 'PACKAGED'
  | 'IN_TRANSIT'
  | 'AT_SUPPLIER'
  | 'TESTING'
  | 'TEST_PASSED'
  | 'TEST_FAILED'
  | 'AT_MANUFACTURER'
  | 'PROCESSED'
  | 'MANUFACTURER_TEST_PASSED'
  | 'MANUFACTURER_TEST_FAILED'
  | 'BUNDLED'
  | 'AT_DISTRIBUTOR'
  | 'DISTRIBUTOR_VERIFIED'
  | 'AT_RETAILER'
  | 'READY_FOR_SALE'
  | 'SOLD'
  | 'QUARANTINED'
  | 'REJECTED'
  | 'RECALLED'
  | 'SUSPENDED';

export type QrStatus = 'ACTIVE' | 'CONSUMED' | 'EXPIRED' | 'REVOKED' | 'SUSPENDED';

export type TestResult = 'PASS' | 'FAIL' | 'HOLD' | 'PENDING' | 'NOT_TESTED';

export type FlagSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface ApiResponse<T> {
  success: boolean;
  status: number;
  code?: string;
  message?: string;
  data: T;
  traceId?: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  empty: boolean;
}

export interface AuthResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
  user_uuid: string;
  user_name: string;
  user_type: string;
  role: string;
  organization_id?: number;
}

export interface LoginRequest {
  identity: string;
  pin: string;
}

export interface FarmerRegistrationRequest {
  aadhaarReference: string;
  otp: string;
  pin: string;
}

export interface PfRegistrationRequest {
  pfReference: string;
  aadhaarReference: string;
  otp: string;
  pin: string;
  userType: string;
  functionalType?: string;
}

export interface EmployeeRegistrationRequest {
  employeeId: string;
  aadhaarReference: string;
  organizationId: number;
  otp: string;
  pin: string;
  userType: string;
  functionalType?: string;
}

export interface RetailerRegistrationRequest {
  gstNumber: string;
  aadhaarReference: string;
  otp: string;
  pin: string;
}

export interface Lot {
  lotId: string;
  farmerUuid: string;
  productId: number;
  varietyId?: number;
  quantity: number;
  unit?: string;
  status: LotStatus;
  originLatitude?: number;
  originLongitude?: number;
  originAddress?: string;
  estimatedValue?: number;
  currentCustodianUuid?: string;
  currentCustodianRole?: string;
  qrId?: string;
  acceptedAt?: string;
  createdAt: string;
  updatedAt?: string;
  recalled: boolean;
  notes?: string;
}

export interface LotCreateRequest {
  productId: number;
  varietyId?: number;
  quantity: number;
  unit?: string;
  latitude?: number;
  longitude?: number;
  originAddress?: string;
  estimatedValue?: number;
  notes?: string;
}

export interface Package {
  packageId: string;
  lotId: string;
  quantity: number;
  unit?: string;
  packageType?: string;
  status: LotStatus;
  currentCustodianUuid?: string;
  currentCustodianRole?: string;
  qrId?: string;
  testingStatus?: string;
  quarantined: boolean;
  recalled: boolean;
  notes?: string;
  createdAt: string;
}

export interface PackageSplitRequest {
  quantities: number[];
  packageType?: string;
  notes?: string;
}

export interface Bundle {
  bundleId: string;
  manufacturerLotId: string;
  bundleType: string;
  quantity: number;
  unit: string;
  status: LotStatus;
  currentCustodianUuid?: string;
  currentCustodianRole?: string;
  qrId?: string;
  recalled: boolean;
  quarantined: boolean;
  retailerReceived: boolean;
  distributorVerified: boolean;
  notes?: string;
  createdAt: string;
}

export interface TestSubmitRequest {
  packageId: string;
  testProfileId: number;
  testDefinitionId?: number;
  standardRequirementId?: number;
  measuredValue: string;
  unit?: string;
  mandatory?: boolean;
  qrId?: string;
}

export interface TestResultResponse {
  testRecordId: string;
  objectType: string;
  objectId: string;
  testProfileId: number;
  testDefinitionId?: number;
  testerUuid: string;
  measurementSource: string;
  measuredValue: string;
  unit?: string;
  result: TestResult;
  standardName?: string;
  minThreshold?: string;
  maxThreshold?: string;
  mandatory: boolean;
  testedAt: string;
  anomalyFlag: boolean;
}

export interface ManufacturerLot {
  manufacturerLotId: string;
  productId: number;
  manufacturerEmployeeUuid: string;
  productionQuantity: number;
  unit?: string;
  facilityName?: string;
  status: LotStatus;
  testingStatus?: string;
  qrId?: string;
  inputLotIds: string[];
  recalled: boolean;
  notes?: string;
  createdAt: string;
  bundles?: ManufacturerBundle[];
}

export interface ManufacturerBundle {
  bundleId: string;
  bundleType: string;
  quantity: number;
  unit: string;
  qrId: string;
  status: LotStatus;
  createdAt: string;
}

export interface ManufacturerLotCreateRequest {
  productId: number;
  inputLotIds: string[];
  productionQuantity: number;
  unit?: string;
  facilityName?: string;
  notes?: string;
}

export interface Product {
  id: number;
  name: string;
  category?: string;
  baseUnit?: string;
  unit?: string;
  description?: string;
  active?: boolean;
}

export interface ProductVariety {
  id: number;
  name: string;
  active?: boolean;
}

export interface ProductVerification {
  verificationStatus: string;
  productName?: string;
  manufacturer?: string;
  manufacturedAt?: string;
  qualityStatus?: string;
  traceabilityComplete: boolean;
  retailerReceived: boolean;
  recalled: boolean;
  reason?: string;
  traceEventCount: number;
}

export interface Flag {
  id: number;
  flagType?: string;
  type?: string;
  severity?: string;
  entityType?: string;
  entityId?: string;
  status: string;
  description?: string;
  reason?: string;
  resolution?: string;
  assignedInvestigator?: string;
  investigatorUuid?: string;
  createdAt: string;
}

export interface Complaint {
  complaintId: string;
  id?: string;
  category?: string;
  description?: string;
  status: string;
  resolution?: string;
  createdAt: string;
}

export interface ComplaintRequest {
  category: string;
  description: string;
}

export interface TraceEvent {
  eventId?: string;
  eventType?: string;
  event?: string;
  type?: string;
  action?: string;
  actorUuid?: string;
  actor?: string;
  actorRole?: string;
  role?: string;
  objectType?: string;
  objectId?: string;
  previousState?: string;
  newState?: string;
  details?: string;
  metadata?: string;
  metadataJson?: string;
  latitude?: number;
  longitude?: number;
  qrId?: string;
  eventHash?: string;
  createdAt?: string;
  occurredAt?: string;
  eventTimestamp?: string;
}

export interface QrCredential {
  qrId: string;
  status: QrStatus;
  entityType?: string;
  entityId?: string;
  type?: string;
  createdAt?: string;
  expiresAt?: string;
}

export interface FullInvestigation {
  lot?: Lot;
  traceEvents: TraceEvent[];
  flags: Flag[];
}
