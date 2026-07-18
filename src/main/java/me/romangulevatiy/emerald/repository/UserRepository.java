package me.romangulevatiy.emerald.repository;

import me.romangulevatiy.emerald.dto.response.UserResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT new me.romangulevatiy.emerald.dto.response.UserResponse(u.username, u.role, u.createdAt, u.updatedAt) FROM UserEntity u")
    Page<UserResponse> findAllUsers(Pageable pageable);
}