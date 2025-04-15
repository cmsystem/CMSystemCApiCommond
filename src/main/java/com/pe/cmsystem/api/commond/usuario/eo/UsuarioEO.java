package com.pe.cmsystem.api.commond.usuario.eo;

import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "USUARIO")
@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class UsuarioEO extends CMSystemEntityID {

    @Column(name = "USERNAME", length = 250, nullable = false)
    private String username;

    @Column(name = "PASSWORD")
    @NotAudited
    private String password;
}
