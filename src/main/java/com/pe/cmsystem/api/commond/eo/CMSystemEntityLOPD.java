package com.pe.cmsystem.api.commond.eo;

import com.pe.cmsystem.api.commond.auditoria.AuditingEntityListener;
import com.pe.cmsystem.api.commond.autentificacion.CMSystemUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class CMSystemEntityLOPD implements Serializable {

    @CreatedBy
    @Column(name = "USUINS", nullable = false, updatable = false)
    private Long usuins;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATINS", nullable = false, updatable = false)
    private Date datins;

    @LastModifiedBy
    @Column(name = "USUMOD")
    private Long usumod;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATMOD")
    private Date datmod;


    @Column(name = "FLGACT", nullable = false, precision = 1, scale = 1)
    @ColumnDefault(value = "1")
    private Integer flgact;

    @PrePersist
    public void prePersist() {
        Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMSystemUserDetails.class::isInstance)
                .map(p -> (CMSystemUserDetails) p)
                .map(CMSystemUserDetails::getIdUsuario)
                .ifPresentOrElse(this::setUsumod
                        , () -> new RuntimeException("No se pudo obtener el usuario autentificado"));
        this.datmod = new Date();
        if (this.usuins == null) {
            this.usuins = this.usumod;
            this.datins = this.datmod;
        }
        this.flgact = 1;
    }

    @PreUpdate
    public void preUpdate() {
        Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMSystemUserDetails.class::isInstance)
                .map(p -> (CMSystemUserDetails) p)
                .map(CMSystemUserDetails::getIdUsuario)
                .ifPresentOrElse(this::setUsumod
                        , () -> new RuntimeException("No se pudo obtener el usuario autentificado"));
        this.datmod = new Date();
    }
}
