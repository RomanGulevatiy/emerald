package me.romangulevatiy.emerald.service;

public interface RefreshTokenService {

    String create(String username);

    String extractUsername(String rawToken);

    void delete(String rawToken);
}
