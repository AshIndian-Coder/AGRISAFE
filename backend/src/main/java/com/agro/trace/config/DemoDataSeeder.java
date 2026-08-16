package com.agro.trace.config;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.domain.TestResult;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.packages.repository.PackageRepository;
import com.agro.trace.qr.repository.QrCredentialRepository;
import com.agro.trace.traceability.domain.TraceEvent;
import com.agro.trace.traceability.repository.TraceEventRepository;
import com.agro.trace.testing.domain.TestRecord;
import com.agro.trace.testing.repository.TestRecordRepository;
import com.agro.trace.complaints.domain.Complaint;
import com.agro.trace.complaints.repository.ComplaintRepository;
import com.agro.trace.fraud.domain.Flag;
import com.agro.trace.fraud.repository.FlagRepository;
import com.agro.trace.manufacturing.domain.ManufacturerLot;
import com.agro.trace.manufacturing.domain.ManufacturerLotInput;
import com.agro.trace.manufacturing.repository.ManufacturerLotInputRepository;
import com.agro.trace.manufacturing.repository.ManufacturerLotRepository;
import com.agro.trace.bundles.domain.Bundle;
import com.agro.trace.bundles.repository.BundleRepository;
import com.agro.trace.users.domain.Role;
import com.agro.trace.users.domain.User;
import com.agro.trace.users.domain.UserType;
import com.agro.trace.users.repository.RoleRepository;
import com.agro.trace.users.repository.UserRepository;
import com.agro.trace.products.domain.Product;
import com.agro.trace.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LotRepository lotRepository;
    private final PackageRepository packageRepository;
    private final TraceEventRepository traceEventRepository;
    private final TestRecordRepository testRecordRepository;
    private final ManufacturerLotRepository manufacturerLotRepository;
    private final ManufacturerLotInputRepository manufacturerLotInputRepository;
    private final BundleRepository bundleRepository;
    private final ComplaintRepository complaintRepository;
    private final FlagRepository flagRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 5) {
            log.info("Demo data already exists, skipping.");
            return;
        }
        log.info("======= SEEDING DEMO DATA =======");

        String farmerUuid = createUser("AADHAR-DEMO-FARMER", "Ramesh Farmer", null, UserType.FARMER, "ROLE_FARMER", "9988770011");
        String agentUuid = createUser("AADHAR-DEMO-AGENT", "Suresh Agent", "PF-COL-DEMO", UserType.COLLECTING_AGENT, "ROLE_COLLECTING_AGENT", "9988770022");
        String supplierUuid = createUser("AADHAR-DEMO-SUP", "Amar Supplier", "PF-SUP-DEMO", UserType.SUPPLIER, "ROLE_SUPPLIER", "9988770033");
        String mfgUuid = createUser("AADHAR-DEMO-MFG", "Rohit Mfg", "PF-MFG-DEMO", UserType.MANUFACTURER_EMPLOYEE, "ROLE_MANUFACTURER_EMPLOYEE", "9988770044");
        String distUuid = createUser("AADHAR-DEMO-DIST", "Prakash Dist", "PF-DIST-DEMO", UserType.DISTRIBUTOR_EMPLOYEE, "ROLE_DISTRIBUTOR_EMPLOYEE", "9988770055");
        String retUuid = createUser("AADHAR-DEMO-RET", "Amit Retail", null, UserType.RETAILER, "ROLE_RETAILER", "9988770066");
        String govtUuid = createUser("AADHAR-DEMO-GOV", "Vikram Govt", "PF-AG-DEMO", UserType.GOVERNMENT_EMPLOYEE, "ROLE_GOVERNMENT_EMPLOYEE", "9988770077");

        // Lot 1: Milk - ACCEPTED -> PACKAGED -> TESTED
        String lot1Id = createLot(farmerUuid, "MILK-001", BigDecimal.valueOf(500), "Litre");
        acceptLot(lot1Id, agentUuid);
        moveToNodalCenter(lot1Id);
        packageLot(lot1Id, agentUuid);
        testRecord("PACKAGE", lot1Id + "-PKG1", agentUuid, "3.5", TestResult.PASS);

        // Lot 2: Rice - fully processed to consumer-ready
        String lot2Id = createLot(farmerUuid, "RICE-001", BigDecimal.valueOf(1000), "Kg");
        acceptLot(lot2Id, agentUuid);
        moveToNodalCenter(lot2Id);
        packageLot(lot2Id, agentUuid);

        // Manufacturer lot from rice
        String mfgId = createMfgLot(mfgUuid, lot2Id);
        createBundles(mfgId, mfgUuid, 10);

        // Lot 3: Wheat - with complaint and flag
        String lot3Id = createLot(farmerUuid, "WHEAT-001", BigDecimal.valueOf(2000), "Kg");
        createComplaint(farmerUuid, lot3Id);
        createFlag(lot3Id, govtUuid);

        log.info("======= DEMO DATA READY =======");
        log.info("All demo users PIN: 123456");
        log.info("Farmer: AADHAR-DEMO-FARMER");
        log.info("Agent: PF-COL-DEMO");
        log.info("Supplier: PF-SUP-DEMO");
        log.info("Manufacturer: PF-MFG-DEMO");
        log.info("Distributor: PF-DIST-DEMO");
        log.info("Retailer: AADHAR-DEMO-RET");
        log.info("Government: PF-AG-DEMO");
        log.info("Lots: {} (Milk, processed), {} (Rice, manufactured), {} (Wheat, flagged)", lot1Id, lot2Id, lot3Id);
    }

    private String createUser(String aadhaarRef, String name, String pfRef, UserType type, String roleName, String mobile) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) return UUID.randomUUID().toString();

        User user = new User();
        user.setAadhaarReference(aadhaarRef);
        user.setName(name);
        user.setPfReference(pfRef);
        user.setMobileNumber(mobile);
        user.setPinHash(passwordEncoder.encode("123456"));
        user.setUserType(type);
        user.setIdentityReference(aadhaarRef);
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.getRoles().add(role);
        user = userRepository.save(user);
        return user.getUuid();
    }

    private String createLot(String farmerUuid, String productCode, BigDecimal qty, String unit) {
        Product product = productRepository.findByProductCode(productCode).orElse(null);
        if (product == null) return "NO-PRODUCT";

        String lotId = "LOT-DEMO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Lot lot = new Lot();
        lot.setLotId(lotId);
        lot.setFarmerUuid(farmerUuid);
        lot.setProductId(product.getId());
        lot.setQuantity(qty);
        lot.setUnit(unit);
        lot.setStatus(LotStatus.CREATED);
        lot.setCurrentCustodianUuid(farmerUuid);
        lot.setCurrentCustodianRole("FARMER");
        lotRepository.save(lot);
        return lotId;
    }

    private void acceptLot(String lotId, String agentUuid) {
        lotRepository.findByLotId(lotId).ifPresent(lot -> {
            lot.setStatus(LotStatus.ACCEPTED);
            lot.setAcceptedAt(Instant.now());
            lot.setAcceptedBy(agentUuid);
            lot.setCurrentCustodianUuid(agentUuid);
            lot.setCurrentCustodianRole("COLLECTING_AGENT");
            lotRepository.save(lot);
            saveTrace(lotId, agentUuid, "COLLECTING_AGENT", "CREATED", "ACCEPTED");
        });
    }

    private void moveToNodalCenter(String lotId) {
        lotRepository.findByLotId(lotId).ifPresent(lot -> {
            lot.setStatus(LotStatus.AT_NODAL_CENTER);
            lotRepository.save(lot);
        });
    }

    private void packageLot(String lotId, String agentUuid) {
        lotRepository.findByLotId(lotId).ifPresent(lot -> {
            lot.setStatus(LotStatus.PACKAGED);
            lotRepository.save(lot);
        });
    }

    private String createMfgLot(String empUuid, String inputLotId) {
        Product p = productRepository.findByProductCode("RICE-001").orElse(null);
        if (p == null) return "NO-PRODUCT";

        String mfgId = "MFR-DEMO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ManufacturerLot mfg = new ManufacturerLot();
        mfg.setManufacturerLotId(mfgId);
        mfg.setProductId(p.getId());
        mfg.setManufacturerEmployeeUuid(empUuid);
        mfg.setProductionQuantity(BigDecimal.valueOf(950));
        mfg.setUnit("Kg");
        mfg.setProcessingDate(Instant.now());
        mfg.setFacilityName("Demo Rice Mill");
        mfg.setStatus(LotStatus.PROCESSED);
        manufacturerLotRepository.save(mfg);

        ManufacturerLotInput inp = new ManufacturerLotInput();
        inp.setManufacturerLotId(mfgId);
        inp.setInputLotId(inputLotId);
        inp.setInputType("LOT");
        manufacturerLotInputRepository.save(inp);
        return mfgId;
    }

    private void createBundles(String mfgLotId, String empUuid, int count) {
        for (int i = 0; i < count; i++) {
            String bid = "BDL-DEMO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            Bundle b = new Bundle();
            b.setBundleId(bid);
            b.setManufacturerLotId(mfgLotId);
            b.setBundleType("CARTON");
            b.setStatus(LotStatus.BUNDLED);
            b.setCurrentCustodianUuid(empUuid);
            b.setCurrentCustodianRole("MANUFACTURER_EMPLOYEE");
            bundleRepository.save(b);
        }
    }

    private void testRecord(String objType, String objId, String testerUuid, String value, TestResult result) {
        TestRecord r = new TestRecord();
        r.setTestRecordId("TST-DEMO-" + UUID.randomUUID().toString().substring(0, 8));
        r.setObjectType(objType);
        r.setObjectId(objId);
        r.setTesterUuid(testerUuid);
        r.setMeasurementSource("SIMULATED");
        r.setMeasuredValue(value);
        r.setUnit("%");
        r.setResult(result);
        r.setTestedAt(Instant.now());
        r.setFinalized(true);
        testRecordRepository.save(r);
    }

    private void createComplaint(String farmerUuid, String lotId) {
        Complaint c = new Complaint();
        c.setComplaintId("CMP-DEMO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        c.setComplainantUuid(farmerUuid);
        c.setComplainantRole("FARMER");
        c.setCategory("PAYMENT_ISSUE");
        c.setDescription("Payment pending for lot " + lotId);
        c.setRelatedLotId(lotId);
        c.setStatus("PENDING");
        complaintRepository.save(c);
    }

    private void createFlag(String lotId, String govtUuid) {
        Flag f = new Flag();
        f.setFlagId("FLG-DEMO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        f.setFlagType("ANOMALY_DETECTED");
        f.setSeverity(FlagSeverity.MEDIUM);
        f.setEntityType("LOT");
        f.setEntityId(lotId);
        f.setDescription("Suspicious test pattern detected for this lot");
        f.setStatus("OPEN");
        f.setAssignedInvestigatorUuid(govtUuid);
        flagRepository.save(f);
    }

    private void saveTrace(String objectId, String actorUuid, String role, String from, String to) {
        TraceEvent e = new TraceEvent();
        e.setEventId("EVT-DEMO-" + UUID.randomUUID().toString().substring(0, 8));
        e.setObjectType("LOT");
        e.setObjectId(objectId);
        e.setActorUuid(actorUuid);
        e.setActorRole(role);
        e.setAction(ActionType.LOT_ACCEPTED);
        e.setPreviousState(from);
        e.setNewState(to);
        e.setEventTimestamp(Instant.now());
        traceEventRepository.save(e);
    }
}
