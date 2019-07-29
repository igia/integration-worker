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
package io.igia.integration.worker.route.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.igia.integration.worker.config.ApplicationProperties;
import io.igia.integration.worker.config.Constants;

@Component
public class RouteUtil {

    @Autowired
    private ApplicationProperties applicationProperties;

    public String getAuditFileEndpoint(String directoryPath) {
        return Constants.FILE_PROTOCOL + applicationProperties.getAudit().get("directory-path")
                + "?fileExist=append&fileName=" + directoryPath + "_${date:now:"
                + applicationProperties.getAudit().get("file-suffix") + "}.json";
    }
}
