package com.energy.device.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_devices")
@IdClass(UserDevice.UserDeviceId.class)
public class UserDevice {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    public UserDevice() {
    }

    public UserDevice(UUID userId, UUID deviceId) {
        this.userId = userId;
        this.deviceId = deviceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public static class UserDeviceId implements Serializable {
        private UUID userId;
        private UUID deviceId;

        public UserDeviceId() {
        }

        public UserDeviceId(UUID userId, UUID deviceId) {
            this.userId = userId;
            this.deviceId = deviceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserDeviceId that = (UserDeviceId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(deviceId, that.deviceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, deviceId);
        }
    }
}
