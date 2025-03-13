package com.domnerka.workflow.process;

import com.domnerka.workflow.entity.domnerka.BPMNProcessEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedProcessDefinitionDto {

    private String id;

    private String key;

    private String name;

    private String description;

    private Integer version;

    private String resource;

    private String deploymentId;

    private Boolean suspended;

    private String tenantId;

    private String versionTag;

    private Integer historyTimeToLive;

    private Boolean startableInTasklist;

    private String createdBy;

    public static ExtendedProcessDefinitionDto fromEntity(BPMNProcessEntity bpmnProcess) {
        ExtendedProcessDefinitionDto dto = new ExtendedProcessDefinitionDto();
        dto.setId(bpmnProcess.getId());
        dto.setKey(bpmnProcess.getKey());
        dto.setName(bpmnProcess.getName());
        dto.setDescription(bpmnProcess.getDescription());
        dto.setVersion(bpmnProcess.getVersion());
        dto.setResource(bpmnProcess.getResource());
        dto.setDeploymentId(bpmnProcess.getDeploymentId());
        dto.setSuspended(bpmnProcess.getSuspended());
        dto.setTenantId(bpmnProcess.getTenantId());
        dto.setVersionTag(bpmnProcess.getVersionTag());
        dto.setHistoryTimeToLive(bpmnProcess.getHistoryTimeToLive());
        dto.setStartableInTasklist(bpmnProcess.getIsStartableInTasklist());
        dto.setCreatedBy(bpmnProcess.getCreatedBy());
        return dto;
    }
}
