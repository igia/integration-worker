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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.igia.integration.worker.config.ApplicationProperties;
import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.dto.AuditMessage;
import io.igia.integration.worker.datapipeline.dto.AuditMessageEventType;
import io.igia.integration.worker.datapipeline.dto.AuditMessageExchange;
import io.igia.integration.worker.datapipeline.dto.AuditMessageInfo;
import io.igia.integration.worker.datapipeline.mapper.AuditMessageMapper;
import io.igia.integration.worker.encrypt.EncryptionUtility;
import io.igia.integration.worker.route.util.RouteUtil;

@Component
public class AuditMessagesRoute extends RouteBuilder {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RouteUtil routeUtil;

    @Autowired
    private EncryptionUtility encryptionUtility;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure() throws Exception {

        from(Constants.CAMEL_ENDPOINT_AUDIT)
        .routeId("audit_messages")
        .bean(AuditMessageMapper.class, "toAuditMessage")
        .process(exchange -> {
            AuditMessage auditMessage = exchange.getIn().getBody(AuditMessage.class);
            AuditMessageExchange auditMessageExchange = auditMessage.getExchange();
            for (AuditMessageInfo header : auditMessageExchange.getHeaders()) {
                if (header.getValue() != null && !header.getValue().toString().isEmpty()) {
                    header.setValue(encryptionUtility.encrypt(header.getValue().toString()));
                }
            }
            for (AuditMessageInfo property : auditMessageExchange.getProperties()) {
                if (property.getValue() != null && !property.getValue().toString().isEmpty()) {
                    property.setValue(encryptionUtility.encrypt(property.getValue().toString()));
                }
            }
            auditMessageExchange.setBody(encryptionUtility.encrypt(auditMessageExchange.getBody()));
            auditMessage.setExchange(auditMessageExchange);
            exchange.getIn().setBody(objectMapper.writeValueAsString(auditMessage).concat(System.lineSeparator()));
        })
        .choice()
        .when(exchangeProperty(Constants.PROPERTY_MESSAGE_TYPE).isEqualTo(AuditMessageEventType.ALL.name()))
        .to(routeUtil.getAuditFileEndpoint(applicationProperties.getAudit().get("all-messages-filepath")))
        .when(exchangeProperty(Constants.PROPERTY_MESSAGE_TYPE).isEqualTo(AuditMessageEventType.ERROR.name()))
        .to(routeUtil.getAuditFileEndpoint(applicationProperties.getAudit().get("error-messages-filepath")))
        .when(exchangeProperty(Constants.PROPERTY_MESSAGE_TYPE).isEqualTo(AuditMessageEventType.FILTERED.name()))
        .to(routeUtil.getAuditFileEndpoint(applicationProperties.getAudit().get("filtered-out-messages-filepath")));
    }
}
