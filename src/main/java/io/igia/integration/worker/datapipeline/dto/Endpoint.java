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
 * DTO encapsulating endpoint details to be used while building camel routes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Endpoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private EndpointType type;
    private String name;
    private MessageDataType inDataType;
    private MessageDataType outDataType;

    private List<EndpointConfiguration> configurations;
    private List<DataPipelineScript> filters;
    private List<DataPipelineScript> transformers;

    public EndpointType getType() {
        return type;
    }

    public void setType(EndpointType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageDataType getInDataType() {
        return inDataType;
    }

    public void setInDataType(MessageDataType inDataType) {
        this.inDataType = inDataType;
    }

    public MessageDataType getOutDataType() {
        return outDataType;
    }

    public void setOutDataType(MessageDataType outDataType) {
        this.outDataType = outDataType;
    }

    public List<EndpointConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<EndpointConfiguration> configurations) {
        this.configurations = configurations;
    }

    public List<DataPipelineScript> getFilters() {
        return filters;
    }

    public void setFilters(List<DataPipelineScript> filters) {
        this.filters = filters;
    }

    public List<DataPipelineScript> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<DataPipelineScript> transformers) {
        this.transformers = transformers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return type == endpoint.type &&
            name.equals(endpoint.name) &&
            inDataType == endpoint.inDataType &&
            outDataType == endpoint.outDataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, inDataType, outDataType);
    }

    @Override
    public String toString() {
        return "Endpoint{" +
            "type=" + type +
            ", name='" + name + '\'' +
            ", inDataType=" + inDataType +
            ", outDataType=" + outDataType +
            ", configurations=" + configurations +
            '}';
    }
}
