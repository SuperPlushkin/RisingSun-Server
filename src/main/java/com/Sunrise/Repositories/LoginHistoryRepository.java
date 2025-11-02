package com.Sunrise.Repositories;

import com.Sunrise.Entities.LoginHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    @Modifying
    @Query(value = "INSERT INTO login_history (user_id, ip_address, device_info) VALUES (:user_id, :ip_address, :device_info)", nativeQuery = true)
    void addLoginHistory(@Param("user_id") Long user_id, @Param("ip_address") String ip_address, @Param("device_info") String device_info);
}
