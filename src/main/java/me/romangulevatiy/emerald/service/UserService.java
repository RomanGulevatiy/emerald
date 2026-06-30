package me.romangulevatiy.emerald.service;

import me.romangulevatiy.emerald.dto.UpdatePasswordRequest;

public interface UserService {

    void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest);
}
