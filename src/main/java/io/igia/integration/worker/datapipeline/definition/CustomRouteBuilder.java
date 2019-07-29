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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.FilterDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import io.igia.integration.worker.config.Constants;
import io.igia.integration.worker.datapipeline.dto.AuditMessageEventType;
import io.igia.integration.worker.datapipeline.dto.DataPipelineScript;
import io.igia.integration.worker.datapipeline.dto.MessageDataType;

public abstract class CustomRouteBuilder extends RouteBuilder {

    public ProcessorDefinition<?> unmarshalInDataType(ProcessorDefinition<?> processorDefinition, MessageDataType messageDataType) {
        if (messageDataType != null) {
            switch(messageDataType) {
            case HL7_V2:
                processorDefinition = processorDefinition.split().tokenize(Constants.HL7_MESSAGE_HEADER).streaming();
                processorDefinition = processorDefinition.transform(body().prepend(Constants.HL7_MESSAGE_HEADER));
                processorDefinition = processorDefinition.unmarshal().hl7();
                break;
            case CSV:
                processorDefinition = processorDefinition.unmarshal().csvLazyLoad().split(body()).streaming();
                break;
            case JSON:
                processorDefinition = processorDefinition.unmarshal().json(JsonLibrary.Jackson);
                break;
            case RAW:
                break;
            }
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> unmarshalOutDataType(ProcessorDefinition<?> processorDefinition, MessageDataType messageDataType) {
        if (messageDataType != null) {
            switch(messageDataType) {
            case HL7_V2:
                processorDefinition = processorDefinition.unmarshal().hl7();
                break;
            case CSV:
                processorDefinition = processorDefinition.unmarshal().csvLazyLoad();
                break;
            case JSON:
                processorDefinition = processorDefinition.unmarshal().json(JsonLibrary.Jackson);
                break;
            case RAW:
                break;
            }
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> marshalMessageDataType(ProcessorDefinition<?> processorDefinition, MessageDataType messageDataType) {
        if (messageDataType != null) {
            switch(messageDataType) {
            case HL7_V2:
                processorDefinition = processorDefinition.marshal().hl7();
                break;
            case CSV:
                processorDefinition = processorDefinition.process(exchange -> {
                    List<List<?>> csvData = new ArrayList<>();
                    csvData.add((List<?>)exchange.getIn().getBody());
                    exchange.getIn().setBody(csvData);
                }).marshal().csvLazyLoad();
                break;
            case JSON:
                processorDefinition = processorDefinition.marshal().json(JsonLibrary.Jackson);
                break;
            case RAW:
                break;
            }
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> wiretapAllMessages(ProcessorDefinition<?> processorDefinition, List<AuditMessageEventType> auditMessageEventTypes) {
        if (auditMessageEventTypes.contains(AuditMessageEventType.ALL)) {
            processorDefinition.setProperty(Constants.PROPERTY_MESSAGE_TYPE, simple(AuditMessageEventType.ALL.name()));
            processorDefinition = processorDefinition.wireTap(Constants.CAMEL_ENDPOINT_ALL_MESSAGES);
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> routeErrorMessages(ProcessorDefinition<?> processorDefinition, List<AuditMessageEventType> auditMessageEventTypes) {
        if (auditMessageEventTypes.contains(AuditMessageEventType.ERROR)) {
            processorDefinition = processorDefinition.onException(Exception.class).handled(true)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            exchange.setProperty(Constants.PROPERTY_MESSAGE_TYPE, AuditMessageEventType.ERROR.name());
                            final Throwable e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                            exchange.setProperty(Constants.PROPERTY_ERROR_MESSAGE, e.getMessage());
                        }
                    }).to(Constants.CAMEL_ENDPOINT_ERROR_MESSAGES).end();
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> wiretapFilteredMessages(ProcessorDefinition<?> processorDefinition, List<AuditMessageEventType> auditMessageEventTypes, MessageDataType messageDataType) {
        if (auditMessageEventTypes.contains(AuditMessageEventType.FILTERED)) {
            processorDefinition.setProperty(Constants.PROPERTY_MESSAGE_TYPE, simple(AuditMessageEventType.FILTERED.name()));
            processorDefinition = processorDefinition.wireTap(Constants.CAMEL_ENDPOINT_FILTERED_MESSAGES);
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> addFilters(ProcessorDefinition<?> processorDefinition, List<DataPipelineScript> dataPipelineScripts, String groupName, String endpoint) {

        String endpointName = endpoint.concat(groupName);
        if (dataPipelineScripts != null && !dataPipelineScripts.isEmpty()) {
            FilterDefinition filterDefinition = null;
            dataPipelineScripts = dataPipelineScripts.stream()
                    .sorted(Comparator.comparingInt(DataPipelineScript::getOrder))
                    .collect(Collectors.toList());
            for (DataPipelineScript dataPipelineScript : dataPipelineScripts) {
                if (filterDefinition == null) {
                    filterDefinition = processorDefinition.filter().javaScript(dataPipelineScript.getData());
                } else {
                    filterDefinition = filterDefinition.filter().javaScript(dataPipelineScript.getData());
                }
            }
            if (filterDefinition != null) {
                processorDefinition = filterDefinition.to(endpointName);
                for (int i = 0; i < dataPipelineScripts.size(); i++) {
                    processorDefinition = processorDefinition.end();
                }
            }
        } else {
            processorDefinition = processorDefinition.to(endpointName);
        }
        return processorDefinition;
    }

    public ProcessorDefinition<?> addTransformers(ProcessorDefinition<?> processorDefinition, List<DataPipelineScript> dataPipelineScripts) {
        if (dataPipelineScripts != null && !dataPipelineScripts.isEmpty()) {
            dataPipelineScripts.stream()
                .sorted(Comparator.comparing(DataPipelineScript::getOrder))
                .collect(Collectors.toList())
                .forEach(transformerDTO -> processorDefinition.transform().javaScript(transformerDTO.getData()));
        }
        return processorDefinition;
    }
}
