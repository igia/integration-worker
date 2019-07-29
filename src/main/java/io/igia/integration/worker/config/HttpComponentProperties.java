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
package io.igia.integration.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Integrationworker.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "http.component", ignoreUnknownFields = false)
public class HttpComponentProperties {

    private int httpConnectTimeout;
    private int httpSocketTimeout;
    private HttpConsumer consumer = new HttpConsumer();

    public int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(int httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public int getHttpSocketTimeout() {
        return httpSocketTimeout;
    }

    public void setHttpSocketTimeout(int httpSocketTimeout) {
        this.httpSocketTimeout = httpSocketTimeout;
    }

    public HttpConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(HttpConsumer consumer) {
        this.consumer = consumer;
    }

    public static class HttpConsumer {
        private HttpSsl ssl = new HttpSsl();

        public HttpSsl getSsl() {
            return ssl;
        }

        public void setSsl(HttpSsl ssl) {
            this.ssl = ssl;
        }

        public static class HttpSsl {
            private String keyStore;
            private String keyStorePassword;

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
}
