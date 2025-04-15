package com.pe.cmsystem.api.commond.controllers;

import com.pe.cmsystem.api.commond.autentificacion.CMSystemUserDetails;
import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import com.pe.cmsystem.api.commond.service.CMSystemCrudService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * CMSystem Controller
 */
@Slf4j
public abstract class CMSystemCrudController<E extends CMSystemEntityID, T extends CMSystemCrudService<E>> {
    protected final AdapterController adapterController = new AdapterController();
    private final T service;

    protected CMSystemCrudController(T service) {
        this.service = service;
    }

    private static List<Field> getFieldsObject(Object a) {
        List<Field> fieldsList = new ArrayList<>();
        for (Class obj = a.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
            fieldsList.addAll(Optional.ofNullable(obj.getDeclaredFields()).map(Arrays::asList).orElse(new ArrayList<>()));
        }
        return fieldsList;
    }

    protected static Long getAuthenticationIdUsuario() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMSystemUserDetails.class::isInstance)
                .map(p -> (CMSystemUserDetails) p)
                .map(CMSystemUserDetails::getIdUsuario)
                .orElse(null);
    }

    @PostMapping(value = "/create"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<E>> create(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody E entity) {
        return adapterController.execute(uuid -> this.getService().create(entity));
    }

    @PutMapping(value = "/update"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<E>> update(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                          @RequestBody E entity) {
        return adapterController.execute(uuid -> this.getService().update(entity));
    }

    /**
     * Delete entity
     *
     * @param entity
     */
    @DeleteMapping(value = "/delete"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<Boolean>> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                @RequestBody E entity) {
        return adapterController.execute(uuid -> this.getService().delete(entity));
    }

    /**
     * Delete entity by id
     *
     * @param id entity id
     */
    @DeleteMapping(value = "/deleteById/{id}"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<Boolean>> deleteById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                    @PathVariable(value = "id", required = true) Long id) {
        return adapterController.execute(uuid -> this.getService().deleteById(id));
    }

    /**
     * Find entity by id
     *
     * @param id entity id
     * @return entity
     */
    @GetMapping(value = "/findById/{id}"
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<E>> findById(@PathVariable(value = "id", required = true) Long id) {
        log.info("[CRUD] Buscando entidad por id: {}", id);
        return adapterController.execute(uuid -> this.getService().findById(id));
    }

    /**
     * Buscar todos los registros con la configuración de la paginación
     *
     * @return list of entities
     */
    @PostMapping(value = "/findAll"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<List<E>>> findAll(@RequestBody(required = true) CMSystemPageable pageable) {
        log.info("[CRUD] Buscando todos los registros con paginación: page:{} size:{}, sort:{}", pageable.page, pageable.size, pageable.sort);
        List<Sort.Order> lstOrder = new ArrayList<>();
        Optional.ofNullable(pageable.sort).ifPresentOrElse(sort ->
                sort.forEach(s -> {
                    Sort.Order order = Sort.Order.asc(s);
                    lstOrder.add(order);
                }), () -> lstOrder.add(Sort.Order.asc("id"))
        );
        Pageable pageableInterno = PageRequest.of(pageable.page, pageable.size, Sort.by(lstOrder));
        return adapterController.execute(uuid -> this.getService().findAll(pageableInterno));
    }


    /**
     * Buscar todos los registros con la configuración de la paginación
     *
     * @return list of entities
     */
    @PostMapping(value = "/findLOV"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<List<CMSystemResponsetItemLOV>>> findLOV(@RequestBody(required = true) CMSystemRequestLOV configLOV) {
        return adapterController.execute(uuid -> this.getService().findLOV(configLOV));
    }

    protected T getService() {
        return service;
    }
}
