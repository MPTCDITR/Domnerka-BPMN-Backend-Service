package com.domnerka.workflow.repository.domnerka;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.entity.domnerka.FeaturedProcessDefinitionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/featured-process-definition")
public class FeaturedProcessDefinitionController {
    private final FeaturedProcessDefinitionService featuredProcessDefinitionService;

    @GetMapping("")
    public PagedResponseDto<FeaturedProcessDefinitionEntity> getAllProcessDefinitions(Pageable pageable) {
        return featuredProcessDefinitionService.getAllProcessDefinitions(pageable);
    }

    @GetMapping("/process")
    public List<FeaturedProcessDefinitionEntity> getFeaturedProcessDefinition() {
        return featuredProcessDefinitionService.getFeaturedProcessDefinitions();
    }

    @GetMapping("/{id}")
    public FeaturedProcessDefinitionEntity getProcessDefinitionById(@PathVariable String id) {
        return featuredProcessDefinitionService.getProcessDefinitionById(id);
    }
}
