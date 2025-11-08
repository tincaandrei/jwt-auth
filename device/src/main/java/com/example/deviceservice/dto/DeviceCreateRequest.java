package com.example.deviceservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Payload used to create a new device")
public record DeviceCreateRequest(
        @NotBlank
        @Size(max = 150)
        @Schema(description = "Unique device name", example = "Air Conditioner")
        String name,

        @Size(max = 500)
        @Schema(description = "Optional description", example = "Living room AC")
        String description,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        @Schema(description = "Maximum consumption in kWh", example = "3.50")
        BigDecimal maxConsumption
) {
}
