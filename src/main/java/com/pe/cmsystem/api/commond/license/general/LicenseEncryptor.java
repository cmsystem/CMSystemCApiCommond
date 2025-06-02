package com.pe.cmsystem.api.commond.license.general;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.cmsystem.api.commond.controllers.exceptionHandler.StatusException;
import com.pe.cmsystem.api.commond.license.model.LicenseStructure;
import org.springframework.http.HttpStatus;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class LicenseEncryptor {
    //private static final String SECRET_KEY = "cmsystem.sysmedico.2025"; // 16 chars

    private static SecretKeySpec getKey(String secret) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(secret.getBytes("UTF-8"));
        return new SecretKeySpec(key, "AES");
    }

    // Encripta con AES-256-CBC, genera IV aleatorio y lo concatena al ciphertext (Base64)
    public static String encrypt(String plainText, String secretKey) throws Exception {
        SecretKeySpec keySpec = getKey(secretKey);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Generar IV aleatorio de 16 bytes
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Concatenar IV + encrypted para enviar juntos
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Desencripta el texto cifrado que contiene IV + ciphertext (ambos en Base64)
    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        SecretKeySpec keySpec = getKey(secretKey);

        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // Extraer IV y ciphertext
        byte[] iv = Arrays.copyOfRange(combined, 0, 16);
        byte[] encrypted = Arrays.copyOfRange(combined, 16, combined.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, "UTF-8");
    }
    private static boolean isValidLicenseStructure(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            // Validar campos esperados
            return  node.has("codigo") &&
                    node.has("serieKey") &&
                    node.has("fechaInicio") &&
                    node.has("fechaFinal");

        } catch (Exception e) {
            // JSON mal formado
            return false;
        }
    }
    public static LicenseStructure parseJsonToLicense(String json) {
        try {

            if (!isValidLicenseStructure(json)) {
                throw new StatusException("No cuenta con una licencia valida", HttpStatus.PAYMENT_REQUIRED);
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, LicenseStructure.class);

        } catch (Exception e) {
            throw new StatusException("Error con la licencia: "+e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
