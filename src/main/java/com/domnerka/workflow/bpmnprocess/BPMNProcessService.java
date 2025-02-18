package com.domnerka.workflow.bpmnprocess;

import com.domnerka.workflow.camundautillity.BPMNModificationUtility;
import com.domnerka.workflow.dto.PagedResponseDto;
import com.domnerka.workflow.entity.domnerka.FeaturedProcessDefinitionEntity;
import com.domnerka.workflow.exception.BPMNProcessDeploymentException;
import com.domnerka.workflow.exception.ProcessXMLReadException;
import com.domnerka.workflow.process.BPMNProcessDto;
import com.domnerka.workflow.process.ExtendedProcessDefinitionDto;
import com.domnerka.workflow.process.ProcessDefinitionBpmnDto;
import com.domnerka.workflow.repository.camunda.DeploymentRepository;
import com.domnerka.workflow.repository.camunda.FeaturedProcessDefinitionRepository;
import com.domnerka.workflow.util.SecurityContextUtility;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.rest.dto.repository.ProcessDefinitionDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.domnerka.workflow.entity.domnerka.FeaturedProcessDefinitionEntity.fromProcessDefinition;
import static com.domnerka.workflow.util.GroupsHierarchyUtility.*;

@Service
@RequiredArgsConstructor
public class BPMNProcessService {

    private final RepositoryService repositoryService;
    private final IdentityService identityService;
    private final RuntimeService runtimeService;
    private final AuthorizationService authorizationService;

    private final DeploymentRepository deploymentQueryRepository;
    private final FeaturedProcessDefinitionRepository featuredProcessDefinitionRepository;



    public PagedResponseDto<ExtendedProcessDefinitionDto> getAllProcessDefinition(String search, Pageable pageable) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByDeploymentId().desc();

        if (search != null && !search.isEmpty()) {
            query.processDefinitionNameLike("%" + search + "%");
        }
        String currentUser = SecurityContextUtility.getUsername();
        List<String> currentGroups = getLowestLevelGroups(SecurityContextUtility.getGroups());

        identityService.setAuthentication(currentUser, currentGroups);
        List<ProcessDefinition> processDefinitions = query.startablePermissionCheck()
                .listPage(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());

        long totalProcess = query.count();
        identityService.clearAuthentication();

        // Collect all deployment IDs
        List<String> deploymentIds = processDefinitions.stream()
                .map(ProcessDefinition::getDeploymentId)
                .distinct()
                .collect(Collectors.toList());

        // Use DeploymentQueryRepository to fetch deployment times
        Map<String, Date> deploymentTimes = deploymentQueryRepository.findDeploymentTimesByIds(deploymentIds);

        List<ExtendedProcessDefinitionDto> definitions = processDefinitions.stream()
                .map(definition -> {
                    ExtendedProcessDefinitionDto dto = ExtendedProcessDefinitionDto.fromProcessDefinition(definition);
                    dto.setCreatedAt(deploymentTimes.get(definition.getDeploymentId()));
                    return dto;
                })
                .toList();

        return new PagedResponseDto<>(definitions, totalProcess);
    }

    public ProcessDefinitionBpmnDto getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = getProcessDefinitionById(processDefinitionId);
        String bpmnXmlContent = getBpmnXmlContent(processDefinition);
        FeaturedProcessDefinitionEntity featuredProcess = getFeaturedProcessDefinition(processDefinitionId);

        ProcessDefinitionDto processDefinitionDto = ProcessDefinitionDto.fromProcessDefinition(processDefinition);
        return new ProcessDefinitionBpmnDto(processDefinitionDto, bpmnXmlContent,
                featuredProcess.getIsFeatured(),
                featuredProcess.getDescription());
    }

    private ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        try {
            return repositoryService.getProcessDefinition(processDefinitionId);
        } catch (ProcessEngineException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No matching process definition with id " + processDefinitionId, e);
        }
    }

    private String getBpmnXmlContent(ProcessDefinition processDefinition) {
        try (InputStream resourceStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), processDefinition.getResourceName());
             BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new ProcessXMLReadException("Failed to read BPMN XML content", e);
        }
    }

    private FeaturedProcessDefinitionEntity getFeaturedProcessDefinition(String processDefinitionId) {
        return featuredProcessDefinitionRepository.findById(processDefinitionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching featured process description with id " + processDefinitionId));
    }

    public ProcessInstance startProcessInstanceByProcessDefinitionId(String processDefinitionKey) {

        ProcessDefinition processDefinition = getProcessDefinitionByKey(processDefinitionKey);

        String currentUser = SecurityContextUtility.getUsername();
        List<String> userGroupList = SecurityContextUtility.getGroups();

        String[] groupPosition = getGroupOfEachPositions(userGroupList);

        identityService.setAuthenticatedUserId(currentUser);

        Map<String, Object> variables = new HashMap<>();
        variables.put("userstarted_name", currentUser);
        variables.put("userstarted_id", SecurityContextUtility.getUserID());
        variables.put("userstarted_roles", SecurityContextUtility.getRealmRoles());
        variables.put("userstarted_groups", SecurityContextUtility.getGroups());
        variables.put("userstarted_full_name", SecurityContextUtility.getUserFullName());

        // // add each group structure into variables
        variables.put("userstarted_position", getUserPosition(userGroupList));

        if (groupPosition != null && groupPosition.length > 0) {
            variables.put("userstarted_ministry", groupPosition[0]);
            variables.put("userstarted_standing_secretary_of_state", groupPosition[1]);
            variables.put("userstarted_secretary_of_state", groupPosition[2]);
            variables.put("userstarted_under_secretary_of_state", groupPosition[3]);
            variables.put("userstarted_general_department", groupPosition[4]);
            variables.put("userstarted_department", groupPosition[5]);
            variables.put("userstarted_office", groupPosition[6]);
            variables.put("userstartd_group_no_code",
                    removeGroupCode(userGroupList.get(0)));
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);

        identityService.clearAuthentication();

        return processInstance;
    }

    @Transactional
    public String deployBpmnProcess(BPMNProcessDto bpmnProcessDto) {
        String currentUser = SecurityContextUtility.getUsername();
        String randomKey = "process_" + UUID.randomUUID();
        String bpmnXml = bpmnProcessDto.getBpmn().replace("%IDPLACEHOLDER%", randomKey);

        bpmnXml = BPMNModificationUtility.addDescriptionToBpmnXml(bpmnXml, bpmnProcessDto.getDescription());

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        try {
            deploymentBuilder.addString(bpmnProcessDto.getProcessName() + ".bpmn", BPMNModificationUtility
                    .insertIntoEndEventSendMsgNotification(bpmnXml));
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            throw new BPMNProcessDeploymentException("Failed to deploy BPMN process", e);
        }
        deploymentBuilder.name(bpmnProcessDto.getProcessName());

        List<ProcessDefinition> deployedDefinitions = deploymentBuilder.deployWithResult()
                .getDeployedProcessDefinitions();

        if (deployedDefinitions.isEmpty()) {
            throw new RuntimeException("No process definitions were deployed");
        }
        ProcessDefinition processDefinition = deployedDefinitions.get(0);

        FeaturedProcessDefinitionEntity featuredProcessDefinition = fromProcessDefinition(processDefinition,
                bpmnProcessDto);
        featuredProcessDefinitionRepository.saveAndFlush(featuredProcessDefinition);


        Authorization newAuthorization = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
        newAuthorization.setUserId(currentUser);
        newAuthorization.setResource(Resources.PROCESS_DEFINITION);
        newAuthorization.setResourceId(processDefinition.getKey());
        newAuthorization.addPermission(Permissions.ALL);
        authorizationService.saveAuthorization(newAuthorization);


        return "Deployed process definition with id: " + processDefinition.getId();
    }

    @Transactional
    public String updateDeployBpmnProcess(BPMNProcessDto bpmnProcessDto, String key) {

        ProcessDefinition processDefinition = getProcessDefinitionByKey(key);

        FeaturedProcessDefinitionEntity existingEntity = featuredProcessDefinitionRepository.findLatestByKey(key);

        if (existingEntity == null) {
            throw new RuntimeException("Existing FeaturedProcessDefinitionEntity with key " + key + " not found");
        }

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        try {
            String updatedBpmn = BPMNModificationUtility.insertIntoEndEventSendMsgNotification(
                    bpmnProcessDto.getBpmn());
            updatedBpmn = updatedBpmn.replaceFirst("name=\"(.*?)\"",
                    "name=\"" + bpmnProcessDto.getProcessName() + "\"");

            updatedBpmn = BPMNModificationUtility.addDescriptionToBpmnXml(updatedBpmn, bpmnProcessDto.getDescription());

            deploymentBuilder.addString(bpmnProcessDto.getProcessName() + ".bpmn", updatedBpmn);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            throw new BPMNProcessDeploymentException("Failed to update BPMN process", e);
        }

        deploymentBuilder.name(bpmnProcessDto.getProcessName());

        List<ProcessDefinition> deployedDefinitions = deploymentBuilder.deployWithResult()
                .getDeployedProcessDefinitions();

        if (deployedDefinitions.isEmpty()) {
            throw new RuntimeException("No process definitions were deployed");
        }
        ProcessDefinition firstDeployedDefinition = deployedDefinitions.get(0);

        FeaturedProcessDefinitionEntity updatedProcess = fromProcessDefinition(
                firstDeployedDefinition,
                bpmnProcessDto);
        featuredProcessDefinitionRepository.saveAndFlush(updatedProcess);

        return "Updated process definition with key: " + key;
    }

    private ProcessDefinition getProcessDefinitionByKey(String processDefinitionKey) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Process definition is not found: " + processDefinitionKey);
        }
        return processDefinition;
    }

    @Transactional
    public void deleteBpmnProcess(String key) {
        try {
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(key).list();

            if (processDefinitions.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching definition with key: " + key);
            }

            for (ProcessDefinition definition : processDefinitions) {
                try {
                    repositoryService.deleteDeployment(definition.getDeploymentId());
                    featuredProcessDefinitionRepository.deleteById(definition.getId());
                } catch (ProcessEngineException e) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Cannot delete process definition, there are active instance(s) depend on it.");
                }
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting process definitions: " + e.getMessage(), e);
        }
    }
}
