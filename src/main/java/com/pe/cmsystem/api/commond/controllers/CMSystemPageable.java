package com.pe.cmsystem.api.commond.controllers;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CMSystemPageable implements java.io.Serializable {
    Integer page;
    Integer size;
    List<String> sort;
}
