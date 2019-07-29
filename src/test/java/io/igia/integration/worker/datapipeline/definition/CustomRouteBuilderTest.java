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

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.definition.CustomRouteBuilder;
import io.igia.integration.worker.datapipeline.dto.DataPipelineScript;
import io.igia.integration.worker.datapipeline.dto.ScriptType;

public class CustomRouteBuilderTest extends CamelTestSupport {

    private CustomRouteBuilder customRouteBuilder;

    @Override
    public RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            }
        };
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        customRouteBuilder = Mockito.mock(CustomRouteBuilder.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testAddFilters() throws Exception {

        ProcessorDefinition<?> processorDefinition = createRouteBuilder().from("direct:start");
        processorDefinition = customRouteBuilder.addFilters(processorDefinition, getFilters(), "8", "mock:process");

        assertNotNull(processorDefinition);

        FilterDefinition firstFilter = (FilterDefinition) processorDefinition.getOutputs().get(0);
        assertEquals("1", firstFilter.getExpression().getExpression());

        FilterDefinition secondFilter = (FilterDefinition) firstFilter.getOutputs().get(0);
        assertEquals("true", secondFilter.getExpression().getExpression());

        ToDefinition toDefinition = (ToDefinition) secondFilter.getOutputs().get(0);
        assertEquals("mock:process8", toDefinition.getUri());
    }

    @Test
    public void testAddFiltersEmpty() throws Exception {

        List<DataPipelineScript> dataPipelineScripts = new ArrayList<>();
        ProcessorDefinition<?> processorDefinition = createRouteBuilder().from("direct:start");
        processorDefinition = customRouteBuilder.addFilters(processorDefinition, dataPipelineScripts, "8", "mock:process");

        assertNotNull(processorDefinition);

        ToDefinition toDefinition = (ToDefinition) processorDefinition.getOutputs().get(0);
        assertEquals("mock:process8", toDefinition.getUri());
    }

    private List<DataPipelineScript> getFilters() {

        List<DataPipelineScript> dataPipelineScripts = new ArrayList<>();
        DataPipelineScript dataPipelineScript = new DataPipelineScript();
        dataPipelineScript.setData("true");
        dataPipelineScript.setOrder(2);
        dataPipelineScript.setType(ScriptType.JAVASCRIPT);
        dataPipelineScripts.add(dataPipelineScript);

        dataPipelineScript = new DataPipelineScript();
        dataPipelineScript.setData("1");
        dataPipelineScript.setOrder(1);
        dataPipelineScript.setType(ScriptType.JAVASCRIPT);
        dataPipelineScripts.add(dataPipelineScript);

        return dataPipelineScripts;
    }
}
