package com.pe.cmsystem.api.commond.controllers;

import org.springframework.http.ResponseEntity;

public interface CMSystemOptionalExecute<T> {
    ResponseEntity<CMSystemResponseRest<T>> execute();
}
