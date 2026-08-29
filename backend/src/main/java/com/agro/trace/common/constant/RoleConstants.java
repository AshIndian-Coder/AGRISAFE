package com.agro.trace.common.constant;

public final class RoleConstants {

    public static final String ROLE_FARMER = "FARMER";
    public static final String ROLE_COLLECTING_AGENT = "COLLECTING_AGENT";
    public static final String ROLE_TESTING_AGENT = "TESTING_AGENT";
    public static final String ROLE_NODAL_CENTER_AGENT = "NODAL_CENTER_AGENT";
    public static final String ROLE_SUPPLIER = "SUPPLIER";
    public static final String ROLE_MANUFACTURER_EMPLOYEE = "MANUFACTURER_EMPLOYEE";
    public static final String ROLE_DISTRIBUTOR_EMPLOYEE = "DISTRIBUTOR_EMPLOYEE";
    public static final String ROLE_RETAILER = "RETAILER";
    public static final String ROLE_GOVERNMENT_EMPLOYEE = "GOVERNMENT_EMPLOYEE";
    public static final String ROLE_GOVERNMENT_INVESTIGATOR = "GOVERNMENT_INVESTIGATOR";
    public static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";
    public static final String ROLE_CONSUMER = "CONSUMER";

    private RoleConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}