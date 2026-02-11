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
 * Controlador CRUD anónimo, reutilizable para cualquier entidad que extienda CMSystemEntityID.
 * No requiere autorización, ideal para endpoints públicos o abiertos.
 *
 * @param <E> Tipo de entidad que extiende CMSystemEntityID
 * @param <T> Tipo de servicio que extiende CMSystemCrudService<E>
 */
@Slf4j
public abstract class CMSystemCrudAnonymousController<
        E extends CMSystemEntityID,
        T extends CMSystemCrudService<E>> {

    private final T service;
    private final AdapterController adapterController = new AdapterController();

    protected CMSystemCrudAnonymousController(T service) {
        this.service = service;
    }

    protected T getService() {
        return service;
    }

    protected AdapterController getAdapterController() {
        return adapterController;
    }

    // 🔍 Buscar por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<CMSystemResponseRest<E>> findById(@PathVariable("id") Long id) {
        return adapterController.execute(uuid -> service.findById(id));
    }

    // 📄 Buscar todos con paginación y ordenamiento
    @PostMapping("/findAll")
    public ResponseEntity<CMSystemResponseRest<List<E>>> findAll(@RequestBody CMSystemPageable pageable) {
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

    // 🆕 Crear entidad
    @PostMapping(value = "/create"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<E>> create(@RequestBody E entity) {
        return adapterController.execute(uuid -> service.create(entity));
    }

    // ✏️ Actualizar entidad
    @PutMapping(value = "/update"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<E>> update(@RequestBody E entity) {
        return adapterController.execute(uuid -> service.update(entity));
    }

    // ❌ Eliminar por ID
    @DeleteMapping(value = "/deleteById/{id}"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CMSystemResponseRest<Boolean>> deleteById(@PathVariable("id") Long id) {
        return adapterController.execute(uuid -> service.deleteById(id));
    }


}
