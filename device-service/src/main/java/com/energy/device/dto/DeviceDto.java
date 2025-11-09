package com.energy.device.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class DeviceDto {
    private UUID id;
    private String name;
    private BigDecimal maxConsumption;
    private Instant createdAt;

    public DeviceDto(UUID id, String name, BigDecimal maxConsumption, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMaxConsumption() {
        return maxConsumption;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
