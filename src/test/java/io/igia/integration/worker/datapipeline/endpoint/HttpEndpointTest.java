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
package io.igia.integration.worker.datapipeline.endpoint;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.GenericApplicationContext;

import com.google.common.base.Supplier;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.config.HttpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.HttpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.util.EndpointUtil;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

public class HttpEndpointTest {

    @InjectMocks
    private HttpEndpoint httpEndpoint;

    @Mock
    private EndpointConfigMapper endpointConfigMapper;

    @Mock
    private HttpEndpointProperties httpEndpointProperties;

    @Mock
    private GenericApplicationContext applicationContext;

    @Mock
    private EndpointUtil endpointUtil;

    private final String dataPipelineId = "1";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateSourceUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("localhost");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("9000");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("resourceUri");
        endpointConfiguration.setValue("/resource");
        endpointConfigurations.add(endpointConfiguration);

        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setConfigurations(endpointConfigurations);
        sourceEndpoint.setType(EndpointType.HTTP);

        Mockito.when(httpEndpointProperties.getConsumer()).thenReturn(getDefaultSourceProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();
        Mockito.when(endpointUtil.getHandlerName(dataPipelineId)).thenCallRealMethod();

        String sourceUri = httpEndpoint.generateUri(sourceEndpoint,dataPipelineId);
        assertEquals("igia-jetty:https://localhost:9000/resource?sendServerVersion=false&bridgeErrorHandler=true"
                + "&httpMethodRestrict=GET,POST,PUT,DELETE&handlers="+dataPipelineId+"_handler", sourceUri);
    }

    @Test
    public void testGenerateDestinationUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("localhost");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("9000");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("resourceUri");
        endpointConfiguration.setValue("/resource");
        endpointConfigurations.add(endpointConfiguration);

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setConfigurations(endpointConfigurations);
        destinationEndpoint.setType(EndpointType.HTTP);

        Mockito.when(httpEndpointProperties.getProducer()).thenReturn(getDefaultDestinationProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String destinationUri = httpEndpoint.generateUri(destinationEndpoint,dataPipelineId);
        assertEquals("igia-http://localhost:9000/resource?copyHeaders=false&maxTotalConnections=200"
                + "&connectionsPerRoute=20&bridgeEndpoint=true", destinationUri);
    }
    
    @Test
    public void testGenerateDestinationHTTPSUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("localhost");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("9000");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("resourceUri");
        endpointConfiguration.setValue("/resource");
        endpointConfigurations.add(endpointConfiguration);
        
        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("isSecure");
        endpointConfiguration.setValue("true");
        endpointConfigurations.add(endpointConfiguration);

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(httpEndpointProperties.getProducer()).thenReturn(getDefaultDestinationProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String destinationUri = httpEndpoint.generateUri(destinationEndpoint,dataPipelineId);
        assertEquals("igia-http://localhost:9000/resource?"+Constants.ENDPOINT_PROPERTY_PROXY_AUTH_PORT+"=9000&copyHeaders=false&maxTotalConnections=200"
                + "&"+Constants.ENDPOINT_PROPERTY_PROXY_AUTH_SCHEME+"=https"+"&connectionsPerRoute=20&bridgeEndpoint=true&"
                +Constants.ENDPOINT_PROPERTY_PROXY_AUTH_HOST+"=localhost", destinationUri);
        
    }

    private Map<String, String> getDefaultSourceProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("bridgeErrorHandler", "true");
        map.put("httpMethodRestrict", "GET,POST,PUT,DELETE");
        map.put("sendServerVersion", "false");
        return map;
    }

    private Map<String, String> getDefaultDestinationProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("maxTotalConnections", "200");
        map.put("connectionsPerRoute", "20");
        map.put("bridgeEndpoint", "true");
        map.put("copyHeaders", "false");
        return map;
    }

    @SuppressWarnings("unchecked")
    @Test
    public <T> void testSourceUriForHandler() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("localhost");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("9000");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("resourceUri");
        endpointConfiguration.setValue("/resource");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("username");
        endpointConfiguration.setValue("admin");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("password");
        endpointConfiguration.setValue("admin");
        endpointConfigurations.add(endpointConfiguration);

        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(httpEndpointProperties.getConsumer()).thenReturn(getDefaultSourceProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();
        Mockito.when(endpointUtil.getHandlerName(dataPipelineId)).thenCallRealMethod();

        doNothing().when(applicationContext).registerBean(ArgumentMatchers.anyString(), (Class<T>) ArgumentMatchers.any(), (Supplier<T>)ArgumentMatchers.any());

        String sourceUri = httpEndpoint.generateUri(sourceEndpoint,dataPipelineId);
        assertEquals("igia-jetty:https://localhost:9000/resource?sendServerVersion=false&bridgeErrorHandler=true"
                + "&httpMethodRestrict=GET,POST,PUT,DELETE&handlers="+dataPipelineId+"_handler", sourceUri);
    }
}
