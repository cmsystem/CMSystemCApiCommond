package com.pe.cmsystem.api.commond.autentificacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public abstract class CMSystemAuthConfigurationManager<T extends CMSystemAuthFilterManager> {

    private static final String API_DOCS = "/api-docs/**";
    private static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    private static final String SWAGGER_UI = "/swagger-ui/**";
    private final T authenticationFilterManager;

    private final CMSystemAuthProperties properties;

    @Autowired
    public CMSystemAuthConfigurationManager(T authenticationFilterManager, CMSystemAuthProperties properties) {
        this.authenticationFilterManager = authenticationFilterManager;
        this.properties = properties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(SWAGGER_UI, SWAGGER_UI_HTML, API_DOCS).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(authenticationFilterManager, BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
