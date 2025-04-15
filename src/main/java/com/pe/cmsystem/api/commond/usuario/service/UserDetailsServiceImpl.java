package com.pe.cmsystem.api.commond.usuario.service;

import com.pe.cmsystem.api.commond.autentificacion.CMSystemUserDetails;
import com.pe.cmsystem.api.commond.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de UserDetailsService para la autenticación
 * Esta clase no se utiliza en el proyecto
 * No observamos la necesidad para romper la dependencia cíclica
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repositorio de usuario
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor
     *
     * @param usuarioRepository
     */
    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Load user by username
     *
     * @param username the username identifying the user whose data is required.
     * @return the UserDetails requested
     * @throws UsernameNotFoundException
     */
    @Override
    public CMSystemUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() ? CMSystemUserDetails.builder()
                .idUsuario(usuario.get().getId())
                .username(usuario.get().getUsername())
                .password(usuario.get().getPassword())
                .roles(List.of("USER"))
                .build() : null;

    }

    /**
     * Load user by username
     *
     * @param idusu Identificador del usuario
     * @return the UserDetails requested
     * @throws UsernameNotFoundException
     */
    public CMSystemUserDetails loadUserByID(Long idusu) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findById(idusu);
        return usuario.isPresent() ? CMSystemUserDetails.builder()
                .idUsuario(usuario.get().getId())
                .username(usuario.get().getUsername())
                .password(usuario.get().getPassword())
                .roles(List.of("USER"))
                .build() : null;

    }
}
