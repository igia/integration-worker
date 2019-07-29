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
import io.igia.integration.worker.config.SftpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.Endpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

@Component
public class SftpEndpoint {

    private final Logger log = LoggerFactory.getLogger(SftpEndpoint.class);

    @Autowired
    private EndpointConfigMapper endpointConfigMapper;

    @Autowired
    private SftpEndpointProperties sftpEndpointProperties;

    public String generateUri(Endpoint endpoint) {

        Map<String, String> configMap = new HashMap<>();
        configMap.putAll(sftpEndpointProperties.getCommon());

        if (endpoint instanceof SourceEndpoint) {
            configMap.putAll(sftpEndpointProperties.getConsumer());
        } else {
            configMap.putAll(sftpEndpointProperties.getProducer());
        }

        List<EndpointConfiguration> configurations = endpoint.getConfigurations();
        for (EndpointConfiguration configuration : configurations) {
            configMap.put(configuration.getKey(), configuration.getValue());
        }

        String sortBy = configMap.get(Constants.ENDPOINT_PROPERTY_SORTBY);
        if (sortBy != null) {
            configMap.put(Constants.ENDPOINT_PROPERTY_PRESORT, "true");
        }

        String schedulerCron = configMap.get(Constants.ENDPOINT_PROPERTY_SCHEDULER_CRON);
        if (schedulerCron == null) {
            configMap.remove(Constants.ENDPOINT_PROPERTY_SCHEDULER);
        }

        StringBuilder uri = new StringBuilder(Constants.SFTP_PROTOCOL)
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_HOSTNAME)).append(":")
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_PORT)).append("/")
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_DIRECTORYNAME)).append("?");
        configMap.remove(Constants.ENDPOINT_PROPERTY_HOSTNAME);
        configMap.remove(Constants.ENDPOINT_PROPERTY_PORT);
        configMap.remove(Constants.ENDPOINT_PROPERTY_DIRECTORYNAME);

        String baseUri = uri.toString();
        uri.append(endpointConfigMapper.mapToUriQueryString(configMap));

        configMap.remove(Constants.SFTP_PROPERTY_PASSWORD);
        if (log.isInfoEnabled()) {
            log.info("Generated URI : {}", baseUri.concat(endpointConfigMapper.mapToUriQueryString(configMap)));   
        }

        return uri.toString();
    }
}
