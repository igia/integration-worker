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
package io.igia.integration.worker.datapipeline.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.dto.AuditMessage;
import io.igia.integration.worker.datapipeline.dto.AuditMessageEventType;
import io.igia.integration.worker.datapipeline.dto.AuditMessageExchange;
import io.igia.integration.worker.datapipeline.dto.AuditMessageInfo;

@Component
public class AuditMessageMapper {

    public void toAuditMessage(Exchange exchange) {
        AuditMessage auditMessage = new AuditMessage();
        AuditMessageEventType messageType = AuditMessageEventType.valueOf(exchange.getProperty(Constants.PROPERTY_MESSAGE_TYPE).toString());
        AuditMessageExchange auditMessageExchange = toAuditMessageExchange(exchange);
        auditMessage.setMessageType(messageType);
        auditMessage.setExchange(auditMessageExchange);
        exchange.getIn().setBody(auditMessage);
    }

    private AuditMessageExchange toAuditMessageExchange(Exchange exchange) {
        AuditMessageExchange auditMessageExchange = new AuditMessageExchange();
        List<AuditMessageInfo> headers = new ArrayList<>();
        for (Entry<String, Object> entry : exchange.getIn().getHeaders().entrySet()) {
            AuditMessageInfo auditMessageInfo = new AuditMessageInfo();
            auditMessageInfo.setKey(entry.getKey());
            auditMessageInfo.setValue(entry.getValue());
            headers.add(auditMessageInfo);
        }
        List<AuditMessageInfo> properties = new ArrayList<>();
        for (Entry<String, Object> entry : exchange.getProperties().entrySet()) {
            AuditMessageInfo auditMessageInfo = new AuditMessageInfo();
            auditMessageInfo.setKey(entry.getKey());
            auditMessageInfo.setValue(entry.getValue());
            properties.add(auditMessageInfo);
        }
        auditMessageExchange.setHeaders(headers);
        auditMessageExchange.setProperties(properties);
        auditMessageExchange.setBody(exchange.getIn().getBody(String.class));
        return auditMessageExchange;
    }
}
