package com.example.deviceservice.service;

import com.example.deviceservice.dto.DeviceCreateRequest;
import com.example.deviceservice.dto.DeviceResponse;
import com.example.deviceservice.dto.DeviceUpdateRequest;
import com.example.deviceservice.entity.Device;
import com.example.deviceservice.mapper.DeviceMapper;
import com.example.deviceservice.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;

    public DeviceService(DeviceRepository repository, DeviceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DeviceResponse create(DeviceCreateRequest request) {
        repository.findByNameIgnoreCase(request.name()).ifPresent(device -> {
            throw new IllegalArgumentException("Device name already in use");
        });
        Device device = mapper.toEntity(request);
        validateConsumption(device.getMaxConsumption());
        Device saved = repository.save(device);
        return mapper.toResponse(saved);
    }

    public DeviceResponse getById(UUID id, UUID requesterId, boolean isAdmin) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        if (!isAdmin) {
            enforceOwnership(device, requesterId);
        }
        return mapper.toResponse(device);
    }

    public Page<DeviceResponse> list(Pageable pageable, UUID requesterId, boolean isAdmin, boolean ownerOnly) {
        if (!isAdmin || ownerOnly) {
            if (requesterId == null) {
                throw new AccessDeniedException("Missing requester context");
            }
            List<Device> devices = repository.findAllByUserId(requesterId);
            Page<Device> page = toPage(devices, pageable);
            return page.map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public DeviceResponse update(UUID id, DeviceUpdateRequest request) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        request.name().ifPresent(newName -> {
            repository.findByNameIgnoreCase(newName)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("Device name already in use");
                    });
            device.setName(newName);
        });
        request.description().ifPresent(device::setDescription);
        request.maxConsumption().ifPresent(value -> {
            validateConsumption(value);
            device.setMaxConsumption(value);
        });
        Device saved = repository.save(device);
        return mapper.toResponse(saved);
    }

    public void delete(UUID id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        repository.delete(device);
    }

    public DeviceResponse assignToUser(UUID id, UUID userId) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        device.setUserId(userId);
        return mapper.toResponse(repository.save(device));
    }

    public DeviceResponse unassign(UUID id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        device.setUserId(null);
        return mapper.toResponse(repository.save(device));
    }

    private void validateConsumption(BigDecimal value) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException("maxConsumption must be positive");
        }
    }

    private void enforceOwnership(Device device, UUID requesterId) {
        if (requesterId == null) {
            throw new AccessDeniedException("Missing requester context");
        }
        if (device.getUserId() == null || !device.getUserId().equals(requesterId)) {
            throw new AccessDeniedException("Access denied to device");
        }
    }

    private Page<Device> toPage(List<Device> devices, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), devices.size());
        if (start > end) {
            return new PageImpl<>(List.of(), pageable, devices.size());
        }
        List<Device> slice = devices.subList(start, end);
        return new PageImpl<>(slice, pageable, devices.size());
    }
}
