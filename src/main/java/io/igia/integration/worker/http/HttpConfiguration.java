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
package io.igia.integration.worker.http;

import org.apache.camel.component.http4.HttpComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.igia.integration.worker.config.HttpComponentProperties;

@Configuration
public class HttpConfiguration {

    @Bean("igia-http")
    public HttpComponent httpComponent(HttpComponentProperties httpComponentProperties) {
        HttpComponent httpComponent = new HttpComponent();
        httpComponent.setConnectTimeout(httpComponentProperties.getHttpConnectTimeout());
        httpComponent.setSocketTimeout(httpComponentProperties.getHttpSocketTimeout());
        return httpComponent;
    }
}
