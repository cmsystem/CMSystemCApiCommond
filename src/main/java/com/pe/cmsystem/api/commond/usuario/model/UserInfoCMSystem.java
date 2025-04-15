package com.pe.cmsystem.api.commond.usuario.model;

import lombok.*;

import java.io.Serializable;

/**
 * Class UserInfoCMSystem esta clase termina siendo el token serializado
 */
@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UserInfoCMSystem implements Serializable {
    private Long id;
    private String username;
    private String email;
}
