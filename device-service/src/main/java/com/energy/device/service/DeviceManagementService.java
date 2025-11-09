package com.energy.device.service;

import com.energy.device.dto.CreateDeviceRequest;
import com.energy.device.dto.DeviceDto;
import com.energy.device.dto.UpdateDeviceRequest;
import com.energy.device.entity.Device;
import com.energy.device.entity.UserDevice;
import com.energy.device.repository.DeviceRepository;
import com.energy.device.repository.UserDeviceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceManagementService {

    private final DeviceRepository deviceRepository;
    private final UserDeviceRepository userDeviceRepository;

    public DeviceManagementService(DeviceRepository deviceRepository, UserDeviceRepository userDeviceRepository) {
        this.deviceRepository = deviceRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<DeviceDto> findAll() {
        return deviceRepository.findAll().stream()
                .map(device -> new DeviceDto(device.getId(), device.getName(), device.getMaxConsumption(), device.getCreatedAt()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DeviceDto findById(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        return new DeviceDto(device.getId(), device.getName(), device.getMaxConsumption(), device.getCreatedAt());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DeviceDto create(CreateDeviceRequest request) {
        Device device = new Device();
        device.setName(request.getName());
        device.setMaxConsumption(request.getMaxConsumption());
        Device saved = deviceRepository.save(device);
        return new DeviceDto(saved.getId(), saved.getName(), saved.getMaxConsumption(), saved.getCreatedAt());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DeviceDto update(UUID id, UpdateDeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        if (request.getName() != null) {
            device.setName(request.getName());
        }
        if (request.getMaxConsumption() != null) {
            device.setMaxConsumption(request.getMaxConsumption());
        }
        Device saved = deviceRepository.save(device);
        return new DeviceDto(saved.getId(), saved.getName(), saved.getMaxConsumption(), saved.getCreatedAt());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        deviceRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void assignDevice(UUID deviceId, UUID userId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        userDeviceRepository.save(new UserDevice(userId, device.getId()));
    }

    public List<DeviceDto> findDevicesForUser(UUID userId) {
        return userDeviceRepository.findByUserId(userId).stream()
                .map(link -> deviceRepository.findById(link.getDeviceId())
                        .map(device -> new DeviceDto(device.getId(), device.getName(), device.getMaxConsumption(), device.getCreatedAt()))
                        .orElse(null))
                .filter(dto -> dto != null)
                .toList();
    }
}
