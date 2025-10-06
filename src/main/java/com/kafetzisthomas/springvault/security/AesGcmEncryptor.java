package com.kafetzisthomas.springvault.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AesGcmEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;  // bits
    private static final int IV_LENGTH = 12;   // bytes
    private static final int KEY_LENGTH = 32;  // 256 bits
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] encrypt(byte[] data, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        byte[] iv = new byte[IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);

        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

        byte[] cipherText = cipher.doFinal(data);

        // iv + ciphertext
        byte[] result = new byte[IV_LENGTH + cipherText.length];
        System.arraycopy(iv, 0, result, 0, IV_LENGTH);
        System.arraycopy(cipherText, 0, result, IV_LENGTH, cipherText.length);
        return result;
    }

    public static byte[] decrypt(byte[] encrypted, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        if (encrypted.length < IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }

        byte[] iv = new byte[IV_LENGTH];
        byte[] cipherText = new byte[encrypted.length - IV_LENGTH];

        System.arraycopy(encrypted, 0, iv, 0, IV_LENGTH);
        System.arraycopy(encrypted, IV_LENGTH, cipherText, 0, cipherText.length);

        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));

        return cipher.doFinal(cipherText);
    }

    public static String generateKey() {
        byte[] key = new byte[KEY_LENGTH];
        SECURE_RANDOM.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

}
