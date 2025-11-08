package com.example.deviceservice.controller;

import com.example.deviceservice.dto.DeviceCreateRequest;
import com.example.deviceservice.dto.DeviceResponse;
import com.example.deviceservice.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Test
    void createDeviceReturnsCreated() throws Exception {
        UUID id = UUID.randomUUID();
        DeviceResponse response = new DeviceResponse(id, "Thermostat", "Hall", new BigDecimal("2.50"), null, Instant.now(), Instant.now());
        given(deviceService.create(any(DeviceCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Thermostat\",\"description\":\"Hall\",\"maxConsumption\":2.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Thermostat"));
    }

    @Test
    void listDevicesReturnsPage() throws Exception {
        UUID id = UUID.randomUUID();
        DeviceResponse response = new DeviceResponse(id, "Thermostat", "Hall", new BigDecimal("2.50"), null, Instant.now(), Instant.now());
        given(deviceService.list(any(Pageable.class), any(UUID.class), eq(true), eq(false)))
                .willReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()));
    }
}
