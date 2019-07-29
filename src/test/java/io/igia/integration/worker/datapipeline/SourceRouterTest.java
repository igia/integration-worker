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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.SourceRouter;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.FileEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.HttpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.MllpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.SftpEndpoint;

public class SourceRouterTest {

    @InjectMocks
    private SourceRouter sourceRouter;

    @Mock
    private MllpEndpoint mllpEndpoint;

    @Mock
    private FileEndpoint fileEndpoint;

    @Mock
    private SftpEndpoint sftpEndpoint;

    @Mock
    private HttpEndpoint httpEndpoint;

    private final String dataPipelineId = "1";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRedirectMllp() {
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setType(EndpointType.MLLP);

        Mockito.when(mllpEndpoint.generateUri(ArgumentMatchers.any(SourceEndpoint.class)))
                .thenReturn("mllp://localhost:9000");

        String sourceUri = sourceRouter.redirect(sourceEndpoint,dataPipelineId);
        assertEquals("mllp://localhost:9000", sourceUri);
    }

    @Test
    public void testRedirectFile() {
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setType(EndpointType.FILE);

        Mockito.when(fileEndpoint.generateUri(ArgumentMatchers.any(SourceEndpoint.class)))
                .thenReturn("file:testDirectory?fileName=test.txt&fileExist=append");

        String sourceUri = sourceRouter.redirect(sourceEndpoint,dataPipelineId);
        assertEquals("file:testDirectory?fileName=test.txt&fileExist=append", sourceUri);
    }

    @Test
    public void testRedirectSftp() {
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setType(EndpointType.SFTP);

        Mockito.when(sftpEndpoint.generateUri(ArgumentMatchers.any(SourceEndpoint.class)))
        .thenReturn("sftp:myhost.domain.com:22/testDirectory?username=username&password=password&fileName=test.txt&fileExist=append");

        String sourceUri = sourceRouter.redirect(sourceEndpoint,dataPipelineId);
        assertEquals("sftp:myhost.domain.com:22/testDirectory?username=username&password=password&fileName=test.txt&fileExist=append", sourceUri);
    }

    @Test
    public void testRedirectHttp() {
        SourceEndpoint sourceEndpoint = new SourceEndpoint();
        sourceEndpoint.setType(EndpointType.HTTP);

        Mockito.when(httpEndpoint.generateUri(ArgumentMatchers.any(SourceEndpoint.class),ArgumentMatchers.anyString()))
        .thenReturn("jetty:http4://localhost:9000/resource");

        String sourceUri = sourceRouter.redirect(sourceEndpoint,dataPipelineId);
        assertEquals("jetty:http4://localhost:9000/resource", sourceUri);
    }
}
