package com.example.deviceservice.controller;

import com.example.deviceservice.dto.DeviceCreateRequest;
import com.example.deviceservice.dto.DeviceResponse;
import com.example.deviceservice.dto.DeviceUpdateRequest;
import com.example.deviceservice.security.UserPrincipal;
import com.example.deviceservice.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Devices", description = "Device management endpoints")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new device")
    public DeviceResponse create(@Valid @RequestBody DeviceCreateRequest request) {
        return deviceService.create(request);
    }

    @GetMapping
    @Operation(summary = "List devices", description = "Admins see all devices. Clients see their assigned devices. Use owner=me to filter to the authenticated user.")
    public Page<DeviceResponse> list(@AuthenticationPrincipal UserPrincipal principal,
                                     @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable,
                                     @Parameter(description = "Filter devices by owner. Use owner=me to list current user's devices.")
                                     @RequestParam(value = "owner", required = false) String owner) {
        boolean isAdmin = principal != null && "ROLE_ADMIN".equals(principal.getRole());
        boolean ownerOnly = "me".equalsIgnoreCase(owner);
        UUID requesterId = principal != null ? principal.getUserId() : null;
        return deviceService.list(pageable, requesterId, isAdmin, ownerOnly);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a device by id")
    public DeviceResponse getById(@PathVariable UUID id,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal != null && "ROLE_ADMIN".equals(principal.getRole());
        UUID requesterId = principal != null ? principal.getUserId() : null;
        return deviceService.getById(id, requesterId, isAdmin);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update a device")
    public DeviceResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody DeviceUpdateRequest request) {
        return deviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a device")
    public void delete(@PathVariable UUID id) {
        deviceService.delete(id);
    }

    @PostMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Assign a device to a user")
    public DeviceResponse assign(@PathVariable UUID id, @PathVariable UUID userId) {
        return deviceService.assignToUser(id, userId);
    }

    @PostMapping("/{id}/unassign")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Unassign a device from any user")
    public DeviceResponse unassign(@PathVariable UUID id) {
        return deviceService.unassign(id);
    }
}
