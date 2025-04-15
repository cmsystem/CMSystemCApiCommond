package com.pe.cmsystem.api.commond.usuario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;
import com.pe.cmsystem.api.commond.usuario.model.UserInfoCMSystem;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * Clase que se encarga de generar el token
 */
@Service
@Slf4j
public class TokenCMSystemServiceImpl implements TokenCMSystemService {
    private static final String AUTHORITIES = "authorities";
    private static final String APP_EXTERN = "appExtern";
    private static final String ROLE_OPTVO_ROLE_CSUTA = "ROLE_OPTVO,ROLE_CSUTA";

    /**
     * Crea un token JWT para un usuario externo
     *
     * @param usu               usuario
     * @param expirationSegundo tiempo de expiración en segundos
     * @param secret            clave secreta proporcionada por MGD
     * @param aplicacion        nombre de la aplicación
     * @return token JWT (String)
     */
    @Override
    public String generateToken(UsuarioEO usu, long expirationSegundo, String secret, String aplicacion) {

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(ROLE_OPTVO_ROLE_CSUTA);

        UserInfoCMSystem userInfo = UserInfoCMSystem.builder()
                .id(usu.getId())
                .username(usu.getUsername())
                .email(usu.getUsername())
                .build();

        Map<String, Object> claim = new HashMap<>();
        claim.put(APP_EXTERN, Boolean.TRUE);
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(getUsuariExternJson(userInfo))
                .claim(AUTHORITIES, grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (expirationSegundo * 1000)))
                .setIssuer(aplicacion)
                .addClaims(claim)
                .signWith(getSignKey(secret));
        return builder.compact();
    }

    /**
     * Función de utilidad para convertir un objeto Java a String
     *
     * @param usuari objeto a convertir
     * @return objeto convertido a String
     */
    private String getUsuariExternJson(UserInfoCMSystem usuari) {
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(usuari);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir el objeto a JSON", e);
        }
        return json;
    }

    /**
     * Extrae el usuario de un token JWT
     *
     * @param json JSON del usuario
     * @return objeto UserInfoCMSystem
     */
    private UserInfoCMSystem getJsonUsuariExtern(String json) {
        UserInfoCMSystem userInfo = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            userInfo = mapper.readValue(json, UserInfoCMSystem.class);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir de JSON a objeto", e);
        }
        return userInfo;
    }

    // Get the signing key for JWT token
    private Key getSignKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    @Override
    public UserInfoCMSystem extractUsername(String token, String secret) {
        return extractClaim(token, claim -> getJsonUsuariExtern(claim.getSubject()), secret);
    }

    // Extract the expiration date from the token
    @Override
    public Date extractExpiration(String token, String secret) {
        return extractClaim(token, Claims::getExpiration, secret);
    }

    // Extract a claim from the token
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token, String secret) {
        return (Boolean) extractExpiration(token, secret).before(new Date());
    }

    // Validate the token against user details and expiration
    @Override
    public Boolean validateToken(String token, String secret, String username) {
        final UserInfoCMSystem user = extractUsername(token, secret);
        return (Boolean) (user.getUsername().equals(username) && !isTokenExpired(token, secret));
    }
}
