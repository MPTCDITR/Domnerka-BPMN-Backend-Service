package com.domnerka.workflow.entity.domnerka;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "forms")
@NoArgsConstructor
@AllArgsConstructor
public class FormDefinitionEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "REV")
    private int rev;

    @Column(name = "`KEY`")
    private String key;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VERSION")
    private int version;

    @Column(name = "DEPLOYMENT_ID")
    private String deploymentId;

    @Column(name = "RESOURCE_NAME")
    private String resourceName;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Lob
    @Column(name = "`schema`", columnDefinition = "LONGTEXT")
    private String schema;
}
