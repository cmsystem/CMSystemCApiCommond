package com.pe.cmsystem.api.commond.controllers;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CMSystemResponseRest<T> implements Serializable {
    private String status;
    private String message;
    private T data;
}
