package com.energy.device.repository;

import com.energy.device.entity.UserDevice;
import com.energy.device.entity.UserDevice.UserDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UserDeviceId> {
    List<UserDevice> findByUserId(UUID userId);
}
