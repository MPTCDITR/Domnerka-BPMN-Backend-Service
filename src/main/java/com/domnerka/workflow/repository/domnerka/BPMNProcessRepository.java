package com.domnerka.workflow.repository.domnerka;

import com.domnerka.workflow.entity.domnerka.BPMNProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BPMNProcessRepository extends JpaRepository<BPMNProcessEntity, String>, JpaSpecificationExecutor<BPMNProcessEntity>{
    Optional<BPMNProcessEntity> findByKey(String key);

}
