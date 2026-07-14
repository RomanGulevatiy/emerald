package me.romangulevatiy.emerald.service;

import java.util.Date;

public interface TokenBlacklistService {

    void revoke(String jti, Date expiration);

    boolean isRevoked(String jti);
}
