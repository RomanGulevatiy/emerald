package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.dto.UpdatePasswordRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
            log.error("Passwords do not match");
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        String encodedNewPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());

        user.setPassword(encodedNewPassword);
        log.info("Changing password for user {}", username);
    }
}
