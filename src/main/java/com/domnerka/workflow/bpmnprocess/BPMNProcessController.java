package com.domnerka.workflow.bpmnprocess;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.process.BPMNProcessDto;
import com.domnerka.workflow.process.ExtendedProcessDefinitionDto;
import com.domnerka.workflow.process.ProcessDefinitionBpmnDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/bpmn-process")
@RequiredArgsConstructor
public class BPMNProcessController {

    private final BPMNProcessService bpmnProcessService;

    @GetMapping("")
    public PagedResponseDto<ExtendedProcessDefinitionDto> getAllProcessDefinition(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return bpmnProcessService.getAllProcessDefinition(search, pageable);
    }

    @GetMapping("/{processDefinitionId}")
    public ProcessDefinitionBpmnDto getProcessDefinition(@PathVariable String processDefinitionId) {
        return bpmnProcessService.getProcessDefinition(processDefinitionId);
    }

    @PostMapping("/process-definition/{id}/start")
    public ProcessInstanceDto startProcessInstance(@PathVariable String id) {
        return ProcessInstanceDto
                .fromProcessInstance(bpmnProcessService.startProcessInstanceByProcessDefinitionId(id));
    }

    @PostMapping("")
    public String deployBpmnProcess(@Valid @RequestBody BPMNProcessDto bpmnProcessDto) {
        return bpmnProcessService.deployBpmnProcess(bpmnProcessDto);
    }

    @PutMapping("/process/{key}")
    public ResponseEntity<String> updateBpmnProcess(@PathVariable String key,
                                                    @RequestBody BPMNProcessDto bpmnProcessDto) {
        String result = bpmnProcessService.updateDeployBpmnProcess(bpmnProcessDto, key);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteBpmnProcess(@PathVariable String key) {
        bpmnProcessService.deleteBpmnProcess(key);
        return ResponseEntity.ok("Deleted all process definitions with key: " + key);
    }
}
