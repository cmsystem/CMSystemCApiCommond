package com.pe.cmsystem.api.commond.license.model;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LicenseStructure implements Serializable {
    private String codigo;
    private String codigoStr;
    private String serieKey;
    private Date fechaInicio;
    private Date fechaFinal;
}
