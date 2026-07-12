package me.romangulevatiy.emerald.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.romangulevatiy.emerald.dto.UpdatePasswordRequest;
import me.romangulevatiy.emerald.security.UserPrincipal;
import me.romangulevatiy.emerald.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Endpoints for managing user profile")
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Update password", description = "Updates the password of the authenticated user. Requires the current password for verification.")
    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@AuthenticationPrincipal UserPrincipal principal,
                               @RequestBody @Valid UpdatePasswordRequest request) {
        userService.updatePassword(principal.getUsername(), request);
    }
}
