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
package io.igia.integration.worker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfiguration {

    private ActiveMQConnectionFactory jmsConnectionFactory(JmsComponentProperties jmsProperties) throws Exception {
        final ActiveMQSslConnectionFactory connectionFactory =
            new ActiveMQSslConnectionFactory(jmsProperties.getBrokerUrl());

        // configure connection properties
        connectionFactory.setUserName(jmsProperties.getUser());
        connectionFactory.setPassword(jmsProperties.getPassword());
        connectionFactory.setCloseTimeout((int)jmsProperties.getCloseTimeout().toMillis());
        connectionFactory.setSendTimeout((int)jmsProperties.getSendTimeout().toMillis());
        connectionFactory.setTrustAllPackages(true);

        // configure SSL properties
        JmsComponentProperties.JmsSsl sslProperties = jmsProperties.getSsl();
        connectionFactory.setTrustStore(sslProperties.getTrustStore());
        connectionFactory.setTrustStorePassword(sslProperties.getTrustStorePassword());
        connectionFactory.setKeyStore(sslProperties.getKeyStore());
        connectionFactory.setKeyStorePassword(sslProperties.getKeyStorePassword());

        return connectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(
        JmsComponentProperties jmsProperties) throws Exception {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(jmsConnectionFactory(jmsProperties));

        JmsComponentProperties.JmsPool jmsPoolProperties = jmsProperties.getPool();
        pooledConnectionFactory.setIdleTimeout((int) jmsPoolProperties.getIdleTimeout().toMillis());
        pooledConnectionFactory.setMaxConnections(jmsPoolProperties.getMaxConnections());
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(jmsPoolProperties.getMaximumActiveSessionPerConnection());
        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(jmsPoolProperties.getTimeBetweenExpirationCheck().toMillis());
        return pooledConnectionFactory;
    }

    @Bean("igia-jms")
    public JmsComponent igiaJmsComponent(PooledConnectionFactory connectionFactory) {
        return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
    }
}
