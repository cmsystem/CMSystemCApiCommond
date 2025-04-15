package com.pe.cmsystem.api.commond.controllers;

import com.pe.cmsystem.api.commond.exception.CMSystemCrudException;

import java.beans.Transient;
import java.util.UUID;

public interface AdapterOperationController<T> {

    /**
     * Ejecutar operación de negocio
     *
     * @return Resultado de la operación
     */
    @Transient
    T execute(UUID uuid) throws CMSystemCrudException;
}
