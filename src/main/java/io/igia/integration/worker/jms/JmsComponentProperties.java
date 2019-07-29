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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Properties specific to JMS component
 */
@Configuration
@ConfigurationProperties(prefix = "jms.component")
public class JmsComponentProperties {

    private String brokerUrl;
    private String user;
    private String password;
    private Duration closeTimeout = Duration.ofSeconds(15);
    private Duration sendTimeout = Duration.ofMillis(0);
    private JmsPool pool = new JmsPool();
    private JmsSsl ssl = new JmsSsl();

    public String getBrokerUrl() {
        return this.brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getCloseTimeout() {
        return this.closeTimeout;
    }

    public void setCloseTimeout(Duration closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public Duration getSendTimeout() {
        return this.sendTimeout;
    }

    public void setSendTimeout(Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    public JmsPool getPool() {
        return pool;
    }

    public void setPool(JmsPool pool) {
        this.pool = pool;
    }

    public JmsSsl getSsl() {
        return ssl;
    }

    public void setSsl(JmsSsl ssl) {
        this.ssl = ssl;
    }

    public static class JmsPool {

        private Duration idleTimeout = Duration.ofSeconds(30);
        private int maxConnections = 1;
        private int maximumActiveSessionPerConnection = 500;
        private Duration timeBetweenExpirationCheck = Duration.ofMillis(-1);

        public Duration getIdleTimeout() {
            return this.idleTimeout;
        }

        public void setIdleTimeout(Duration idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public int getMaxConnections() {
            return this.maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getMaximumActiveSessionPerConnection() {
            return this.maximumActiveSessionPerConnection;
        }

        public void setMaximumActiveSessionPerConnection(
                int maximumActiveSessionPerConnection) {
            this.maximumActiveSessionPerConnection = maximumActiveSessionPerConnection;
        }

        public Duration getTimeBetweenExpirationCheck() {
            return this.timeBetweenExpirationCheck;
        }

        public void setTimeBetweenExpirationCheck(Duration timeBetweenExpirationCheck) {
            this.timeBetweenExpirationCheck = timeBetweenExpirationCheck;
        }
    }

    public static class JmsSsl {
        private String trustStore;
        private String trustStorePassword;
        private String keyStore;
        private String keyStorePassword;

        public String getTrustStore() {
            return trustStore;
        }

        public void setTrustStore(String trustStore) {
            this.trustStore = trustStore;
        }

        public String getTrustStorePassword() {
            return trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }

        public String getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }
    }
}


