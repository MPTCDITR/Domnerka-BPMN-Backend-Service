package com.domnerka.workflow.dto.form;

import com.domnerka.workflow.entity.domnerka.FormDefinitionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinitionDto {

    private String id;
    private String key;
    private String name;
    private Integer version;
    private String resource;
    private String deploymentId;
    private String tenantId;
    private String deploymentTime;

    public FormDefinitionDto(FormDefinitionEntity entity) {
        this.id = entity.getId();
        this.key = entity.getKey();
        this.name = entity.getName();
        this.version = entity.getVersion();
        this.resource = entity.getResourceName();
        this.deploymentId = entity.getDeploymentId();
        this.tenantId = entity.getTenantId();
    }
}
