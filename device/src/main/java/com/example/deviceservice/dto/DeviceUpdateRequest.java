package com.example.deviceservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Optional;

@Schema(description = "Payload used to update an existing device")
public record DeviceUpdateRequest(
        @Size(max = 150)
        @Schema(description = "Unique device name", example = "Updated AC")
        String name,

        @Size(max = 500)
        @Schema(description = "Optional description", example = "Updated description")
        String description,

        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(description = "Maximum consumption in kWh", example = "4.20")
        BigDecimal maxConsumption
) {
    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Optional<BigDecimal> maxConsumption() {
        return Optional.ofNullable(maxConsumption);
    }
}
