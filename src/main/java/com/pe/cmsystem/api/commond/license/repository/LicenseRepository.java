package com.pe.cmsystem.api.commond.license.repository;

import com.pe.cmsystem.api.commond.license.eo.LicenseEO;
import com.pe.cmsystem.api.commond.repository.CMSystemCrudRepository;

import java.util.Optional;

public interface LicenseRepository extends CMSystemCrudRepository<LicenseEO> {
    /**
     * Buscar licencia por codigo
     *
     * @param codigo código de la licencia
     * @return LicenceEO
     */
    Optional<LicenseEO> findByCodigoAndFlgact(String codigo, Integer status);
    Optional<LicenseEO> findByFlgact(Integer flgact);
}
