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
package io.igia.integration.worker.jetty;

import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.igia.integration.worker.config.HttpComponentProperties;

@Configuration
public class JettyConfiguration {

    @Bean("igia-jetty")
    public JettyHttpComponent  igiaJettyComponent(HttpComponentProperties properties) {
        HttpComponentProperties.HttpConsumer.HttpSsl sslProperties = properties.getConsumer().getSsl();

        KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
        keyStoreParameters.setResource(sslProperties.getKeyStore());
        keyStoreParameters.setPassword(sslProperties.getKeyStorePassword());

        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
        keyManagersParameters.setKeyStore(keyStoreParameters);
        keyManagersParameters.setKeyPassword(sslProperties.getKeyStorePassword());

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(keyManagersParameters);

        JettyHttpComponent jettyComponent =  new JettyHttpComponent9();
        jettyComponent.setSslContextParameters(sslContextParameters);
        return jettyComponent;
    }
}
