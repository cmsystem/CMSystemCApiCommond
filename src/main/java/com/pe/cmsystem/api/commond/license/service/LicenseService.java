package com.pe.cmsystem.api.commond.license.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pe.cmsystem.api.commond.license.eo.LicenseEO;
import com.pe.cmsystem.api.commond.license.model.LicenseStructure;
import com.pe.cmsystem.api.commond.service.CMSystemCrudService;

import java.util.Optional;

public interface LicenseService extends CMSystemCrudService<LicenseEO> {

    Optional<LicenseEO> findByCodigoAndFlgact(String codigo);
    Optional<LicenseStructure> valorVigente(String serieKey);
    Optional<LicenseEO> validaCodigoVigente(String serieKey, String codigo);
}
