package com.pe.cmsystem.api.commond.autentificacion;

import com.pe.cmsystem.api.commond.usuario.service.TokenCMSystemService;
import com.pe.cmsystem.api.commond.usuario.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
public abstract class CMSystemAuthFilterManager extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final int BEGIN_INDEX = 7;
    private static final List<String> SWAGGER_UI = List.of("/swagger-ui", "/swagger-ui", "/api-docs");
    private final TokenCMSystemService tokenCMSystemService;
    private final UserDetailsService userDetailsService;
    private final CMSystemAuthProperties properties;

    @Autowired
    public CMSystemAuthFilterManager(TokenCMSystemService tokenCMSystemService, UserDetailsService userDetailsService, CMSystemAuthProperties properties) {
        this.tokenCMSystemService = tokenCMSystemService;
        this.userDetailsService = userDetailsService;
        this.properties = properties;
    }

    /**
     * Método que se encarga de validar el token
     *
     * @param request     Petición HTTP Header
     * @param response    Respuesta HTTP Header
     * @param filterChain Filtro de cadena
     * @throws ServletException Excepción de Servlet
     * @throws IOException      Excepción de IO
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Boolean flgSwagger = SWAGGER_UI.stream()
                .filter(uri -> request.getRequestURI().contains(uri))
                .map(m -> Boolean.TRUE)
                .findFirst()
                .orElse(Boolean.FALSE);

        if (!flgSwagger) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional.ofNullable(request.getHeader(AUTHORIZATION))
                        .filter(header -> header != null && header.startsWith("Bearer "))
                        .map(header -> header.substring(BEGIN_INDEX))
                        .ifPresentOrElse(token -> {
                            log.info("Token: {}", token);
                            try {
                                //En este punto se valida el token
                                // si el token ha expirado se lanza una excepción (ExpiredJwtException)
                                var username = tokenCMSystemService.extractUsername(token, properties.getKeySecret());
                                log.info("UserInfoCMSystem: Id:{}, username={}, email={}", username.getId(), username.getUsername(), username.getEmail());
                                UserDetails userDetails = ((UserDetailsServiceImpl) userDetailsService).loadUserByID(username.getId());
                                Optional.ofNullable(userDetails)
                                        .ifPresentOrElse(user -> {
                                            log.info("UserDetails: username={}", user.getUsername());
                                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                    user,
                                                    null,
                                                    user.getAuthorities()
                                            );
                                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                            SecurityContextHolder.getContext().setAuthentication(authToken);
                                        }, () -> {
                                            log.info("Se ha intentado localizar al usuario [{}]", username);
                                            log.error("UsuarioEO no encontrado en la BD");
                                            SecurityContextHolder.clearContext();
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                        });
                            } catch (MalformedJwtException e) {
                                log.error("Token no válido");
                                SecurityContextHolder.clearContext();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            } catch (ExpiredJwtException e) {
                                log.warn("Token caducado: {}", e.getMessage());
                                SecurityContextHolder.clearContext();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            } catch (Exception e) {
                                log.error("Token: Error desconocido", e);
                                SecurityContextHolder.clearContext();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            }
                        }, () -> {
                            log.error("El token no es válido");
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        });
            }
        }
        filterChain.doFilter(request, response);
    }
}
