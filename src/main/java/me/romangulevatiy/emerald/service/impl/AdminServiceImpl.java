package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.dto.response.PageResponse;
import me.romangulevatiy.emerald.dto.response.UserResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.dto.mapper.UserMapper;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserResponse> getUsers(int page, int size) {
        Page<UserResponse> userPage = userRepository.findAllUsers(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        log.info("Retrieved page {} of users with size {}", page, size);
        return userMapper.toPageResponse(userPage);
    }

    @Transactional
    @Override
    public void updateUserRole(String username, UserRole role) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username @{}", username);
                    return new EntityNotFoundException("User not found with username @" + username);
                });

        if(user.getRole().equals(role)) {
            log.info("User @{} already has role {}", username, role);
            return;
        }

        user.setRole(role);
        log.info("Set role {} for user @{}", role, username);
    }
}
