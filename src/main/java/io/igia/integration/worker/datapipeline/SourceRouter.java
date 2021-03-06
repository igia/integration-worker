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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.datapipeline.dto.SourceEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.FileEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.HttpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.MllpEndpoint;
import io.igia.integration.worker.datapipeline.endpoint.SftpEndpoint;

@Component
public class SourceRouter {

    @Autowired
    private MllpEndpoint mllpEndpoint;

    @Autowired
    private FileEndpoint fileEndpoint;

    @Autowired
    private SftpEndpoint sftpEndpoint;

    @Autowired
    private HttpEndpoint httpEndpoint;

    public String redirect(SourceEndpoint sourceEndpoint, String dataPipelineId) {
        switch (sourceEndpoint.getType()) {
        case MLLP:
            return mllpEndpoint.generateUri(sourceEndpoint);
        case FILE:
            return fileEndpoint.generateUri(sourceEndpoint);
        case SFTP:
            return sftpEndpoint.generateUri(sourceEndpoint);
        case HTTP:
            return httpEndpoint.generateUri(sourceEndpoint,dataPipelineId);
        default:
            return null;
        }
    }
}
