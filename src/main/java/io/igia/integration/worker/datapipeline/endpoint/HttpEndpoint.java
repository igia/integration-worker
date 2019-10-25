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

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.config.HttpEndpointProperties;
import io.igia.integration.worker.datapipeline.dto.DestinationEndpoint;
import io.igia.integration.worker.datapipeline.dto.Endpoint;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.util.EndpointUtil;
import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

@Component
public class HttpEndpoint {

    private final Logger log = LoggerFactory.getLogger(HttpEndpoint.class);

    @Autowired
    private EndpointConfigMapper endpointConfigMapper;

    @Autowired
    private HttpEndpointProperties httpEndpointProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EndpointUtil endpointUtil;

    public static final String HTTP_SECURITY_HANDLERS = "handlers";
    public static final String JETTY_PROPERTY_HANDLER_ROLE = "user";
    public static final String JETTY_PROPERTY_HANDLER_USERNAME = "username";
    public static final String JETTY_PROPERTY_HANDLER_PASSWORD = "password";

    public String generateUri(Endpoint endpoint,String dataPipelineId) {

        StringBuilder uri = new StringBuilder();
        Map<String, String> configMap = new HashMap<>();

        if (endpoint instanceof SourceEndpoint) {
            uri.append(Constants.JETTY_PROTOCOL);
            configMap.putAll(httpEndpointProperties.getConsumer());
        } else {
            configMap.putAll(httpEndpointProperties.getProducer());
        }
        List<EndpointConfiguration> configurations = endpoint.getConfigurations();
        for (EndpointConfiguration configuration : configurations) {
            configMap.put(configuration.getKey(), configuration.getValue());
        }

        if(endpoint instanceof SourceEndpoint){
            registerHandler(dataPipelineId, configMap);
            uri.append(Constants.HTTPS_PROTOCOL);
       }else {
           uri.append(Constants.HTTP_PROTOCOL);
       }
        uri.append(configMap.get(Constants.ENDPOINT_PROPERTY_HOSTNAME)).append(":")
                .append(configMap.get(Constants.ENDPOINT_PROPERTY_PORT))
                .append(configMap.get(Constants.HTTP_PROPERTY_RESOURCE_URI)).append("?");

        if(endpoint instanceof  DestinationEndpoint  && configMap.get(Constants.HTTP_SECURE_PROTOCOL)!= null 
                && configMap.get(Constants.HTTP_SECURE_PROTOCOL).equalsIgnoreCase(Boolean.TRUE.toString())){
            configMap.put(Constants.ENDPOINT_PROPERTY_PROXY_AUTH_HOST, configMap.get(Constants.ENDPOINT_PROPERTY_HOSTNAME));
            configMap.put(Constants.ENDPOINT_PROPERTY_PROXY_AUTH_PORT, configMap.get(Constants.ENDPOINT_PROPERTY_PORT));
            configMap.put(Constants.ENDPOINT_PROPERTY_PROXY_AUTH_SCHEME,Constants.ENDPOINT_SCHEME);
        }

        configMap.remove(Constants.ENDPOINT_PROPERTY_HOSTNAME);
        configMap.remove(Constants.ENDPOINT_PROPERTY_PORT);
        configMap.remove(Constants.HTTP_PROPERTY_RESOURCE_URI);
        configMap.remove(Constants.HTTP_SECURE_PROTOCOL);
        String baseUri = uri.toString();
        uri.append(endpointConfigMapper.mapToUriQueryString(configMap));

        configMap.remove(Constants.HTTP_PROPERTY_AUTHPASSWORD);
        if (log.isInfoEnabled()) {
            log.info("Generated URI : {}", baseUri.concat(endpointConfigMapper.mapToUriQueryString(configMap)));   
        }

        return uri.toString();
    }

    private SecurityHandler createSecurityHandler(String id, String username, String password) {
        String realm =  endpointUtil.getRealmName(id);
        Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, JETTY_PROPERTY_HANDLER_ROLE);
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setPathSpec("/*");
        constraintMapping.setConstraint(constraint);

        HashLoginService loginService = new HashLoginService(realm);
        UserStore userStore = new UserStore();
        userStore.addUser(username, new Password(password), new String[]{JETTY_PROPERTY_HANDLER_ROLE});
        loginService.setUserStore(userStore);

        ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
        constraintSecurityHandler.setAuthenticator(new BasicAuthenticator());
        constraintSecurityHandler.addConstraintMapping(constraintMapping);
        constraintSecurityHandler.setLoginService(loginService);
        return constraintSecurityHandler;
    }

    private void registerHandler(String id,Map<String, String> configMap){
        String handlerName =  endpointUtil.getHandlerName(id);
        String userName = configMap.remove(JETTY_PROPERTY_HANDLER_USERNAME);
        String password =  configMap.remove(JETTY_PROPERTY_HANDLER_PASSWORD);
        configMap.put(HTTP_SECURITY_HANDLERS, handlerName);

        GenericApplicationContext genericApplicationContext =  (GenericApplicationContext) applicationContext;

        genericApplicationContext.registerBean(handlerName, SecurityHandler.class,
                () -> createSecurityHandler(id, userName, password));
    }
}
