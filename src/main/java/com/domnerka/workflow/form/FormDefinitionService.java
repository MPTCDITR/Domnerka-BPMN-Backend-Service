package com.domnerka.workflow.form;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.dto.form.FormDefinitionDto;
import com.domnerka.workflow.dto.form.FormDto;
import com.domnerka.workflow.entity.domnerka.FormDefinitionEntity;
import com.domnerka.workflow.repository.domnerka.FormDefinitionRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormDefinitionService {

    private final FormDefinitionRepository formRepository;

    public PagedResponseDto<FormDefinitionDto> getAllFormDefinition(String search, Pageable pageable) {
        Page<FormDefinitionEntity> formPage;

        if (search != null && !search.isEmpty()) {
            formPage = formRepository.findAllFormDefinitionBySearch(search, pageable);
        } else {
            formPage = formRepository.findAll(pageable);
        }

        List<FormDefinitionDto> formDtos = formPage.stream()
                .map(FormDefinitionDto::new)
                .collect(Collectors.toList());

        return new PagedResponseDto<>(formDtos, formPage.getTotalElements());
    }

    public FormDefinitionEntity getFormById(String formId) {
        return formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + formId));
    }

    public String createForm(FormDto formDto) {

        validateFormSchema(formDto.getSchema());

        String formKey = parseFormKey(formDto.getSchema());

        saveFormToDatabase(formDto, formKey);

        return "Deployed form with key: " + formKey;
    }

    private void validateFormSchema(String formSchema) {
        try {
            JsonParser.parseString(formSchema);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid form schema: " + e.getMessage());
        }
    }

    private String parseFormKey(String formSchema) {
        try {
            JsonObject formJson = JsonParser.parseString(formSchema).getAsJsonObject();
            if (!formJson.has("id")) {
                throw new IllegalArgumentException("Form schema must contain an 'id' field.");
            }
            return formJson.get("id").getAsString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse form key from schema", e);
        }
    }

    private void saveFormToDatabase(FormDto formDto, String formKey) {
        FormDefinitionEntity formEntity = new FormDefinitionEntity();
        formEntity.setId(UUID.randomUUID().toString());
        formEntity.setKey(formKey);
        formEntity.setName(formDto.getName());
        formEntity.setResourceName(formDto.getName() + ".form");
        formEntity.setSchema(formDto.getSchema());
        formEntity.setDeploymentId(UUID.randomUUID().toString());

        try {
            formRepository.save(formEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save form to database", e);
        }
    }

    public String updateFormByKey(String formKey, FormDto formDto) {

        validateFormSchema(formDto.getSchema());

        FormDefinitionEntity formEntity = formRepository.findByKey(formKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found with Key: " + formKey));

        formEntity.setName(formDto.getName());
        formEntity.setResourceName(formDto.getName());
        formEntity.setSchema(formDto.getSchema());

        formRepository.save(formEntity);

        return "Updated form with Key: " + formKey;
    }

    public String deleteFormById(String formId) {
        if (!formRepository.existsById(formId)) {
            throw new RuntimeException("Form not found with ID: " + formId);
        }

        formRepository.deleteById(formId);

        return "Deleted form with ID: " + formId;
    }
}
