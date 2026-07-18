package me.romangulevatiy.emerald.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.romangulevatiy.emerald.dto.response.PageResponse;
import me.romangulevatiy.emerald.dto.response.UserResponse;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Endpoints for administrative actions such as managing users and roles")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get paginated list of users", description = "Retrieves a paginated list of all registered users")
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<UserResponse> getUsers(
            @RequestParam(name = "page", defaultValue = "0")
            @Min(value = 0, message = "Page number must be {value} or greater")
            int page,

            @RequestParam(name = "size", defaultValue = "16")
            @Min(value = 1, message = "Size must be at least {value}")
            @Max(value = 64, message = "Size must be at most {value}")
            int size) {
        return adminService.getUsers(page, size);
    }

    @Operation(summary = "Update user role", description = "Updates the role of a specified user")
    @PatchMapping("/users/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserRole(@PathVariable String username, @RequestParam UserRole role) {
        adminService.updateUserRole(username, role);
    }
}
