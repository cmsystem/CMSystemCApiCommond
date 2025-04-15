package com.pe.cmsystem.api.commond.service;

import com.pe.cmsystem.api.commond.autentificacion.CMSystemUserDetails;
import com.pe.cmsystem.api.commond.controllers.CMSystemRequestLOV;
import com.pe.cmsystem.api.commond.controllers.CMSystemResponsetItemLOV;
import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import com.pe.cmsystem.api.commond.repository.CMSystemCrudRepository;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public abstract class CMSystemCrudServiceImpl<E extends CMSystemEntityID, R extends CMSystemCrudRepository<E>> implements CMSystemCrudService<E> {

    protected final R repository;

    public CMSystemCrudServiceImpl(R repository) {
        this.repository = repository;
    }

    protected static CMSystemUserDetails getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CMSystemUserDetails.class::isInstance)
                .map(p -> (CMSystemUserDetails) p)
                .orElse(null);
    }

    private static <E extends CMSystemEntityID> CMSystemResponsetItemLOV getCmSystemResponsetItemLOV(E entity, String campo) {
        log.info("Campo: {}", campo);
        CMSystemResponsetItemLOV responsetItemLOV = new CMSystemResponsetItemLOV();
        try {
            Field fields = null;
            if (campo.equals("id")) {
                fields = CMSystemEntityID.class.getDeclaredField(campo);
            } else {
                fields = entity.getClass().getDeclaredField(campo);
            }
            fields.setAccessible(true);
            responsetItemLOV.put(campo, fields.get(entity).toString());

        } catch (Exception e) {
            log.error("Campo: {} no localizado", campo);
            responsetItemLOV.put(campo, "");
        }
        return responsetItemLOV;
    }

    /**
     * Crea un nuevo registro
     *
     * @param entity EO que se va ha crear
     * @return entity EO con el nuevo ID
     */
    @Override
    public E create(E entity) {
        entity.setUsuins(getAuthentication().getIdUsuario());
        entity.setDatins(new Date());
        return repository.save(entity);
    }

    /**
     * Actualiza un registro
     *
     * @param entity EO que se va ha actualizar
     * @return entity EO actualizado
     */
    @Override
    public E update(E entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("ID is required");
        }
        E entityDB = repository.findById(entity.getId()).orElse(null);
        if (entityDB == null) {
            throw new IllegalArgumentException("Entity not found");
        }
        entity.setUsumod(getAuthentication().getIdUsuario());
        entity.setDatmod(new Date());
        return repository.save(entity);
    }

    /**
     * Elimina un registro
     *
     * @param entity EO que se va ha eliminar
     * @return true si se elimino, false si no se elimino
     */
    @Override
    public Boolean delete(E entity) {
        return deleteById(entity.getId());
    }

    /**
     * Elimina un registro por ID
     *
     * @param id ID del registro a eliminar
     * @return true si se elimino, false si no se elimino
     */
    @Override
    public Boolean deleteById(Long id) {
        AtomicReference<Boolean> isDeleted = new AtomicReference<>(Boolean.FALSE);
        try {
            Optional.ofNullable(id).ifPresentOrElse(i ->
                            repository.findByIdAndFlgact(id, 1)
                                    .ifPresentOrElse(entity -> {
                                        entity.setUsumod(getAuthentication().getIdUsuario());
                                        entity.setDatmod(new Date());
                                        entity.setFlgact(0);
                                        repository.save(entity);
                                        isDeleted.set(Boolean.TRUE);
                                    }, () -> {
                                        throw new IllegalArgumentException("Entity not found by ID: {}" + id);
                                    })
                    , () -> {
                        throw new IllegalArgumentException("ID is required");
                    });
        } catch (Exception e) {
            log.error("Error deleting entity: {}", e.getMessage());
        }
        return isDeleted.get();
    }

    /**
     * Busca un registro por ID
     *
     * @param id ID del registro a buscar
     * @return entity EO
     */
    @Override
    public E findById(Long id) {
        return repository.findByIdAndFlgact(id, 1).orElse(null);
    }

    /**
     * Busca todos los registros, con la configuración de paginación
     *
     * @return Lista de entity EO
     */
    @Override
    public List<E> findAll(Pageable pageable) {
        List<E> entities = List.of();
        try {
            entities = repository.findByFlgact(1, pageable);
        } catch (Exception e) {
            log.error("Error al buscar elementos: {}", e.getMessage());
        }
        return entities;
    }


    /**
     * Busca registros por criterios, con la configuración de paginación
     *
     * @return Lista de entity EO
     */
    @Override
    public List<E> findCriteriaQuery(CriteriaQuery<E> criteria, Pageable pageable) {
        List<E> entities = List.of();
        try {
            entities = repository.findAll(pageable).toList();
        } catch (Exception e) {
            log.error("Error al buscar elementos: {}", e.getMessage());
        }
        return entities;
    }

    /**
     * Busca registros por LOV, con la configuración de paginación
     *
     * @param requestLOV Request con los campos a obtener
     * @return Lista de entity LOV
     */
    @Override
    public List<CMSystemResponsetItemLOV> findLOV(CMSystemRequestLOV requestLOV) {
        List<Sort.Order> lstOrder = new ArrayList<>();
        Optional.ofNullable(requestLOV.getPageable().getSort()).ifPresentOrElse(sort ->
                sort.forEach(s -> {
                    Sort.Order order = Sort.Order.asc(s);
                    lstOrder.add(order);
                }), () -> lstOrder.add(Sort.Order.asc("id"))
        );

        List<String> lstCamposAll = new ArrayList<>();
        lstCamposAll.addAll(requestLOV.getCampos());
        lstCamposAll.add("id");

        StringBuilder sb = new StringBuilder();
        lstCamposAll.forEach(c -> sb.append(c).append(","));
        log.info("Campos: {}", sb.toString());

        List<E> lstDataLov = findAll(PageRequest.of(requestLOV.getPageable().getPage(), requestLOV.getPageable().getSize(), Sort.by(lstOrder)));
        List<CMSystemResponsetItemLOV> lstResponsetItemLOV = new ArrayList<>();

        lstDataLov.forEach(entity -> lstResponsetItemLOV.add(getLOV(entity, lstCamposAll)));

        StringBuilder sbR = new StringBuilder();
        lstResponsetItemLOV.forEach(c -> c.forEach((k, v) -> sbR.append(k).append(":").append(v).append(",")));
        log.info("LOV: {}", sbR.toString());

        return lstResponsetItemLOV;
    }

    private CMSystemResponsetItemLOV getLOV(E entity, List<String> lstCamposAll) {
        return Optional.ofNullable(lstCamposAll)
                .map(lstCampos -> lstCampos.stream()
                        .map(campo -> getCmSystemResponsetItemLOV(entity, campo))
                        .collect(Collectors.toMap(c -> c.keySet().stream().findFirst().map(x -> x.toString()).orElse("")
                                , c -> c.values().stream().findFirst().map(x -> x.toString()).orElse(null)
                                , (existing, replacement) -> existing, // Manejo de colisiones
                                CMSystemResponsetItemLOV::new)))
                .orElse(new CMSystemResponsetItemLOV());
    }
}

