package com.RisingSun.Repositories;

import com.RisingSun.Entities.LoginHistory;
import com.RisingSun.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    @Query("INSERT INTO login_history (user_id, ip_address, device_info) VALUES (:user_id, :ip_address, :device_info)")
    Optional<User> addLoginHistory(@Param("user_id") Long user_id, @Param("ip_address") String ip_address, @Param("device_info") String device_info);
}
