package com.example.deviceservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Device details returned to clients")
public record DeviceResponse(
        UUID id,
        String name,
        String description,
        BigDecimal maxConsumption,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) {
}
