package org.legion.aegis.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class EncryptionUtils {

    private static final GCMParameterSpec parameterSpec;

    static {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        parameterSpec = new GCMParameterSpec(128, iv);
    }

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String hashCode = cyclicHash(5, password);
        return encoder.encode(hashCode);
    }

    public static boolean matchPassword(String plain, String cipherPwd) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String hashCode = cyclicHash(5, plain);
        return encoder.matches(hashCode, cipherPwd);
    }


    public static String cyclicHash(int loop, String plain) {
        String cipher = plain;
        for (int i = 0; i < loop; i++) {
            cipher = DigestUtils.sha512Hex(cipher);
        }
        return cipher;
    }

    public static String encryptAES(String src, String key, boolean padding) throws Exception {
        if (!StringUtils.isEmpty(key) && key.length() >= 16) {
            byte[] keyByte = ArrayUtils.extract(key.getBytes(StandardCharsets.UTF_8), 0, 15);
            SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
            Cipher cipher = null;
            if (padding) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            } else {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);
            }
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encodeBase64(encrypted), StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String decryptAES(String ciphertext, String key, boolean padding) throws Exception {
        if (!StringUtils.isEmpty(key) && key.length() >= 16) {
            byte[] keyByte = ArrayUtils.extract(key.getBytes(StandardCharsets.UTF_8), 0, 15);
            SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
            Cipher cipher = null;
            if (padding) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            } else {
                cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, parameterSpec);
            }
            byte[] decrypted = cipher.doFinal(Base64.decodeBase64(ciphertext.getBytes(StandardCharsets.UTF_8)));
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String encryptHmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return Base64.encodeBase64String(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
