package com.pe.cmsystem.api.commond.usuario.service;

import com.pe.cmsystem.api.commond.service.CMSystemCrudServiceImpl;
import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;
import com.pe.cmsystem.api.commond.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de usuario
 */
@Service
public class UsuarioServiceImpl extends CMSystemCrudServiceImpl<UsuarioEO, UsuarioRepository> implements UsuarioService {
    /**
     * Repositorio de usuario
     */
    //private final UsuarioRepository usuarioRepository;

    /**
     * Constructor
     *
     * @param usuarioRepository
     */
    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    /**
     * Buscar usuario por username y password
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public Optional<UsuarioEO> findByUsernameAndPassword(String username, String password) {
        return this.repository.findByUsernameAndPassword(username, password);
    }

    /**
     * Buscar usuario por username
     *
     * @param username
     * @return
     */
    @Override
    public Optional<UsuarioEO> findByUsername(String username) {
        return this.repository.findByUsername(username);
    }
}
