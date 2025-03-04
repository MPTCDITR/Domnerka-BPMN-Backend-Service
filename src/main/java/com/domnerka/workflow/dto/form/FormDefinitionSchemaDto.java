package com.domnerka.workflow.dto.form;

import com.domnerka.workflow.entity.camunda.CamFormDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinitionSchemaDto {
    private CamFormDefinition formDefinition;
    private String schema;
}
