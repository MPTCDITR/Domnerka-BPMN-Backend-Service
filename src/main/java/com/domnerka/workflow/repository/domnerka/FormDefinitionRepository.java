package com.domnerka.workflow.repository.domnerka;

import com.domnerka.workflow.entity.domnerka.FormDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormDefinitionRepository extends JpaRepository<FormDefinitionEntity, String> {
    Optional<FormDefinitionEntity> findByKey(String key);

    @Query("SELECT f FROM FormDefinitionEntity f WHERE " +
            "LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<FormDefinitionEntity> findAllFormDefinitionBySearch(@Param("search") String search, Pageable pageable);

}
