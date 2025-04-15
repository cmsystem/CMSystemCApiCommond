package com.pe.cmsystem.api.commond.usuario.service;

import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenCMSystemServiceImplTest {


    private static final String USER_NAME = "cgcieza";
    private static final String USER_SECRET = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    private static final String USER_APP = "CMSystem";
    private static final String TOKEN_TEST_2 = "eyJhbGciOiJIUzM4NCJ9.eyJqdGkiOiI2YThmMWMyNS0yZTYyLTQ1ZDktODY5Ny05MTFjNTllYmVhZjMiLCJzdWIiOiJ7XCJpZFwiOjEsXCJ1c2VybmFtZVwiOlwiY2djaWV6YVwiLFwiZW1haWxcIjpcImNnY2llemFcIn0iLCJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9PUFRWTyJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9DU1VUQSJ9XSwiaWF0IjoxNzI0NTE1MzA2LCJleHAiOjE3MjQ1MTU0MjYsImlzcyI6ImNtc3lzdGVtIiwiYXBwRXh0ZXJuIjp0cnVlfQ.LX-uc0RaO-9vPJh4MmwnlsHUlJg3YMHcdISNh0EfMN5SF9brvNmM8b-CR--g69y7";

    @Test
    void generateToken() {
        UsuarioEO usuarioEO = UsuarioEO.builder()
                .username(USER_NAME)
                .password(USER_SECRET)
                .build();
        usuarioEO.setId(1L);
        TokenCMSystemService tokenCMSystemService = new TokenCMSystemServiceImpl();
        String token = tokenCMSystemService.generateToken(usuarioEO, 60, USER_SECRET, USER_APP);
        assertNotNull(token);
    }

    @Test
    void extractUsername() {
        UsuarioEO usuarioEO = UsuarioEO.builder()
                .username(USER_NAME)
                .password(USER_SECRET)
                .build();
        usuarioEO.setId(1L);
        TokenCMSystemService tokenCMSystemService = new TokenCMSystemServiceImpl();
        String token = tokenCMSystemService.generateToken(usuarioEO, 60, USER_SECRET, USER_APP);
        var user = tokenCMSystemService.extractUsername(token, USER_SECRET);
        assertNotNull(user);
        assertEquals(USER_NAME, user.getUsername());
    }

    @Test
    void validateToken() {
        UsuarioEO usuarioEO = UsuarioEO.builder()
                .username(USER_NAME)
                .password(USER_SECRET)
                .build();
        usuarioEO.setId(1L);
        TokenCMSystemService tokenCMSystemService = new TokenCMSystemServiceImpl();
        String token = tokenCMSystemService.generateToken(usuarioEO, 60, USER_SECRET, USER_APP);
        assertTrue(tokenCMSystemService.validateToken(token, USER_SECRET, USER_NAME));
    }

    @Test
    void verificaToken() {
        TokenCMSystemService tokenCMSystemService = new TokenCMSystemServiceImpl();
        assertThrows(ExpiredJwtException.class, () -> tokenCMSystemService.extractUsername(TOKEN_TEST_2, USER_SECRET));
    }
}