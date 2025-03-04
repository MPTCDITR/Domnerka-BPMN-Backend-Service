package com.domnerka.workflow.entity.camunda;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "ACT_RE_CAMFORMDEF")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CamFormDefinition {

    @Id
    @Column(name = "ID_")
    private String id;
    @Column(name = "REV_")
    private int rev;
    @Column(name = "KEY_")
    private String key;
    @Column(name = "VERSION_")
    private int version;
    @Column(name = "DEPLOYMENT_ID_")
    private String deploymentId;
    @Column(name = "RESOURCE_NAME_")
    private String resourceName;
    @Column(name = "TENANT_ID_")
    private String tenantId;
}
