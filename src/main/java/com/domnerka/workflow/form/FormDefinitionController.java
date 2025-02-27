package com.domnerka.workflow.form;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.dto.form.FormDefinitionDto;
import com.domnerka.workflow.dto.form.FormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/forms")
public class FormDefinitionController {

    private final FormDefinitionService formDefinitionService;

    @GetMapping("")
    public PagedResponseDto<FormDefinitionDto> getAllFormDefinition(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) @SortDefault(sort = "DEPLOY_TIME_", direction = Sort.Direction.DESC) Pageable pageable) {
        return formDefinitionService.getAllFormDefinition(search, pageable);
    }

    @PostMapping("")
    public String deploy(@RequestBody FormDto formDto) {
        return formDefinitionService.deployForm(formDto);
    }
}
