package com.pe.cmsystem.api.commond.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CMSystemResponseRestTest {

    protected static String requestBody(Object request) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return new String(ow.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConversionJSON() {
        CMSystemResponseRest<String> response = CMSystemResponseRest.<String>builder()
                .status("OK")
                .message("Operación de negocio ejecutada correctamente")
                .data("Resultado de la operación")
                .build();
        String responseFinal = requestBody(response);
        assertNotNull(responseFinal);
        assertTrue(responseFinal.contains("OK"));
    }
}