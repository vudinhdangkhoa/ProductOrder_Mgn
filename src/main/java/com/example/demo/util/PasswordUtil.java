package com.example.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility class for password hashing and validation
 */
public final class PasswordUtil {
    
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    
    // Private constructor to prevent instantiation
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Hash a raw password using BCrypt
     * @param rawPassword the plain text password
     * @return hashed password (BCrypt format)
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return ENCODER.encode(rawPassword);
    }
    
    /**
     * Verify if a raw password matches the hashed password
     * @param rawPassword the plain text password to check
     * @param hashedPassword the stored hashed password
     * @return true if passwords match
     */
    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, hashedPassword);
    }
    
    /**
     * Generate a random password
     * @param length password length (recommended: 12-16)
     * @return random secure password
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upperCase + lowerCase + digits + specialChars;
        
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one character from each group
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill remaining length with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password characters
        char[] passwordChars = password.toString().toCharArray();
        for (int i = passwordChars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordChars[i];
            passwordChars[i] = passwordChars[j];
            passwordChars[j] = temp;
        }
        
        return new String(passwordChars);
    }
    
    /**
     * Check password strength
     * @param password the password to check
     * @return PasswordStrength enum
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // Length check
        if (password.length() >= 12) score++;
        
        // Contains uppercase
        if (password.matches(".*[A-Z].*")) score++;
        
        // Contains lowercase
        if (password.matches(".*[a-z].*")) score++;
        
        // Contains digit
        if (password.matches(".*\\d.*")) score++;
        
        // Contains special char
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) score++;
        
        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 4) return PasswordStrength.MEDIUM;
        return PasswordStrength.STRONG;
    }
    
    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }
}