package com.agro.trace.common.constant;

public final class ApiConstants {

    public static final String API_VERSION_1 = "/api/v1";

    // Auth endpoints
    public static final String AUTH_BASE = "/auth";
    public static final String AUTH_FARMER_REGISTER = "/auth/farmer/register";
    public static final String AUTH_FARMER_LOGIN = "/auth/farmer/login";
    public static final String AUTH_PF_REGISTER = "/auth/pf/register";
    public static final String AUTH_PF_LOGIN = "/auth/pf/login";
    public static final String AUTH_EMPLOYEE_REGISTER = "/auth/employee/register";
    public static final String AUTH_EMPLOYEE_LOGIN = "/auth/employee/login";
    public static final String AUTH_RETAILER_REGISTER = "/auth/retailer/register";
    public static final String AUTH_RETAILER_LOGIN = "/auth/retailer/login";
    public static final String AUTH_VERIFY_OTP = "/auth/otp/verify";
    public static final String AUTH_REFRESH_TOKEN = "/auth/refresh";
    public static final String AUTH_UNLOCK = "/auth/unlock";
    public static final String AUTH_LOGOUT = "/auth/logout";

    // Public endpoints
    public static final String PUBLIC_BASE = "/public";
    public static final String PUBLIC_VERIFY_PRODUCT = "/public/products/{qrToken}";

    // Farmer endpoints
    public static final String FARMER_BASE = "/farmer";
    public static final String FARMER_LOTS = "/farmer/lots";
    public static final String FARMER_COMPLAINTS = "/farmer/complaints";

    // Lot endpoints
    public static final String LOT_BASE = "/lots";

    // QR endpoints
    public static final String QR_BASE = "/qr";

    // Collection/Agent endpoints
    public static final String AGENT_BASE = "/agents";

    // Nodal Center endpoints
    public static final String NODAL_BASE = "/nodal-centers";

    // Supplier endpoints
    public static final String SUPPLIER_BASE = "/suppliers";

    // Manufacturer endpoints
    public static final String MANUFACTURER_BASE = "/manufacturers";

    // Distributor endpoints
    public static final String DISTRIBUTOR_BASE = "/distributors";

    // Retailer endpoints
    public static final String RETAILER_BASE = "/retailers";

    // Testing endpoints
    public static final String TESTING_BASE = "/testing";

    // Government endpoints
    public static final String GOVERNMENT_BASE = "/government";

    // Complaint endpoints
    public static final String COMPLAINT_BASE = "/complaints";

    // Organization endpoints
    public static final String ORGANIZATION_BASE = "/organizations";

    // Product endpoints
    public static final String PRODUCT_BASE = "/products";

    private ApiConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}