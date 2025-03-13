package com.domnerka.workflow.form;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.dto.form.FormDefinitionDto;
import com.domnerka.workflow.dto.form.FormDto;
import com.domnerka.workflow.entity.domnerka.FormDefinitionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/forms")
public class FormDefinitionController {

    private final FormDefinitionService formService;


    @GetMapping("")
    public ResponseEntity<PagedResponseDto<FormDefinitionDto>> getAllFormDefinitions(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        PagedResponseDto<FormDefinitionDto> response = formService.getAllFormDefinition(search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{formId}")
    public ResponseEntity<FormDefinitionEntity> getFormById(@PathVariable String formId) {
        FormDefinitionEntity formEntity = formService.getFormById(formId);
        return ResponseEntity.ok(formEntity);
    }

    @PostMapping("")
    public ResponseEntity<String> deployForm(@RequestBody FormDto formDto) {
        String result = formService.createForm(formDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{formKey}")
    public ResponseEntity<String> updateFormByKey(@PathVariable String formKey, @RequestBody FormDto formDto) {
        String result = formService.updateFormByKey(formKey, formDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{formId}")
    public ResponseEntity<String> deleteFormById(@PathVariable String formId) {
        String result = formService.deleteFormById(formId);
        return ResponseEntity.ok(result);
    }
}
