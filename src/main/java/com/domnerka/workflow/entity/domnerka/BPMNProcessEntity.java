package com.domnerka.workflow.entity.domnerka;

import com.domnerka.workflow.process.BPMNProcessDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "bpmn_process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPMNProcessEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "`key`")
    private String key;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private Integer version;

    @Column(name = "resource")
    private String resource;

    @Column(name = "deployment_id")
    private String deploymentId;

    @Lob // Use @Lob for large data (e.g., BPMN XML)
    @Column(name = "diagram", columnDefinition = "LONGTEXT")
    private String diagram;

    @Column(name = "suspended")
    private Boolean suspended;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "version_tag")
    private String versionTag;

    @Column(name = "history_time_to_live")
    private Integer historyTimeToLive;

    @Column(name = "is_startable_in_tasklist")
    private Boolean isStartableInTasklist;

    @Column(name = "created_by")
    private String createdBy;

    public static BPMNProcessEntity createEntity(BPMNProcessDto bpmnProcessDto, String createdBy, String processKey) {
        BPMNProcessEntity entity = new BPMNProcessEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setKey(processKey);
        entity.setName(bpmnProcessDto.getProcessName());
        entity.setDescription(bpmnProcessDto.getDescription());
        entity.setDiagram(bpmnProcessDto.getBpmn());
        entity.setVersion(1);
        entity.setResource(bpmnProcessDto.getProcessName() + ".bpmn");
        entity.setDeploymentId(UUID.randomUUID().toString());
        entity.setSuspended(false);
        entity.setIsStartableInTasklist(true);
        entity.setCreatedBy(createdBy);
        return entity;
    }
}
