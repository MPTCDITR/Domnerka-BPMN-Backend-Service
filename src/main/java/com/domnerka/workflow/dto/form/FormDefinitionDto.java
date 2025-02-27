package com.domnerka.workflow.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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

    public FormDefinitionDto(Map<String, Object> form) {
        this.id = (String) form.get("ID_");
        this.key = (String) form.get("KEY_");
        this.name = (String) form.get("NAME_");
        this.version = (Integer) form.get("VERSION_");
        this.resource = (String) form.get("RESOURCE_NAME_");
        this.deploymentId = (String) form.get("DEPLOYMENT_ID_");
        this.tenantId = (String) form.get("TENANT_ID_");
        this.deploymentTime = form.get("DEPLOY_TIME_").toString();
    }
}
