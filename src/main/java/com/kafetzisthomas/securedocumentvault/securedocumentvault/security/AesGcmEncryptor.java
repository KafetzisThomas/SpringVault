package com.kafetzisthomas.securedocumentvault.securedocumentvault.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class AesGcmEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH = 128;

    // Binary api for byte[] <-> byte[], recommended for lob/bytea
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        byte[] salt = generateRandomBytes(16);
        byte[] iv = generateRandomBytes(12);
        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
        byte[] cipherText = cipher.doFinal(data);

        // salt + iv + ciphertext
        byte[] result = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(iv, 0, result, salt.length, iv.length);
        System.arraycopy(cipherText, 0, result, salt.length + iv.length, cipherText.length);
        return result;
    }

    public static byte[] decrypt(byte[] encryptedData, String password) throws Exception {
        byte[] salt = new byte[16];
        byte[] iv = new byte[12];
        byte[] cipherText = new byte[encryptedData.length - salt.length - iv.length];

        System.arraycopy(encryptedData, 0, salt, 0, salt.length);
        System.arraycopy(encryptedData, salt.length, iv, 0, iv.length);
        System.arraycopy(encryptedData, salt.length + iv.length, cipherText, 0, cipherText.length);

        SecretKey key = deriveKey(password, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
        return cipher.doFinal(cipherText);
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100_000, KEY_SIZE);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

}
