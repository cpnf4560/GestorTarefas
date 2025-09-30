package com.gestortarefas.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class Sha256PasswordEncoder implements PasswordEncoder {
    private static final int SALT_LENGTH = 16;

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = generateSalt();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hash = hashPassword(rawPassword.toString(), salt);
        return saltBase64 + ":" + hash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split(":");
        if (parts.length != 2) return false;
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String hash = hashPassword(rawPassword.toString(), salt);
        return hash.equals(parts[1]);
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash SHA-256", e);
        }
    }

    private byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }
}
