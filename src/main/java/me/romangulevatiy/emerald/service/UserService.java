package me.romangulevatiy.emerald.service;

import me.romangulevatiy.emerald.dto.request.UpdatePasswordRequest;

public interface UserService {

    void updatePassword(String username, UpdatePasswordRequest updatePasswordRequest);
}
