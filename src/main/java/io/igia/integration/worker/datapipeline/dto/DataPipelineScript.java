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
import java.util.Objects;

/**
 * DTO encapsulating details to apply JSR scripts.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPipelineScript implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Script data
     */
    private String data;

    /**
     * Order specifying precedence while building routes
     */
    private int order;

    /**
     * JSR 223 compliant script type
     */
    private ScriptType type;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public ScriptType getType() {
        return type;
    }

    public void setType(ScriptType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPipelineScript that = (DataPipelineScript) o;
        return order == that.order &&
            data.equals(that.data) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, order, type);
    }

    @Override
    public String toString() {
        return "DataPipelineScript{" +
            "data='" + data + '\'' +
            ", order=" + order +
            ", type=" + type +
            '}';
    }
}
