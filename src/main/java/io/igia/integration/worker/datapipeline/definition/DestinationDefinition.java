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
package io.igia.integration.worker.datapipeline.definition;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.dto.AuditMessageEventType;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.MessageDataType;

@Component
public class DestinationDefinition {

    public RouteBuilder configure(DataPipeline dataPipeline) {

        return new CustomRouteBuilder() {

            @Override
            public void configure() throws Exception {
                List<DestinationEndpoint> destinationEndpoints = dataPipeline.getDestinations();
                String groupName = dataPipeline.getId().toString();

                for (DestinationEndpoint destinationEndpoint : destinationEndpoints) {

                    ProcessorDefinition<?> processorDefinition = from("direct:" + destinationEndpoint.getName() + groupName)
                            .routeGroup(groupName);

                    processorDefinition = processorDefinition
                            .setProperty(Constants.PROPERTY_DATAPIPELINE_ID, simple(groupName))
                            .setProperty(Constants.PROPERTY_ENDPOINT_NAME, simple(destinationEndpoint.getName()));

                    List<AuditMessageEventType> auditMessageEventTypes = dataPipeline.getAuditMessages();
                    if (auditMessageEventTypes != null && !auditMessageEventTypes.isEmpty()) {
                        processorDefinition = routeErrorMessages(processorDefinition, auditMessageEventTypes);
                    }

                    MessageDataType messageDataType = destinationEndpoint.getInDataType();
                    processorDefinition = unmarshalInDataType(processorDefinition, messageDataType);

                    processorDefinition = addFilters(processorDefinition, destinationEndpoint.getFilters(), groupName,
                            Constants.CAMEL_ENDPOINT_DESTINATION_PROCESS);

                    if (auditMessageEventTypes != null && !auditMessageEventTypes.isEmpty()) {
                        wiretapFilteredMessages(processorDefinition, auditMessageEventTypes, messageDataType);
                    }
                }
            }
        };
    }
}
