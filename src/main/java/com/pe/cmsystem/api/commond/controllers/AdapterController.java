package com.pe.cmsystem.api.commond.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Controlador de adaptación
 */
@Slf4j
public class AdapterController {
    /**
     * Ejecutar operación de negocio
     *
     * @param operationController Controlador de operación
     * @param <T>                 Tipo de resultado
     * @return Resultado de la operación
     */
    public <T> ResponseEntity<CMSystemResponseRest<T>> execute(AdapterOperationController<T> operationController) {
        UUID uuid = UUID.randomUUID();
        ResponseEntity<CMSystemResponseRest<T>> response = null;
        try {
            log.info("Business {} - Ejecutando operación de negocio", uuid);
            T result = operationController.execute(uuid);
            if (result == null) {
                log.warn("Business {} - Operación de negocio no encontró resultados", uuid);
                response = ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(CMSystemResponseRest.<T>builder()
                                .status(HttpStatus.NOT_FOUND.toString())
                                .message("Operación de negocio no encontró resultados")
                                .build());
            } else {
                log.info("Business {} - Operación de negocio ejecutada correctamente", uuid);
                response = ResponseEntity.status(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .body(CMSystemResponseRest.<T>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Operación de negocio ejecutada correctamente")
                                .data(result)
                                .build());
            }
        } catch (Exception e) {
            log.error("Business {} - Error al ejecutar operación de negocio: {}", uuid, e.getMessage());
            response = ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CMSystemResponseRest.<T>builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message(e.getMessage())
                            .build());
        }
        return response;
    }

}
