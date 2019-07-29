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
package io.igia.integration.worker.datapipeline.endpoint.util;

import org.springframework.stereotype.Component;

@Component
public class EndpointUtil {

    public static final String HTTP_HANDLER_SUFFIX = "_handler";
    public static final String HTTP_REALM_SUFFIX  = "_realm";

    public String getHandlerName(String id){
        return id+HTTP_HANDLER_SUFFIX;
    }

    public String getRealmName(String id){
        return id+HTTP_REALM_SUFFIX;
    }

}
