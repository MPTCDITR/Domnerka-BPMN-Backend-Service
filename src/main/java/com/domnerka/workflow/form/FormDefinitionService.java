package com.domnerka.workflow.form;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.dto.form.FormDefinitionDto;
import com.domnerka.workflow.dto.form.FormDto;
import com.domnerka.workflow.repository.camunda.CamFormRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FormDefinitionService {

    private final RepositoryService repositoryService;

    private final CamFormRepository camFormRepository;

    public PagedResponseDto<FormDefinitionDto> getAllFormDefinition(String search, Pageable pageable) {
        Set<String> allowedProperties = Set.of("NAME_", "KEY_", "DEPLOY_TIME_");

        boolean hasInvalidProperty = pageable.getSort().stream()
                .map(Sort.Order::getProperty)
                .anyMatch(property -> !allowedProperties.contains(property));

        if (hasInvalidProperty) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort property. Only NAME_, KEY_, or DEPLOY_TIME_ are allowed.");
        }

        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<Map<String, Object>> forms = camFormRepository.findAllFormDefinitionsBySearch(searchTerm, pageable);

        List<FormDefinitionDto> formDefinitions = forms.getContent().stream()
                .map(FormDefinitionDto::new).toList();

        return new PagedResponseDto<>(formDefinitions, forms.getTotalElements());
    }

    @Transactional
    public String deployForm(@RequestBody FormDto formDto) {

        // Deploy the form
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        String formResourceName = formDto.getName() + ".form";
        deploymentBuilder.addString(formResourceName, formDto.getSchema());
        deploymentBuilder.name(formDto.getName());
        Deployment deployment = deploymentBuilder.deploy();

        // Get the deployed resource
        Resource formResource = repositoryService.getDeploymentResources(deployment.getId())
                .stream()
                .filter(resource -> resource.getName().equals(formResourceName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve form resource after deployment"));

        // Parse the form definition from the resource
        String formContent = new String(formResource.getBytes(), StandardCharsets.UTF_8);
        JsonObject formJson = JsonParser.parseString(formContent).getAsJsonObject();
        String formKey = formJson.get("id").getAsString();

        return "Deployed form with key: " + formKey;
    }

}
