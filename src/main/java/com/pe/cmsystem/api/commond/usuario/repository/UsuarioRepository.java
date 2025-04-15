package com.pe.cmsystem.api.commond.usuario.repository;

import com.pe.cmsystem.api.commond.repository.CMSystemCrudRepository;
import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;

import java.util.Optional;

public interface UsuarioRepository extends CMSystemCrudRepository<UsuarioEO> {
    /**
     * Buscar usuario por username y password
     *
     * @param username
     * @param password
     * @return
     */
    Optional<UsuarioEO> findByUsernameAndPassword(String username, String password);

    /**
     * Buscar usuario por username
     *
     * @param username código de usuario
     * @return UsuarioEO
     */
    Optional<UsuarioEO> findByUsername(String username);
}
