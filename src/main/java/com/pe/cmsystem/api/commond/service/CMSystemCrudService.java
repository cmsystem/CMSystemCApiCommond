package com.pe.cmsystem.api.commond.service;

import com.pe.cmsystem.api.commond.controllers.CMSystemRequestLOV;
import com.pe.cmsystem.api.commond.controllers.CMSystemResponsetItemLOV;
import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * CMSystem Crud Service
 *
 * @param <T>
 */
public interface CMSystemCrudService<T extends CMSystemEntityID> {
    /**
     * Create entity
     *
     * @param entity
     */
    T create(T entity);

    /**
     * Update entity
     *
     * @param entity Tabla
     */
    T update(T entity);

    /**
     * Delete entity
     *
     * @param entity Tabla
     */
    Boolean delete(T entity);

    /**
     * Delete entity by id
     *
     * @param id entity id
     */
    Boolean deleteById(Long id);

    /**
     * Find entity by id
     *
     * @param id entity id
     * @return entity
     */
    T findById(Long id);

    /**
     * Find all entities
     *
     * @return list of entities
     */
    List<T> findAll(Pageable pageable);

    List<T> findCriteriaQuery(CriteriaQuery<T> criteria, Pageable pageable);

    /**
     * Find all en formato LOV
     *
     * @param requestLOV Configuración de campos
     * @return list of entities
     */
    List<CMSystemResponsetItemLOV> findLOV(CMSystemRequestLOV requestLOV);
}
