package com.pe.cmsystem.api.commond.autentificacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * Configura el filtro anónimo solo para rutas específicas.
 */
@Configuration
public class CMSystemAnonymousConfigurationManager extends CMSystemAnonymousFilterManager {

    protected CMSystemAnonymousConfigurationManager() {
        super();
    }

    @Bean
    public FilterRegistrationBean<CMSystemAnonymousConfigurationManager> anonymousFilter() {
        FilterRegistrationBean<CMSystemAnonymousConfigurationManager> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(this);
        registrationBean.addUrlPatterns("/anonymous-crud/*"); // Rutas anónimas
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}