package com.srms.service;

import com.srms.util.AppConfig;
import com.srms.util.PasswordUtil;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/** Authenticates the administrator against the credentials in app.properties. */
public class AuthService {

    public boolean login(String username, char[] password) {
        if (username == null || password == null || password.length == 0) {
            return false;
        }
        String expectedUser = AppConfig.get("admin.username");
        String expectedHash = AppConfig.get("admin.passwordHash");
        String actualHash = PasswordUtil.sha256(new String(password));

        boolean userOk = expectedUser.equals(username.trim());
        // constant-time comparison of hashes
        boolean passOk = MessageDigest.isEqual(
                expectedHash.toLowerCase().getBytes(StandardCharsets.UTF_8),
                actualHash.getBytes(StandardCharsets.UTF_8));
        return userOk && passOk;
    }
}
