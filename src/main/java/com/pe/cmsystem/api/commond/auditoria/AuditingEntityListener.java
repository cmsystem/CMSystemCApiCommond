package com.pe.cmsystem.api.commond.auditoria;

import com.pe.cmsystem.api.commond.autentificacion.CMSystemUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class AuditingEntityListener implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMSystemUserDetails.class::isInstance)
                .map(p -> (CMSystemUserDetails) p)
                .map(CMSystemUserDetails::getIdUsuario);
    }
}
