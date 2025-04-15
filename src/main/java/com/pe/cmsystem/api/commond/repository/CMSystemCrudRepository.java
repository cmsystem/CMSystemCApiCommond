package com.pe.cmsystem.api.commond.repository;

import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CMSystemCrudRepository<T extends CMSystemEntityID> extends CrudRepository<T, Long>, PagingAndSortingRepository<T, Long> {

    List<T> findByFlgact(Integer flgact, Pageable pageable);

    Optional<T> findByIdAndFlgact(Long id, Integer flgact);

}