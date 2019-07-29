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
package io.igia.integration.worker.datapipeline.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EndpointConfigMapper {

    public String mapToUriQueryString(Map<String, String> map) {
        StringBuilder uri = new StringBuilder();
        map.forEach((key, value) -> uri.append("&").append(key).append("=").append(value));
        return uri.substring(1);
    }
}
