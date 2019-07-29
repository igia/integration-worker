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
package io.igia.integration.worker.datapipeline;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.Builder;
import io.igia.integration.worker.datapipeline.definition.DestinationDefinition;
import io.igia.integration.worker.datapipeline.definition.DestinationProcessDefinition;
import io.igia.integration.worker.datapipeline.definition.SourceDefinition;
import io.igia.integration.worker.datapipeline.definition.SourceProcessDefinition;
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

public class BuilderTest {

    @InjectMocks
    private Builder builder;

    @Mock
    private SourceDefinition sourceDefinition;

    @Mock
    private SourceProcessDefinition sourceProcessDefinition;

    @Mock
    private DestinationDefinition destinationDefinition;

    @Mock
    private DestinationProcessDefinition destinationProcessDefinition;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildRoutes() throws Exception {
        DataPipeline dataPipeline = createDataPipeline();

        Mockito.when(sourceDefinition.configure(ArgumentMatchers.any(DataPipeline.class)))
                .thenReturn(getSourceRouteBuilder());
        Mockito.when(sourceProcessDefinition.configure(ArgumentMatchers.any(DataPipeline.class)))
                .thenReturn(getSourceProcessRouteBuilder());
        Mockito.when(destinationDefinition.configure(ArgumentMatchers.any(DataPipeline.class)))
                .thenReturn(getDestinationRouteBuilder());
        Mockito.when(destinationProcessDefinition.configure(ArgumentMatchers.any(DataPipeline.class)))
                .thenReturn(getDestinationProcessRouteBuilder());

        RouteBuilder routeBuilder = builder.buildRoutes(dataPipeline);
        routeBuilder.configure();

        assertEquals(4, routeBuilder.getRouteCollection().getRoutes().size());

        RouteDefinition sourceProcessRouteDefinition = routeBuilder.getRouteCollection().getRoutes().get(0);
        assertEquals("direct:sourceProcess8", sourceProcessRouteDefinition.getInputs().get(0).getEndpointUri());
        ToDefinition sourceProcessToDefinition = (ToDefinition) sourceProcessRouteDefinition.getOutputs().get(0);
        assertEquals("direct:8_DestinationEndpoint", sourceProcessToDefinition.getUri());

        RouteDefinition destinationRouteDefinition = routeBuilder.getRouteCollection().getRoutes().get(1);
        assertEquals("direct:8_DestinationEndpoint", destinationRouteDefinition.getInputs().get(0).getEndpointUri());
        ToDefinition destinationToDefinition = (ToDefinition) destinationRouteDefinition.getOutputs().get(0);
        assertEquals("direct:destinationProcess8", destinationToDefinition.getUri());

        RouteDefinition destinationProcessRouteDefinition = routeBuilder.getRouteCollection().getRoutes().get(2);
        assertEquals("direct:destinationProcess8", destinationProcessRouteDefinition.getInputs().get(0).getEndpointUri());
        ToDefinition destinationProcessToDefinition = (ToDefinition) destinationProcessRouteDefinition.getOutputs().get(0);
        assertEquals("file:myDirectory?fileName=test.txt", destinationProcessToDefinition.getUri());

        RouteDefinition sourceRouteDefinition = routeBuilder.getRouteCollection().getRoutes().get(3);
        assertEquals("mllp://localhost:9000", sourceRouteDefinition.getInputs().get(0).getEndpointUri());
        ToDefinition sourceToDefinition = (ToDefinition) sourceRouteDefinition.getOutputs().get(0);
        assertEquals("direct:sourceProcess8", sourceToDefinition.getUri());
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

    private RouteBuilder getSourceRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("mllp://localhost:9000").routeGroup("8").to("direct:sourceProcess8");
            }
        };
    }

    private RouteBuilder getSourceProcessRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:sourceProcess8").routeGroup("8").to("direct:8_DestinationEndpoint");
            }
        };
    }

    private RouteBuilder getDestinationRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:8_DestinationEndpoint").routeGroup("8").to("direct:destinationProcess8");
            }
        };
    }

    private RouteBuilder getDestinationProcessRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:destinationProcess8").routeGroup("8").to("file:myDirectory?fileName=test.txt");
            }
        };
    }
}
