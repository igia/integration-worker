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
package io.igia.integration.worker.listener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.Deployer;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.Message;
import io.igia.integration.worker.datapipeline.dto.ProvisionMessageStatus;
import io.igia.integration.worker.datapipeline.dto.ProvisionMessageSubType;
import io.igia.integration.worker.datapipeline.dto.ProvisionRequestMessageData;
import io.igia.integration.worker.datapipeline.dto.ProvisionResponseMessageData;
import io.igia.integration.worker.listener.ActiveMQListener;

public class ActiveMQListenerTest {

    @InjectMocks
    private ActiveMQListener activeMQListener;

    @Mock
    private Deployer deployer;

    @Mock
    private ProducerTemplate producerTemplate;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(producerTemplate).sendBody(ArgumentMatchers.anyString(), ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testConsumeDeploySuccess() throws Exception {
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(8L);
        dataPipeline.setDeploy(Boolean.TRUE);
        Message message = getMessage(dataPipeline);

        doNothing().when(deployer).deploy(ArgumentMatchers.any(DataPipeline.class));

        activeMQListener.consume(message);

        assertEquals(ProvisionMessageSubType.DEPLOY, message.getSubType());
        ProvisionResponseMessageData provisionResponseMessageData = (ProvisionResponseMessageData) message.getData();
        assertEquals(8, provisionResponseMessageData.getDataPipelineId().intValue());
        assertEquals(ProvisionMessageStatus.SUCCESS, provisionResponseMessageData.getStatus());
    }

    @Test
    public void testConsumeDeployFail() throws Exception {
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(8L);
        dataPipeline.setDeploy(Boolean.TRUE);
        Message message = getMessage(dataPipeline);

        Exception exception = new Exception("Failure Reason");
        doThrow(exception).when(deployer).deploy(ArgumentMatchers.any(DataPipeline.class));

        activeMQListener.consume(message);

        assertEquals(ProvisionMessageSubType.DEPLOY, message.getSubType());
        ProvisionResponseMessageData provisionResponseMessageData = (ProvisionResponseMessageData) message.getData();
        assertEquals(8, provisionResponseMessageData.getDataPipelineId().intValue());
        assertEquals(ProvisionMessageStatus.FAILURE, provisionResponseMessageData.getStatus());
        assertEquals("Failure Reason", provisionResponseMessageData.getErrorMessage());
    }

    @Test
    public void testConsumeUndeploySuccess() throws Exception {
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(8L);
        dataPipeline.setDeploy(Boolean.FALSE);
        Message message = getMessage(dataPipeline);

        doNothing().when(deployer).undeploy(ArgumentMatchers.any(DataPipeline.class));

        activeMQListener.consume(message);

        assertEquals(ProvisionMessageSubType.UNDEPLOY, message.getSubType());
        ProvisionResponseMessageData provisionResponseMessageData = (ProvisionResponseMessageData) message.getData();
        assertEquals(8, provisionResponseMessageData.getDataPipelineId().intValue());
        assertEquals(ProvisionMessageStatus.SUCCESS, provisionResponseMessageData.getStatus());
    }

    @Test
    public void testConsumeUndeployFail() throws Exception {
        DataPipeline dataPipeline = new DataPipeline();
        dataPipeline.setId(8L);
        dataPipeline.setDeploy(Boolean.FALSE);
        Message message = getMessage(dataPipeline);

        Exception exception = new Exception("Failure Reason");
        doThrow(exception).when(deployer).undeploy(ArgumentMatchers.any(DataPipeline.class));

        activeMQListener.consume(message);

        assertEquals(ProvisionMessageSubType.UNDEPLOY, message.getSubType());
        ProvisionResponseMessageData provisionResponseMessageData = (ProvisionResponseMessageData) message.getData();
        assertEquals(8, provisionResponseMessageData.getDataPipelineId().intValue());
        assertEquals(ProvisionMessageStatus.FAILURE, provisionResponseMessageData.getStatus());
        assertEquals("Failure Reason", provisionResponseMessageData.getErrorMessage());
    }

    private Message getMessage(DataPipeline dataPipeline) {
        ProvisionRequestMessageData provisionRequestMessageData = new ProvisionRequestMessageData();
        provisionRequestMessageData.setDataPipeline(dataPipeline);
        Message message = new Message();
        message.setData(provisionRequestMessageData);
        return message;
    }
}
