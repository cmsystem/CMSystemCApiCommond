package com.pe.cmsystem.api.commond.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

@Service
public class CMSystemKeyServiceImpl implements CMSystemKeyService {
    @Value("${crypto.pbkdf2.iterations}")
    private int iterations;

    @Value("${crypto.pbkdf2.key-length}")
    private int keyLength;

    @Value("${crypto.pbkdf2.algorithm}")
    private String algorithm;

    @Value("${crypto.pbkdf2.secretKey}")
    private String secretKey;

    @Value("${crypto.pbkdf2.saltBase64}")
    private String saltBase64;

    @Value("${crypto.pbkdf2.tipe-algorithm}")
    private String tipealgorithm;

    public SecretKey deriveKeyFromClient() throws Exception {
        byte[] salt = Base64.getDecoder().decode(saltBase64);

        // Debe coincidir con los parámetros de tu Web Crypto API
        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        PBEKeySpec spec = new PBEKeySpec(
                secretKey.toCharArray(),
                salt,
                iterations, // Iteraciones (deben ser iguales a las de TS)
                keyLength     // Longitud de la llave
        );

        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), tipealgorithm);
    }
}
