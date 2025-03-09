package com.domnerka.workflow.bpmnprocess;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.entity.domnerka.BPMNProcessEntity;
import com.domnerka.workflow.process.BPMNProcessDto;
import com.domnerka.workflow.process.ExtendedProcessDefinitionDto;
import com.domnerka.workflow.process.ProcessDefinitionBpmnDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return bpmnProcessService.getAllBpmnProcess(search, pageable);
    }

    @GetMapping("/{processDefinitionId}")
    public ProcessDefinitionBpmnDto getProcessDefinition(@PathVariable String processDefinitionId) {
        return bpmnProcessService.getBpmnProcessById(processDefinitionId);
    }

    @PostMapping("")
    public String createBPMNProcess(@Valid @RequestBody BPMNProcessDto bpmnProcessDto) {
        return bpmnProcessService.saveBpmnProcess(bpmnProcessDto);
    }

    @PutMapping("/{key}")
    public ResponseEntity<BPMNProcessEntity> updateBpmnProcess(
            @PathVariable String key,
            @RequestBody BPMNProcessDto bpmnProcessDto) {
        BPMNProcessEntity updatedProcess = bpmnProcessService.updateBpmnProcessByKey(key, bpmnProcessDto);
        return ResponseEntity.ok(updatedProcess);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteBpmnProcess(@PathVariable String key) {
        bpmnProcessService.deleteBpmnProcessByKey(key);
        return ResponseEntity.ok("Deleted successfully with key: "+ key);
    }
}
