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

import io.igia.integration.worker.config.FileEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.FileEndpoint;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

public class FileEndpointTest {

    @InjectMocks
    private FileEndpoint fileEndpoint;

    @Mock
    private EndpointConfigMapper endpointConfigMapper;

    @Mock
    private FileEndpointProperties fileEndpointProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateSourceUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("directoryName");
        endpointConfiguration.setValue("testDirectory");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileName");
        endpointConfiguration.setValue("test.txt");
        endpointConfigurations.add(endpointConfiguration);

        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(fileEndpointProperties.getCommon()).thenReturn(getDefaultProperties());
        Mockito.when(fileEndpointProperties.getConsumer()).thenReturn(getDefaultSourceProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String sourceUri = fileEndpoint.generateUri(sourceEndpoint);
        assertEquals("file:testDirectory?charset=UTF-8&fileName=test.txt"
                + "&bridgeErrorHandler=true&runLoggingLevel=DEBUG&startingDirectoryMustExist=true&directoryMustExist=true&autoCreate=false",
                sourceUri);
    }

    @Test
    public void testGenerateDestinationUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("directoryName");
        endpointConfiguration.setValue("testDirectory");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileName");
        endpointConfiguration.setValue("test.txt");
        endpointConfigurations.add(endpointConfiguration);

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(fileEndpointProperties.getCommon()).thenReturn(getDefaultProperties());
        Mockito.when(fileEndpointProperties.getProducer()).thenReturn(getDefaultDestinationProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String destinationUri = fileEndpoint.generateUri(destinationEndpoint);
        assertEquals("file:testDirectory?charset=UTF-8&autoCreate=true&fileName=test.txt", destinationUri);
    }

    private Map<String, String> getDefaultProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("charset", "UTF-8");
        return map;
    }

    private Map<String, String> getDefaultSourceProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("bridgeErrorHandler", "true");
        map.put("startingDirectoryMustExist", "true");
        map.put("directoryMustExist", "true");
        map.put("autoCreate", "false");
        map.put("runLoggingLevel", "DEBUG");
        map.put("scheduler", "quartz2");
        return map;
    }

    private Map<String, String> getDefaultDestinationProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("autoCreate", "true");
        return map;
    }
}
