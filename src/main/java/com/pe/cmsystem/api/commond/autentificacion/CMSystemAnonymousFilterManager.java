package com.pe.cmsystem.api.commond.autentificacion;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que permite acceso sin validación para rutas anónimas.
 */
@Slf4j
public abstract class CMSystemAnonymousFilterManager extends OncePerRequestFilter {

    public CMSystemAnonymousFilterManager() {
        super();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // No se realiza validación de token
        filterChain.doFilter(request, response);
    }
}
