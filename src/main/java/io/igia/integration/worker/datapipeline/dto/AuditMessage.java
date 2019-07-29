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
package io.igia.integration.worker.datapipeline.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO encapsulating details to save audit message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private AuditMessageEventType messageType;
    private AuditMessageExchange exchange;
    private Instant timestamp = Instant.now();

    public AuditMessageEventType getMessageType() {
        return messageType;
    }

    public void setMessageType(AuditMessageEventType messageType) {
        this.messageType = messageType;
    }

    public AuditMessageExchange getExchange() {
        return exchange;
    }

    public void setExchange(AuditMessageExchange exchange) {
        this.exchange = exchange;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuditMessage other = (AuditMessage) obj;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (messageType != other.messageType)
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AuditMessage [messageType=" + messageType + ", exchange=" + exchange + ", timestamp=" + timestamp + "]";
    }
}
