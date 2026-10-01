package com.pe.cmsystem.api.commond.controllers;

import com.pe.cmsystem.api.commond.controllers.exceptionHandler.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Controlador de adaptación
 */
@Slf4j
public class AdapterController {

    public <T> ResponseEntity<CMSystemResponseRest<T>> execute(AdapterOperationController<T> operationController) {
        UUID uuid = UUID.randomUUID();

        try {
            log.info("Business {} - Ejecutando operación de negocio", uuid);
            T result = operationController.execute(uuid);

            if (result == null) {
                log.warn("Business {} - Operación de negocio no encontró resultados", uuid);
                return buildResponse(HttpStatus.NOT_FOUND, "Operación de negocio no encontró resultados", null);
            }

            log.info("Business {} - Operación de negocio ejecutada correctamente", uuid);
            return buildResponse(HttpStatus.OK, "Operación de negocio ejecutada correctamente", result);

        } catch (StatusException le) {
            log.error("Business {} - Error: {}", uuid, le.getMessage());
            return buildResponse(le.getStatusCode(), le.getMessage(), null);

        } catch (Exception e) {
            log.error("Business {} - Error inesperado: {}", uuid, e.getMessage());
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
        }
    }

    private <T> ResponseEntity<CMSystemResponseRest<T>> buildResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CMSystemResponseRest.<T>builder()
                        .status(status.toString())
                        .message(message)
                        .data(data)
                        .build());
    }
}
