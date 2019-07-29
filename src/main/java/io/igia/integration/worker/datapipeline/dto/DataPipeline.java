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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * DTO encapsulating details to build camel routes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private boolean deploy;
    private DataPipelineStateType state;
    private String reason;
    private SourceEndpoint source;
    private List<DestinationEndpoint> destinations;

    private List<AuditMessageEventType> auditMessages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }

    public DataPipelineStateType getState() {
        return state;
    }

    public void setState(DataPipelineStateType state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public SourceEndpoint getSource() {
        return source;
    }

    public void setSource(SourceEndpoint source) {
        this.source = source;
    }

    public List<DestinationEndpoint> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationEndpoint> destinations) {
        this.destinations = destinations;
    }

    public List<AuditMessageEventType> getAuditMessages() {
        return auditMessages;
    }

    public void setAuditMessages(List<AuditMessageEventType> auditMessages) {
        this.auditMessages = auditMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPipeline that = (DataPipeline) o;
        return id == that.id &&
            name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "DataPipeline{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", deploy='" + deploy + '\'' +
            ", state=" + state +
            ", reason='" + reason + '\'' +
            ", source=" + source +
            ", destinations=" + destinations +
            ", auditMessages=" + auditMessages +
            '}';
    }
}
