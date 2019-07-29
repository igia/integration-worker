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

import static org.mockito.Mockito.doAnswer;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.igia.integration.worker.datapipeline.Builder;
import io.igia.integration.worker.datapipeline.Deployer;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;

public class DeployerTest extends CamelTestSupport {

    @InjectMocks
    private Deployer deployer;

    @Mock
    private Builder builder;

    @Mock
    private CamelContext camelContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeploy() throws Exception {
        DataPipeline dataPipeline = new DataPipeline();
        RouteBuilder routebuilder = getRoute();

        Mockito.when(builder.buildRoutes(ArgumentMatchers.any(DataPipeline.class))).thenReturn(routebuilder);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                context.addRoutes(routebuilder);
                return null;
            }

        }).when(camelContext).addRoutes(ArgumentMatchers.any(RouteBuilder.class));

        deployer.deploy(dataPipeline);

        assertEquals(1, context.getRoutes().size());
    }

    @Test
    public void testUndeploy() throws Exception {
        SourceEndpoint source = new SourceEndpoint();
        source.setType(EndpointType.MLLP);
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(8L);
        dataPipeline.setSource(source);

        RouteBuilder routebuilder = getRoute();
        context.addRoutes(routebuilder);

        Mockito.when(camelContext.getRouteDefinitions()).thenReturn(context.getRouteDefinitions());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                context.removeRouteDefinition(routebuilder.getRouteCollection().getRoutes().get(0));
                return null;
            }

        }).when(camelContext).removeRouteDefinition(ArgumentMatchers.any(RouteDefinition.class));

        deployer.undeploy(dataPipeline);

        assertEquals(0, context.getRoutes().size());
    }

    private RouteBuilder getRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("mllp://localhost:9000")
                .routeGroup("8")
                .to("mock:end");
            }
        };
    }
}
