package com.pe.cmsystem.api.commond.controllers.exceptionHandler;

import com.pe.cmsystem.api.commond.controllers.CMSystemResponseRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class CMSystemControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CMSystemResponseRest<String>> handleException(Exception ex) {
        log.error("Error due to: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CMSystemResponseRest.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .message(ex.getMessage())
                        .build());
    }

    @ResponseBody
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CMSystemResponseRest<String>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("Direccion no encontrada: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CMSystemResponseRest.<String>builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .message(ex.getMessage())
                        .build());
    }

}
