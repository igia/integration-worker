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
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.datapipeline.dto.DataPipeline;
import io.igia.integration.worker.datapipeline.dto.EndpointType;
import io.igia.integration.worker.datapipeline.endpoint.util.EndpointUtil;

@Component
public class Deployer {

    @Autowired
    private Builder builder;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EndpointUtil endpointUtil;

    public void deploy(DataPipeline dataPipeline) throws Exception {

        RouteBuilder routeBuilder = builder.buildRoutes(dataPipeline);
        camelContext.addRoutes(routeBuilder);
    }

    public void undeploy(DataPipeline dataPipeline) throws Exception {
        List<RouteDefinition> routeDefinitions = camelContext.getRouteDefinitions()
                .parallelStream()
                .filter(route -> route.getGroup() != null && route.getGroup().equals(dataPipeline.getId().toString()))
                .collect(Collectors.toList());
        for (RouteDefinition routeDefinition : routeDefinitions) {
            routeDefinition.stop();
            camelContext.removeRouteDefinition(routeDefinition);
        }
        if(!routeDefinitions.isEmpty() && dataPipeline.getSource().getType().equals(EndpointType.HTTP)){
            removeHandler(endpointUtil.getHandlerName(dataPipeline.getId().toString()));
        }
    }

    private void removeHandler(String handlerName){
        GenericApplicationContext genericApplicationContext =  (GenericApplicationContext) applicationContext;
        genericApplicationContext.removeBeanDefinition(handlerName);
    }
}
