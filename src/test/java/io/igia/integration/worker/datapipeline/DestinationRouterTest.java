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

import io.igia.integration.worker.datapipeline.DestinationRouter;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.endpoint.FileEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.HttpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.MllpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.SftpEndpoint;

public class DestinationRouterTest {

    @InjectMocks
    private DestinationRouter destinationRouter;

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
        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setType(EndpointType.MLLP);

        Mockito.when(mllpEndpoint.generateUri(ArgumentMatchers.any(DestinationEndpoint.class)))
                .thenReturn("mllp://localhost:9000");

        String destinationUri = destinationRouter.redirect(destinationEndpoint,dataPipelineId);
        assertEquals("mllp://localhost:9000", destinationUri);
    }

    @Test
    public void testRedirectFile() {
        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setType(EndpointType.FILE);

        Mockito.when(fileEndpoint.generateUri(ArgumentMatchers.any(DestinationEndpoint.class)))
                .thenReturn("file:testDirectory");

       String destinationUri = destinationRouter.redirect(destinationEndpoint,dataPipelineId);
        assertEquals("file:testDirectory", destinationUri);
    }

    @Test
    public void testRedirectSftp() {
        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setType(EndpointType.SFTP);

        Mockito.when(sftpEndpoint.generateUri(ArgumentMatchers.any(DestinationEndpoint.class)))
        .thenReturn("sftp:myhost.domain.com:22/testDirectory?username=username&password=password&fileName=test.txt&fileExist=append");

        String destinationUri = destinationRouter.redirect(destinationEndpoint,dataPipelineId);
        assertEquals("sftp:myhost.domain.com:22/testDirectory?username=username&password=password&fileName=test.txt&fileExist=append",
                destinationUri);
    }

    @Test
    public void testRedirectHttp() {
        DestinationEndpoint destinationEndpoint = new DestinationEndpoint();
        destinationEndpoint.setType(EndpointType.HTTP);

        Mockito.when(httpEndpoint.generateUri(ArgumentMatchers.any(DestinationEndpoint.class), ArgumentMatchers.anyString()))
        .thenReturn("http4://localhost:9000/resource?bridgeEndpoint=true");

        String destinationUri = destinationRouter.redirect(destinationEndpoint,dataPipelineId);
        assertEquals("http4://localhost:9000/resource?bridgeEndpoint=true", destinationUri);
    }
}
