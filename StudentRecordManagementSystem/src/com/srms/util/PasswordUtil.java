package com.srms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Hashes passwords with SHA-256 so plain-text passwords are never stored. */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /** Run this class directly to generate a hash for a new admin password. */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java com.srms.util.PasswordUtil <newPassword>");
            return;
        }
        System.out.println(sha256(args[0]));
    }
}
