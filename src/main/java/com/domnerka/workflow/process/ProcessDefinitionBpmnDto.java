package com.domnerka.workflow.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionBpmnDto {

    private ProcessDefinitionDto processDefinitionDto;
    private String bpmn;
    private Boolean isFeatured;
    private String description;

}
