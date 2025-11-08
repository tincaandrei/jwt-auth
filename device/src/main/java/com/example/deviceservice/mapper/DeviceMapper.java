package com.example.deviceservice.mapper;

import com.example.deviceservice.dto.DeviceCreateRequest;
import com.example.deviceservice.dto.DeviceResponse;
import com.example.deviceservice.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    public Device toEntity(DeviceCreateRequest request) {
        Device device = new Device();
        device.setName(request.name());
        device.setDescription(request.description());
        device.setMaxConsumption(request.maxConsumption());
        return device;
    }

    public DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getDescription(),
                device.getMaxConsumption(),
                device.getUserId(),
                device.getCreatedAt(),
                device.getUpdatedAt()
        );
    }
}
