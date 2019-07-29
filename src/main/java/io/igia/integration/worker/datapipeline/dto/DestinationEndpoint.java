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

import java.util.List;

/**
 * DTO encapsulating endpoint details to be used as destination while building data pipeline.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class DestinationEndpoint extends Endpoint {
    private static final long serialVersionUID = 1L;

    private List<DataPipelineScript> responseTransformers;

    public List<DataPipelineScript> getResponseTransformers() {
        return responseTransformers;
    }

    public void setResponseTransformers(List<DataPipelineScript> responseTransformers) {
        this.responseTransformers = responseTransformers;
    }
}
