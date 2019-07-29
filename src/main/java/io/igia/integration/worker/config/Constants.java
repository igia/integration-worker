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

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {
    }

    public static final String OUT_MESSAGE_QUEUE_FROM_ENDPOINT = "direct:sendToQueue";
    public static final String OUT_MESSAGE_QUEUE_PREFIX = "igia-jms:queue:";
    public static final String IN_MESSAGE_TOPIC_PREFIX = "igia-jms:topic:";
    public static final String HL7_MESSAGE_HEADER = "MSH";

    public static final String PROPERTY_DATAPIPELINE_ID = "dataPipelineId";
    public static final String PROPERTY_ENDPOINT_NAME = "endpointName";
    public static final String PROPERTY_MESSAGE_TYPE = "messageType";
    public static final String PROPERTY_ERROR_MESSAGE = "error";

    public static final String CAMEL_ENDPOINT_ALL_MESSAGES = "seda:ALLMESSAGES";
    public static final String CAMEL_ENDPOINT_ERROR_MESSAGES = "seda:ERRORMESSAGES";
    public static final String CAMEL_ENDPOINT_FILTERED_MESSAGES = "seda:FILTEREDMESSAGES";
    public static final String CAMEL_ENDPOINT_AUDIT = "seda:AUDIT";
    public static final String CAMEL_ENDPOINT_SOURCE_PROCESS = "direct:SOURCEPROCESS";
    public static final String CAMEL_ENDPOINT_DESTINATION_PROCESS = "direct:DESTINATIONPROCESS";

    public static final String ENDPOINT_PROPERTY_HOSTNAME = "hostname";
    public static final String ENDPOINT_PROPERTY_PORT = "port";
    public static final String ENDPOINT_PROPERTY_DIRECTORYNAME = "directoryName";
    public static final String ENDPOINT_PROPERTY_SORTBY = "sortBy";
    public static final String ENDPOINT_PROPERTY_PRESORT = "preSort";
    public static final String ENDPOINT_PROPERTY_SCHEDULER = "scheduler";
    public static final String ENDPOINT_PROPERTY_SCHEDULER_CRON = "scheduler.cron";

    public static final String MLLP_PROTOCOL = "igia-mllp://";
    public static final String MLLP_PROPERTY_CONNECT_TIMEOUT = "connectTimeout";
    public static final String MLLP_PROPERTY_CONNECT_TIMEOUT_DEFAULT = "30000";

    public static final String FILE_PROTOCOL = "file:";

    public static final String SFTP_PROTOCOL = "sftp:";
    public static final String SFTP_PROPERTY_PASSWORD = "password";

    public static final String JETTY_PROTOCOL = "igia-jetty:";
    public static final String HTTP_PROTOCOL = "igia-http://";
    public static final String HTTP_PROPERTY_RESOURCE_URI = "resourceUri";
    public static final String HTTP_PROPERTY_AUTHPASSWORD = "authPassword";
    public static final String HTTPS_PROTOCOL = "https://";

}
