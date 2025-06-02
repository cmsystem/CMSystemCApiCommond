package com.pe.cmsystem.api.commond.license.eo;

import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "LICENSE")
@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class LicenseEO extends CMSystemEntityID {
    @Column(name = "CODIGO", length = 30, nullable = false)
    private String codigo;

    @Column(name = "LICENSE_TEXT", columnDefinition = "TEXT")
    @NotAudited
    private String licenseText;
}
