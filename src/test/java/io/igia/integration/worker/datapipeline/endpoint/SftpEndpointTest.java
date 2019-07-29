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

import io.igia.integration.worker.config.SftpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.SftpEndpoint;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

public class SftpEndpointTest {

    @InjectMocks
    private SftpEndpoint sftpEndpoint;

    @Mock
    private EndpointConfigMapper endpointConfigMapper;

    @Mock
    private SftpEndpointProperties sftpEndpointProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateSourceUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("myhost.domain.com");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("22");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("username");
        endpointConfiguration.setValue("testUser");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("password");
        endpointConfiguration.setValue("testPassword");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("directoryName");
        endpointConfiguration.setValue("testDirectory");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileName");
        endpointConfiguration.setValue("test.txt");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileExist");
        endpointConfiguration.setValue("append");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("delay");
        endpointConfiguration.setValue("30000");
        endpointConfigurations.add(endpointConfiguration);

        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(sftpEndpointProperties.getCommon()).thenReturn(getDefaultProperties());
        Mockito.when(sftpEndpointProperties.getConsumer()).thenReturn(getDefaultSourceProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String sourceUri = sftpEndpoint.generateUri(sourceEndpoint);
        assertEquals("sftp:myhost.domain.com:22/testDirectory?charset=UTF-8&fileName=test.txt&fileExist=append"
                + "&startingDirectoryMustExist=true&directoryMustExist=true&autoCreate=false&reconnectDelay=60000"
                + "&password=testPassword&bridgeErrorHandler=true&delay=30000&runLoggingLevel=DEBUG&username=testUser",
                sourceUri);
    }

    @Test
    public void testGenerateDestinationUri() {
        List<EndpointConfiguration> endpointConfigurations = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("myhost.domain.com");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("port");
        endpointConfiguration.setValue("22");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("username");
        endpointConfiguration.setValue("testUser");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("password");
        endpointConfiguration.setValue("testPassword");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("directoryName");
        endpointConfiguration.setValue("testDirectory");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileName");
        endpointConfiguration.setValue("test.txt");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("fileExist");
        endpointConfiguration.setValue("append");
        endpointConfigurations.add(endpointConfiguration);

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("delay");
        endpointConfiguration.setValue("30000");
        endpointConfigurations.add(endpointConfiguration);

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(sftpEndpointProperties.getCommon()).thenReturn(getDefaultProperties());
        Mockito.when(sftpEndpointProperties.getProducer()).thenReturn(getDefaultDestinationProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String destinationUri = sftpEndpoint.generateUri(destinationEndpoint);
        assertEquals("sftp:myhost.domain.com:22/testDirectory?charset=UTF-8&password=testPassword&fileName=test.txt"
                + "&delay=30000&fileExist=append&autoCreate=true&reconnectDelay=60000&username=testUser", destinationUri);
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
        map.put("reconnectDelay", "60000");
        map.put("scheduler", "quartz2");
        return map;
    }

    private Map<String, String> getDefaultDestinationProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("autoCreate", "true");
        map.put("reconnectDelay", "60000");
        return map;
    }
}
