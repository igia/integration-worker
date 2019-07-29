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
package io.igia.integration.worker.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.ApplicationProperties;
import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.dto.Message;
import io.igia.integration.worker.encrypt.EncryptionUtility;
import io.igia.integration.worker.listener.ActiveMQListener;

@Component
public class ProvisionDataPipelineRoute extends RouteBuilder {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private EncryptionUtility encryptionUtility;

    @Override
    public void configure() throws Exception {

        final String IN_MESSAGE_TOPIC_ENDPOINT = Constants.IN_MESSAGE_TOPIC_PREFIX
                .concat(applicationProperties.getMessageInTopic());

        from(IN_MESSAGE_TOPIC_ENDPOINT)
        .routeId("jms_topic_datapipeline_" + applicationProperties.getMessageInTopic())
        .unmarshal(encryptionUtility.getCryptoDataFormat())
        .unmarshal().json(JsonLibrary.Jackson, Message.class)
        .bean(ActiveMQListener.class);
    }
}
