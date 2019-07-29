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
package io.igia.integration.worker.datapipeline;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.datapipeline.definition.DestinationDefinition;
import io.igia.integration.worker.datapipeline.definition.DestinationProcessDefinition;
import io.igia.integration.worker.datapipeline.definition.SourceDefinition;
import io.igia.integration.worker.datapipeline.definition.SourceProcessDefinition;
import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;

@Component
public class Builder {
    
    @Autowired
    private SourceDefinition sourceDefinition;
    
    @Autowired
    private DestinationDefinition destinationDefinition;

    @Autowired
    private SourceProcessDefinition sourceProcessDefinition;

    @Autowired
    private DestinationProcessDefinition destinationProcessDefinition;

    public RouteBuilder buildRoutes(DataPipeline dataPipeline) throws Exception {
        SourceEndpoint sourceEndpoint = dataPipeline.getSource();
        List<DestinationEndpoint> destinationEndpoints = dataPipeline.getDestinations();

        /*
         * Below validation part will be moved to other location while error handling
         */
        if (sourceEndpoint == null) {
            throw new NullPointerException("Source is null");
        }

        if (destinationEndpoints == null || destinationEndpoints.isEmpty()) {
            throw new NullPointerException("Destination is null/empty");
        }
        
        RouteBuilder routeBuilder = sourceDefinition.configure(dataPipeline);
        routeBuilder.includeRoutes(sourceProcessDefinition.configure(dataPipeline));
        routeBuilder.includeRoutes(destinationDefinition.configure(dataPipeline));
        routeBuilder.includeRoutes(destinationProcessDefinition.configure(dataPipeline));
        return routeBuilder;
    }
}
