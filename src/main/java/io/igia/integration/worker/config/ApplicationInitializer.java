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
package io.igia.integration.worker.config;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.datapipeline.dto.HealthCheckMessageStatus;
import io.igia.integration.worker.datapipeline.dto.HealthCheckResponseMessageData;
import io.igia.integration.worker.datapipeline.dto.Message;
import io.igia.integration.worker.datapipeline.dto.MessageType;

@Component
public class ApplicationInitializer implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HealthCheckResponseMessageData messageData = new HealthCheckResponseMessageData();
        messageData.setStatus(HealthCheckMessageStatus.UP);
        messageData.setWorkerService(appName);
        messageData.setText("Integration Worker Started");

        Message message = new Message();
        message.setType(MessageType.HEALTHCHECK);
        message.setData(messageData);

        producerTemplate.sendBody(Constants.OUT_MESSAGE_QUEUE_FROM_ENDPOINT, message);
    }
}
