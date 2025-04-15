package com.pe.cmsystem.api.commond.controllers;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class CMSystemOptional {

    public static <T> Optional<T> ofFeignTryCatch(CMSystemOptionalExecute<T> exe) {
        try {
            ResponseEntity<CMSystemResponseRest<T>> v = exe.execute();
            return Optional.ofNullable(v.getBody().getData());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
