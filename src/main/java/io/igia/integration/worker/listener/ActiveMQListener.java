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

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.Deployer;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.Message;
import io.igia.integration.worker.datapipeline.dto.MessageData;
import io.igia.integration.worker.datapipeline.dto.MessageStatus;
import io.igia.integration.worker.datapipeline.dto.MessageSubType;
import io.igia.integration.worker.datapipeline.dto.MessageType;
import io.igia.integration.worker.datapipeline.dto.ProvisionMessageStatus;
import io.igia.integration.worker.datapipeline.dto.ProvisionMessageSubType;
import io.igia.integration.worker.datapipeline.dto.ProvisionRequestMessageData;
import io.igia.integration.worker.datapipeline.dto.ProvisionResponseMessageData;

@Component
public class ActiveMQListener {

    private final Logger log = LoggerFactory.getLogger(ActiveMQListener.class);

    @Autowired
    private Deployer deployer;

    @Autowired
    private ProducerTemplate producerTemplate;

    public void consume(Message message) throws Exception {

        ProvisionRequestMessageData provisionRequestMessageData = (ProvisionRequestMessageData) message.getData();
        DataPipeline dataPipeline = provisionRequestMessageData.getDataPipeline();
        Long dataPipelineId = dataPipeline.getId();
        String dataPipelineName = dataPipeline.getName();
        if (dataPipeline.isDeploy()) {
            log.info("DataPipeline deployment started : {}", dataPipelineName);
            try {
                deployer.deploy(dataPipeline);
                updateMessage(message, ProvisionMessageSubType.DEPLOY,
                        getMessageData(dataPipelineId, ProvisionMessageStatus.SUCCESS));
                log.info("DataPipeline deployment completed successfully : {}", dataPipelineName);
            } catch (Exception e) {
                ProvisionResponseMessageData provisionResponseMessageData = getMessageData(dataPipelineId,
                        ProvisionMessageStatus.FAILURE);
                provisionResponseMessageData.setErrorMessage(e.getMessage());
                updateMessage(message, ProvisionMessageSubType.DEPLOY, provisionResponseMessageData);
                log.error("DataPipeline deployment failed : {} : {}", dataPipelineName, e);
            }
        } else {
            log.info("DataPipeline undeployment started : {}", dataPipelineName);
            try {
                deployer.undeploy(dataPipeline);
                updateMessage(message, ProvisionMessageSubType.UNDEPLOY,
                        getMessageData(dataPipelineId, ProvisionMessageStatus.SUCCESS));
                log.info("DataPipeline undeployment completed successfully : {}", dataPipelineName);
            } catch (Exception e) {
                ProvisionResponseMessageData provisionResponseMessageData = getMessageData(dataPipelineId,
                        ProvisionMessageStatus.FAILURE);
                provisionResponseMessageData.setErrorMessage(e.getMessage());
                updateMessage(message, ProvisionMessageSubType.UNDEPLOY, provisionResponseMessageData);
                log.error("DataPipeline undeployment failed : {} : {}", dataPipelineName, e);
            }
        }
        producerTemplate.sendBody(Constants.OUT_MESSAGE_QUEUE_FROM_ENDPOINT, message);
    }

    private void updateMessage(Message message, MessageSubType messageSubType, MessageData messageData) {
        message.setType(MessageType.PROVISION);
        message.setSubType(messageSubType);
        message.setData(messageData);
    }

    private ProvisionResponseMessageData getMessageData(Long dataPipelineId, MessageStatus messageStatus) {
        ProvisionResponseMessageData provisionResponseMessageData = new ProvisionResponseMessageData();
        provisionResponseMessageData.setDataPipelineId(dataPipelineId);
        provisionResponseMessageData.setStatus(messageStatus);
        return provisionResponseMessageData;
    }
}
