package com.domnerka.workflow.bpmnprocess;

import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.dto.ProcessDefinitionDto;
import com.domnerka.workflow.entity.domnerka.BPMNProcessEntity;
import com.domnerka.workflow.process.BPMNProcessDto;
import com.domnerka.workflow.process.ExtendedProcessDefinitionDto;
import com.domnerka.workflow.process.ProcessDefinitionBpmnDto;
import com.domnerka.workflow.repository.domnerka.BPMNProcessRepository;
import com.domnerka.workflow.util.SecurityContextUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Service
@RequiredArgsConstructor
public class BPMNProcessService {

    private final BPMNProcessRepository bpmnProcessRepository;


    public PagedResponseDto<ExtendedProcessDefinitionDto> getAllBpmnProcess(String search, Pageable pageable) {
        Specification<BPMNProcessEntity> specification = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("name"), "%" + search + "%"));
        }

        Page<BPMNProcessEntity> bpmnProcessPage = bpmnProcessRepository.findAll(specification, pageable);

        List<ExtendedProcessDefinitionDto> definitions = bpmnProcessPage.getContent().stream()
                .map(ExtendedProcessDefinitionDto::fromEntity)
                .toList();

        return new PagedResponseDto<>(definitions, bpmnProcessPage.getTotalElements());
    }

    public ProcessDefinitionBpmnDto getBpmnProcessById(String processId) {
        BPMNProcessEntity bpmnProcess = bpmnProcessRepository.findById(processId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching BPMN process with id " + processId));

        ProcessDefinitionDto processDefinitionDto = ProcessDefinitionDto.fromBpmnProcessEntity(bpmnProcess);

        return new ProcessDefinitionBpmnDto(
                processDefinitionDto,
                bpmnProcess.getDiagram(), // BPMN XML content
                bpmnProcess.getDescription(),
                bpmnProcess.getCreatedBy()
        );
    }

    public String saveBpmnProcess(BPMNProcessDto bpmnProcessDto) {

        String createdBy = SecurityContextUtility.getUsername();
        String randomKey = "process_" + UUID.randomUUID();

        BPMNProcessEntity bpmnProcessEntity = BPMNProcessEntity.createEntity(bpmnProcessDto, createdBy, randomKey);
        bpmnProcessRepository.saveAndFlush(bpmnProcessEntity);

        return "Saved BPMN process with key: " + randomKey;
    }

    public BPMNProcessEntity updateBpmnProcessByKey(String key, BPMNProcessDto bpmnProcessDto) {
        BPMNProcessEntity existingProcess = bpmnProcessRepository.findByKey(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching BPMN process with key " + key));

        existingProcess.setName(bpmnProcessDto.getProcessName());
        existingProcess.setDescription(bpmnProcessDto.getDescription());
        existingProcess.setDiagram(bpmnProcessDto.getBpmn());

        return bpmnProcessRepository.save(existingProcess);
    }

    public void deleteBpmnProcessByKey(String key) {
        BPMNProcessEntity existingProcess = bpmnProcessRepository.findByKey(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching BPMN process with key " + key));

        bpmnProcessRepository.delete(existingProcess);
    }
}
