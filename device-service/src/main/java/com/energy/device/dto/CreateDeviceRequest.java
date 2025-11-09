package com.energy.device.dto;

import java.math.BigDecimal;

public class CreateDeviceRequest {
    private String name;
    private BigDecimal maxConsumption;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(BigDecimal maxConsumption) {
        this.maxConsumption = maxConsumption;
    }
}
