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
package io.igia.integration.worker.mllp;

import org.apache.camel.component.mllp.MllpComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.igia.integration.worker.config.MllpComponentProperties;

@Configuration
public class MllpConfiguration {

    @Bean("igia-mllp")
    public MllpComponent mllpComponent(MllpComponentProperties mllpComponentProperties) {
        MllpComponent mllpComponent = new MllpComponent();
        MllpComponent.setLogPhi(Boolean.parseBoolean(mllpComponentProperties.getLogPhi()));
        return mllpComponent;
    }
}
