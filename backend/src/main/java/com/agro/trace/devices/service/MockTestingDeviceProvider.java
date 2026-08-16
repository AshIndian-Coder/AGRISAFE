package com.agro.trace.devices.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class MockTestingDeviceProvider implements TestingDeviceProvider {

    @Override
    public boolean isDeviceOnline(String deviceId) {
        log.info("[MOCK DEVICE] Checking device: {} -> ONLINE", deviceId);
        return true;
    }

    @Override
    public Optional<DeviceMeasurement> readMeasurement(String deviceId, String testCode) {
        log.info("[MOCK DEVICE] Reading {} from device {} -> returning mock value", testCode, deviceId);
        return Optional.of(new DeviceMeasurement(
            deviceId, testCode, "3.5", "%", "MOCK-DEVICE-TYPE", 0.95, true
        ));
    }

    @Override
    public boolean authenticateDevice(String deviceId, String deviceSecret) {
        log.info("[MOCK DEVICE] Authenticating device: {}", deviceId);
        return true;
    }
}
