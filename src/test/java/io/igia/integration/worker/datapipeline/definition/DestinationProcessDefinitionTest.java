/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.integration.worker.datapipeline.definition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.MarshalDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.TransformDefinition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.DestinationRouter;
import io.igia.integration.worker.datapipeline.definition.DestinationProcessDefinition;
import io.igia.integration.worker.datapipeline.dto.AuditMessageEventType;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.DataPipelineScript;
import io.igia.integration.worker.datapipeline.dto.DataPipelineStateType;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.dto.MessageDataType;
import io.igia.integration.worker.datapipeline.dto.ScriptType;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;

public class DestinationProcessDefinitionTest {

    @InjectMocks
    private DestinationProcessDefinition destinationProcessDefinition;

    @Mock
    private DestinationRouter destinationRouter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigure() throws Exception {
        DataPipeline dataPipeline = createDataPipeline();
        List<DestinationEndpoint> destinationEndpoints = dataPipeline.getDestinations();

        DestinationEndpoint destinationEndpoint = destinationEndpoints.iterator().next();

        Mockito.when(destinationRouter.redirect(ArgumentMatchers.any(DestinationEndpoint.class),ArgumentMatchers.anyString())).thenReturn("file:myDirectory?fileName=test.txt");

        RouteBuilder routeBuilder = destinationProcessDefinition.configure(dataPipeline);
        routeBuilder.configure();

        assertEquals(1, routeBuilder.getRouteCollection().getRoutes().size());

        RouteDefinition destinationProcessRouteDefinition = routeBuilder.getRouteCollection().getRoutes().get(0);
        assertEquals("direct:DESTINATIONPROCESS8", destinationProcessRouteDefinition.getInputs().get(0).getEndpointUri());

        assertEquals(4, destinationProcessRouteDefinition.getOutputs().size());

        OnExceptionDefinition onExceptionDefinition = (OnExceptionDefinition) destinationProcessRouteDefinition.getOutputs().get(0);
        assertEquals(Exception.class, onExceptionDefinition.getExceptionClasses().get(0));

        ToDefinition onExceptionToDefinition = (ToDefinition) onExceptionDefinition.getOutputs().get(1);
        assertEquals("seda:ERRORMESSAGES", onExceptionToDefinition.getUri());

        TransformDefinition destiantionTransformDefinition = (TransformDefinition) destinationProcessRouteDefinition.getOutputs().get(1);
        assertEquals(destinationEndpoint.getTransformers().iterator().next().getData(),
                destiantionTransformDefinition.getExpression().getExpressionType().getExpression());

        MarshalDefinition destinationProcessMarshalDefinition = (MarshalDefinition) destinationProcessRouteDefinition.getOutputs().get(2);
        assertEquals("hl7", destinationProcessMarshalDefinition.getDataFormatType().getDataFormatName());

        ToDefinition destinationToDefinition = (ToDefinition) destinationProcessRouteDefinition.getOutputs().get(3);
        assertEquals("file:myDirectory?fileName=test.txt", destinationToDefinition.getUri());
    }

    private DataPipeline createDataPipeline() {

        // Create SourceEndpoint
        EndpointConfiguration sourceConfig1 = getEndpointConfiguration("hostname", "localhost");
        EndpointConfiguration sourceConfig2 = getEndpointConfiguration("port", "9000");

        List<EndpointConfiguration> sourceConfigurations = new ArrayList<>();
        sourceConfigurations.add(sourceConfig1);
        sourceConfigurations.add(sourceConfig2);

        String sourceFilterData = "true";
        DataPipelineScript sourceFilter = getDataPipelineScript(1, ScriptType.JAVASCRIPT, sourceFilterData);

        List<DataPipelineScript> sourceFilters = new ArrayList<>();
        sourceFilters.add(sourceFilter);

        String sourceTransformerData = "var content = request.getBody().toString().toUpperCase();"
                + "var logger = org.slf4j.LoggerFactory.getLogger(exchange.getFromRouteId());"
                + "logger.info (\"Source tranformer 1 logging\");result = content;";
        DataPipelineScript sourceTransformer = getDataPipelineScript(1, ScriptType.JAVASCRIPT, sourceTransformerData);

        List<DataPipelineScript> sourceTransformers = new ArrayList<>();
        sourceTransformers.add(sourceTransformer);

        SourceEndpoint sourceEndpoint = getSourceEndpoint(3L, EndpointType.MLLP, "Source", MessageDataType.HL7_V2,
                MessageDataType.HL7_V2, sourceConfigurations, sourceFilters, sourceTransformers);

        // Create DestinationEndpoint
        EndpointConfiguration destinationConfig1 = getEndpointConfiguration("directoryName", "testDirectory");
        EndpointConfiguration destinationConfig2 = getEndpointConfiguration("fileName", "test.txt");

        List<EndpointConfiguration> destinationConfigurations = new ArrayList<>();
        destinationConfigurations.add(destinationConfig1);
        destinationConfigurations.add(destinationConfig2);

        String destinationFilterData = "true";
        DataPipelineScript destinationFilter = getDataPipelineScript(1, ScriptType.JAVASCRIPT, destinationFilterData);

        List<DataPipelineScript> destinationFilters = new ArrayList<>();
        destinationFilters.add(destinationFilter);

        String destinationTransformerData = "var content = request.getBody().toString().toUpperCase();"
                + "var logger = org.slf4j.LoggerFactory.getLogger(exchange.getFromRouteId());"
                + "logger.info (\"Destination tranformer 1 logging\");result = content;";
        DataPipelineScript destinationTransformer = getDataPipelineScript(1, ScriptType.JAVASCRIPT, destinationTransformerData);

        List<DataPipelineScript> destinationTransformers = new ArrayList<>();
        destinationTransformers.add(destinationTransformer);

        DestinationEndpoint destinationEndpoint = getDestinationEndpoint(1L, EndpointType.FILE, "Destination",
                MessageDataType.HL7_V2, MessageDataType.HL7_V2, destinationConfigurations, destinationFilters,
                destinationTransformers);

        List<DestinationEndpoint> destinationEndpoints = new ArrayList<>();
        destinationEndpoints.add(destinationEndpoint);

        List<AuditMessageEventType> auditMessages = new ArrayList<>();
        auditMessages.add(AuditMessageEventType.ALL);
        auditMessages.add(AuditMessageEventType.ERROR);
        auditMessages.add(AuditMessageEventType.FILTERED);

        // Create DataPipeline
        DataPipeline dataPipeline = getDataPipeline(8L, "DataPipeline", true, DataPipelineStateType.READY,
                sourceEndpoint, destinationEndpoints, auditMessages);
        return dataPipeline;
    }

    private EndpointConfiguration getEndpointConfiguration(String key, String value) {
        EndpointConfiguration configuration = new EndpointConfiguration();
        configuration.setKey(key);
        configuration.setValue(value);
        return configuration;
    }

    private DataPipelineScript getDataPipelineScript(int order, ScriptType type, String data) {
        DataPipelineScript dataPipelineScript = new DataPipelineScript();
        dataPipelineScript.setOrder(order);
        dataPipelineScript.setType(type);
        dataPipelineScript.setData(data);
        return dataPipelineScript;
    }

    private SourceEndpoint getSourceEndpoint(Long id, EndpointType type, String name, MessageDataType inDataType,
            MessageDataType outDataType, List<EndpointConfiguration> configurations, List<DataPipelineScript> filters,
            List<DataPipelineScript> transformers) {
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setType(type);
        sourceEndpoint.setName(name);
        sourceEndpoint.setInDataType(inDataType);
        sourceEndpoint.setOutDataType(outDataType);
        sourceEndpoint.setConfigurations(configurations);
        sourceEndpoint.setFilters(filters);
        sourceEndpoint.setTransformers(transformers);
        return sourceEndpoint;
    }

    private DestinationEndpoint getDestinationEndpoint(Long id, EndpointType type, String name,
            MessageDataType inDataType, MessageDataType outDataType, List<EndpointConfiguration> configurations,
            List<DataPipelineScript> filters, List<DataPipelineScript> transformers) {
        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setType(type);
        destinationEndpoint.setName(name);
        destinationEndpoint.setInDataType(inDataType);
        destinationEndpoint.setOutDataType(outDataType);
        destinationEndpoint.setConfigurations(configurations);
        destinationEndpoint.setFilters(filters);
        destinationEndpoint.setTransformers(transformers);
        return destinationEndpoint;
    }

    private DataPipeline getDataPipeline(Long id, String name, boolean deploy, DataPipelineStateType state,
            SourceEndpoint sourceEndpoint, List<DestinationEndpoint> destinationEndpointss,
            List<AuditMessageEventType> auditMessages) {
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(id);
        dataPipeline.setName(name);
        dataPipeline.setDeploy(deploy);
        dataPipeline.setState(state);
        dataPipeline.setSource(sourceEndpoint);
        dataPipeline.setDestinations(destinationEndpointss);
        dataPipeline.setAuditMessages(auditMessages);
        return dataPipeline;
    }
}
