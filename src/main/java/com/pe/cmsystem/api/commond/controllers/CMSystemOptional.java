package com.pe.cmsystem.api.commond.controllers;

import org.springframework.http.ResponseEntity;

import java.util.List;
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
    public static <T> List<T> ofFeignTryCatchList(CMSystemOptionalExecute<List<T>> exe) {
        return ofFeignTryCatch(exe).orElse(List.of());
    }

}
