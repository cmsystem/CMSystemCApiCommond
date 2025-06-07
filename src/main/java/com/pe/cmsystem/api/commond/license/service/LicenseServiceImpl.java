package com.pe.cmsystem.api.commond.license.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.cmsystem.api.commond.controllers.exceptionHandler.StatusException;
import com.pe.cmsystem.api.commond.license.eo.LicenseEO;
import com.pe.cmsystem.api.commond.license.general.LicenseEncryptor;
import com.pe.cmsystem.api.commond.license.model.LicenseStructure;
import com.pe.cmsystem.api.commond.license.repository.LicenseRepository;
import com.pe.cmsystem.api.commond.service.CMSystemCrudServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.security.SecureRandom;
@Configuration
@Service
public class LicenseServiceImpl extends CMSystemCrudServiceImpl<LicenseEO, LicenseRepository> implements LicenseService {

    @Autowired
    public LicenseServiceImpl(LicenseRepository repository)
    {
        super(repository);
    }

    @Override
    public Optional<LicenseEO> findByCodigoAndFlgact(String codigo) {
        return this.repository.findByCodigoAndFlgact(codigo, 1);
    }
    @Override
    public Optional<LicenseStructure> valorVigente(String serieKey) {
        return convertirFormato(this.repository.findByFlgact(1), serieKey);
    }
    @Override
    public Optional<LicenseEO> validaCodigoVigente(String serieKey, String codigo) {
        Optional<LicenseEO> licenseEO = this.repository.findByFlgact(1);
        Optional<LicenseStructure> licenseEs = convertirFormato(licenseEO, serieKey);
        if(!licenseEs.isEmpty())
        {
            if(licenseEs.get().getCodigoStr().equals(codigo) )
            {
                licenseEO.get().setCodigo(codigo);
                return Optional.of(this.repository.save(licenseEO.get()));
            }
        }
        return null;
    }
    private Optional<LicenseStructure> convertirFormato(Optional<LicenseEO> licenseEO, String serieKey){
        return licenseEO.flatMap(license -> convertLicense(license, serieKey));
    }

    private Optional<LicenseStructure> convertLicense(LicenseEO license, String serieKey ) {
        try {
            String decrypted = LicenseEncryptor.decrypt(license.getLicenseText(), serieKey);
            Optional<LicenseStructure> estructure = Optional.ofNullable(LicenseEncryptor.parseJsonToLicense(decrypted));
            estructure.get().setCodigoStr(estructure.get().getCodigo());
            estructure.get().setCodigo(license.getCodigo());
            return estructure;
        } catch (Exception e) {
            return null;
        }
    }
}
