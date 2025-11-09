package com.energy.device.controller;

import com.energy.device.dto.CreateDeviceRequest;
import com.energy.device.dto.DeviceDto;
import com.energy.device.dto.UpdateDeviceRequest;
import com.energy.device.service.DeviceManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DeviceController {

    private final DeviceManagementService deviceManagementService;

    public DeviceController(DeviceManagementService deviceManagementService) {
        this.deviceManagementService = deviceManagementService;
    }

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDto>> list() {
        return ResponseEntity.ok(deviceManagementService.findAll());
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<DeviceDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceManagementService.findById(id));
    }

    @PostMapping("/devices")
    public ResponseEntity<DeviceDto> create(@RequestBody CreateDeviceRequest request) {
        return ResponseEntity.ok(deviceManagementService.create(request));
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<DeviceDto> update(@PathVariable UUID id, @RequestBody UpdateDeviceRequest request) {
        return ResponseEntity.ok(deviceManagementService.update(id, request));
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deviceManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/devices/{deviceId}/assign/{userId}")
    public ResponseEntity<Void> assign(@PathVariable UUID deviceId, @PathVariable UUID userId) {
        deviceManagementService.assignDevice(deviceId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/devices")
    public ResponseEntity<List<DeviceDto>> myDevices(Authentication authentication) {
        UUID userId = null;
        if (authentication.getDetails() instanceof Map<?, ?> details) {
            Object value = details.get("userId");
            if (value instanceof String idString) {
                userId = UUID.fromString(idString);
            }
        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(deviceManagementService.findDevicesForUser(userId));
    }
}
