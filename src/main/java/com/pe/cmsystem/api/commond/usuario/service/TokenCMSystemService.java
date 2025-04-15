package com.pe.cmsystem.api.commond.usuario.service;


import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;
import com.pe.cmsystem.api.commond.usuario.model.UserInfoCMSystem;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

/**
 * Servicio de token
 */
public interface TokenCMSystemService {

    /**
     * Generar token
     *
     * @param usu               UsuarioEO
     * @param expirationSegundo Tiempo de expiración
     * @param secret            Clave secreta
     * @param aplicacion        Aplicación
     * @return Token
     */
    String generateToken(UsuarioEO usu, long expirationSegundo, String secret, String aplicacion);

    // Extract the username from the token
    UserInfoCMSystem extractUsername(String token, String secret);

    // Extract the expiration date from the token
    Date extractExpiration(String token, String secret);

    // Extract a claim from the token
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret);

    // Validate the token against user details and expiration
    Boolean validateToken(String token, String secret, String username);
}
