package com.RisingSun.Repositories;

import com.RisingSun.DTO.UserDTO;
import com.RisingSun.Entities.User;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.RisingSun.DTO.UserDTO(u.username, u.name) FROM User u WHERE u.username ILIKE CONCAT('%', :prefix, '%') AND u.enabled = true AND u.is_deleted = false ORDER BY u.created_at DESC")
    List<UserDTO> findFilteredUsers(@Param("prefix") String prefix, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = TRUE AND u.is_deleted = FALSE")
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT insert_user_if_not_exists(:username, :name, :hash_password)", nativeQuery = true)
    boolean insertUserIfNotExists(@Param("username") String username, @Param("name") String name, @Param("hash_password") String hash_password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.last_login = :last_login WHERE u.username = :username")
    void updateLastLogin(@Param("username") String username, @Param("last_login") LocalDateTime last_login);
}
