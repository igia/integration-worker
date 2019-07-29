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

import io.igia.integration.worker.config.MllpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.MllpEndpoint;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

public class MllpEndpointTest {

    @InjectMocks
    private MllpEndpoint mllpEndpoint;

    @Mock
    private EndpointConfigMapper endpointConfigMapper;
    
    @Mock
    private MllpEndpointProperties mllpEndpointProperties;

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

        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(mllpEndpointProperties.getConsumer()).thenReturn(getDefaultSourceProperties());
        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String sourceUri = mllpEndpoint.generateUri(sourceEndpoint);
        assertEquals("igia-mllp://localhost:9000?backlog=20&requireEndOfData=true&validatePayload=true", sourceUri);
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

        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setConfigurations(endpointConfigurations);

        Mockito.when(endpointConfigMapper.mapToUriQueryString(ArgumentMatchers.anyMap())).thenCallRealMethod();

        String destinationUri = mllpEndpoint.generateUri(destinationEndpoint);
        assertEquals("igia-mllp://localhost:9000?connectTimeout=30000", destinationUri);
    }

    private Map<String,String> getDefaultSourceProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("requireEndOfData", "true");
        map.put("validatePayload", "true");
        map.put("backlog", "20");
        return map;
    }
}
