package com.pe.cmsystem.api.commond.autentificacion;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class CMSystemAESGCMDecryptor {

    public static String decrypt(String encryptedBase64, byte[] derivedKey) throws Exception {
        // 1. Decodificar Base64
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

        // 2. Extraer el IV (los primeros 12 bytes)
        byte[] iv = new byte[12];
        System.arraycopy(decoded, 0, iv, 0, iv.length);

        // 3. Extraer el contenido cifrado (el resto)
        int cipherTextOffset = iv.length;
        int cipherTextLen = decoded.length - iv.length;
        byte[] cipherText = new byte[cipherTextLen];
        System.arraycopy(decoded, cipherTextOffset, cipherText, 0, cipherTextLen);

        // 4. Configurar el Cipher para AES/GCM/NoPadding
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Web Crypto usa un tag de autenticación de 128 bits por defecto
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        SecretKeySpec keySpec = new SecretKeySpec(derivedKey, "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

        // 5. Desencriptar y convertir a String
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, "UTF-8");
    }
}