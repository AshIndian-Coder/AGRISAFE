package com.agro.trace.devices.service;

import java.util.Optional;

/**
 * Testing Device Integration Interface.
 * Connects to physical testing devices (moisture meters, density meters, etc.).
 * Mock implementation for prototype; connects to real hardware in production.
 */
public interface TestingDeviceProvider {

    /**
     * Check if a device is connected and authenticated.
     */
    boolean isDeviceOnline(String deviceId);

    /**
     * Read a measurement from a device.
     */
    Optional<DeviceMeasurement> readMeasurement(String deviceId, String testCode);

    /**
     * Authenticate a device with the system.
     */
    boolean authenticateDevice(String deviceId, String deviceSecret);

    record DeviceMeasurement(
            String deviceId,
            String testCode,
            String measuredValue,
            String unit,
            String deviceType,
            double accuracy,
            boolean validated
    ) {}
}
