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
package io.igia.integration.worker.datapipeline.endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.config.MllpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.Endpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

@Component
public class MllpEndpoint {

    private final Logger log = LoggerFactory.getLogger(MllpEndpoint.class);

    @Autowired
    private EndpointConfigMapper endpointConfigMapper;

    @Autowired
    private MllpEndpointProperties mllpEndpointProperties;

    public String generateUri(Endpoint endpoint) {

        Map<String, String> configMap = new HashMap<>();

        if (endpoint instanceof SourceEndpoint) {
            configMap.putAll(mllpEndpointProperties.getConsumer());
        }

        List<EndpointConfiguration> configurations = endpoint.getConfigurations();
        for (EndpointConfiguration configuration : configurations) {
            configMap.put(configuration.getKey(), configuration.getValue());
        }

        if (endpoint instanceof DestinationEndpoint) {
            String connectTimeout = configMap.get(Constants.MLLP_PROPERTY_CONNECT_TIMEOUT);
            if (connectTimeout == null) {
                configMap.put(Constants.MLLP_PROPERTY_CONNECT_TIMEOUT, Constants.MLLP_PROPERTY_CONNECT_TIMEOUT_DEFAULT);
            }
        }

        StringBuilder uri = new StringBuilder(Constants.MLLP_PROTOCOL)
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_HOSTNAME)).append(":")
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_PORT)).append("?");
        configMap.remove(Constants.ENDPOINT_PROPERTY_HOSTNAME);
        configMap.remove(Constants.ENDPOINT_PROPERTY_PORT);

        uri.append(endpointConfigMapper.mapToUriQueryString(configMap));
        log.info("Generated URI : {}", uri);
        return uri.toString();
    }
}
