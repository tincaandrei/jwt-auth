package com.example.deviceservice.repository;

import com.example.deviceservice.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findByNameIgnoreCase(String name);

    @Query("select d from Device d where d.userId = :userId")
    List<Device> findAllByUserId(UUID userId);
}
