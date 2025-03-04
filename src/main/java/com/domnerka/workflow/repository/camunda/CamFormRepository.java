package com.domnerka.workflow.repository.camunda;

import com.domnerka.workflow.entity.camunda.CamFormDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CamFormRepository extends JpaRepository<CamFormDefinition, String> {

    List<CamFormDefinition> findAll();

    /**
     * Retrieves a page of form definitions that match the provided IDs and search
     * criteria.
     *
     * @param search   a search string to filter by form name
     * @param pageable pagination information
     * @return a page of form definitions that match the provided IDs and search
     *         criteria
     */
    @Query(value = "SELECT f.*, d.NAME_ as NAME_, d.DEPLOY_TIME_ as DEPLOY_TIME_ " +
            "FROM (SELECT * FROM ACT_RE_CAMFORMDEF f1 " +
            "      WHERE f1.VERSION_ = (SELECT MAX(f2.VERSION_) " +
            "                          FROM ACT_RE_CAMFORMDEF f2 " +
            "                          WHERE f2.KEY_ = f1.KEY_)) AS f " +
            "JOIN ACT_RE_DEPLOYMENT d ON f.DEPLOYMENT_ID_ = d.ID_ " +
            "WHERE (LOWER(d.NAME_) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL) ",
            countQuery = "SELECT COUNT(DISTINCT f.KEY_) " +
                    "FROM ACT_RE_CAMFORMDEF f " +
                    "JOIN ACT_RE_DEPLOYMENT d ON f.DEPLOYMENT_ID_ = d.ID_ " +
                    "WHERE f.VERSION_ = (SELECT MAX(f2.VERSION_) " +
                    "                    FROM ACT_RE_CAMFORMDEF f2 " +
                    "                    WHERE f2.KEY_ = f.KEY_) " +
                    "AND (LOWER(d.NAME_) LIKE LOWER(CONCAT('%', :search, '%')) OR :search IS NULL)",
            nativeQuery = true)
    Page<Map<String, Object>> findAllFormDefinitionsBySearch(
            @Param("search") String search,
            Pageable pageable);

}
