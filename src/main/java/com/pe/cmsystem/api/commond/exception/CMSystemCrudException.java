package com.pe.cmsystem.api.commond.exception;

/**
 * Excepción de CRUD
 */
public class CMSystemCrudException extends Exception {
    /**
     * Constructor
     *
     * @param message Mensaje de error
     */
    public CMSystemCrudException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message Mensaje de error
     * @param cause   Causa de la excepción
     */
    public CMSystemCrudException(String message, Throwable cause) {
        super(message, cause);
    }
}
