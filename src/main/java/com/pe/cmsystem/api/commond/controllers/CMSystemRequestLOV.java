package com.pe.cmsystem.api.commond.controllers;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CMSystemRequestLOV {
    /**
     * Lista de campos a obtener
     */
    private List<String> campos;

    /**
     * Lista de campos a ordenar
     */
    private CMSystemPageable pageable;
}
