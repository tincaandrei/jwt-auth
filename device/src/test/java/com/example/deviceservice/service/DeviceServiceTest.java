package com.example.deviceservice.service;

import com.example.deviceservice.dto.DeviceCreateRequest;
import com.example.deviceservice.dto.DeviceResponse;
import com.example.deviceservice.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Test
    void createListAndAssignDevice() {
        DeviceCreateRequest request = new DeviceCreateRequest("Thermostat", "Hall thermostat", new BigDecimal("2.50"));
        DeviceResponse created = deviceService.create(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.userId()).isNull();

        Page<DeviceResponse> adminPage = deviceService.list(PageRequest.of(0, 10), null, true, false);
        assertThat(adminPage.getTotalElements()).isEqualTo(1);

        UUID userId = UUID.randomUUID();
        DeviceResponse assigned = deviceService.assignToUser(created.id(), userId);
        assertThat(assigned.userId()).isEqualTo(userId);

        Page<DeviceResponse> clientPage = deviceService.list(PageRequest.of(0, 10), userId, false, false);
        assertThat(clientPage.getTotalElements()).isEqualTo(1);
        assertThat(clientPage.getContent().get(0).userId()).isEqualTo(userId);
    }
}
