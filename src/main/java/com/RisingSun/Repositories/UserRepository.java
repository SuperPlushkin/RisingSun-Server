package com.RisingSun.Repositories;

import com.RisingSun.Entities.User;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);


    @Query(value = """
    WITH ins AS (
        INSERT INTO users (username, password, last_login)
        SELECT :username, :password, CURRENT_TIMESTAMP
        WHERE NOT EXISTS (
            SELECT 1 FROM users WHERE username = :username
        )
        RETURNING id
    )
    SELECT COUNT(*) > 0 FROM ins
    """, nativeQuery = true)
    boolean insertIfNotExists(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.last_login = :last_login WHERE u.username = :username")
    void updateLastLogin(@Param("username") String username, @Param("last_login") LocalDateTime last_login);
}
