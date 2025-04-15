package com.pe.cmsystem.api.commond.autentificacion;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Configuration
public class CMSystemAuthProperties {
    @Value("${cmsystem.security.jwt.secret-key}") //
    private String keySecret;

    @Value("${cmsystem.security.jwt.expiration-time}") //
    private String expirationTime;

    @Value("${cmsystem.security.jwt.app}")
    private String applicationCMSystem;

    @Value("${spring.application.name}")
    private String proyectoCMSystem;

    @Value("${cmsystem.cors.allowed-origins}")
    private String corsAllowedOrigins;

    @Value("${cmsystem.cors.allowed-methods}")
    private String corsAllowedMethods;

    @Value("${cmsystem.cors.allowed-headers}")
    private String corsAllowedHeaders;
}
