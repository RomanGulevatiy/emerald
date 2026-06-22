package me.romangulevatiy.emerald.service;

import me.romangulevatiy.emerald.dto.PageResponse;
import me.romangulevatiy.emerald.dto.UserResponse;
import me.romangulevatiy.emerald.entity.enums.UserRole;

public interface AdminService {

    PageResponse<UserResponse> getUsers(int page, int size);

    void updateUserRole(String username, UserRole role);
}
