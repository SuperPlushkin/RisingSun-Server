package com.Sunrise.Repositories;

import com.Sunrise.DTO.DBResults.InsertUserResult;
import com.Sunrise.DTO.ServiceResults.UserDTO;
import com.Sunrise.Entities.User;

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

    @Query("SELECT new com.Sunrise.DTO.ServiceResults.UserDTO(u.id, u.username, u.name) " +
            "FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :prefix, '%')) " +
            "AND u.enabled = true AND u.isDeleted = false")
    List<UserDTO> findFilteredUsers(@Param("prefix") String prefix, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE username = :username AND enabled = TRUE AND is_deleted = FALSE", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = "SELECT success, error_text, generated_token FROM insert_user_if_not_exists(:username, :name, :email, :hash_password)", nativeQuery = true)
    InsertUserResult insertUserIfNotExists(@Param("username") String username, @Param("name") String name, @Param("email") String email, @Param("hash_password") String hash_password);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET last_login = :last_login WHERE username = :username", nativeQuery = true)
    void updateLastLogin(@Param("username") String username, @Param("last_login") LocalDateTime lastLogin);
}
