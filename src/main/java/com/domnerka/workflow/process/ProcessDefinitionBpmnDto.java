package com.domnerka.workflow.process;

import com.domnerka.workflow.dto.ProcessDefinitionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionBpmnDto {

    private ProcessDefinitionDto processDefinitionDto;
    private String bpmn;
    private String description;
    private String createdBy;

}
