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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import io.igia.integration.worker.datapipeline.mapper.EndpointConfigMapper;

public class EndpointConfigMapperTest {

    @InjectMocks
    private EndpointConfigMapper endpointConfigMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMapToUriQueryString() {
        Map<String, String> map = new HashMap<>();
        map.put("query1", "value1");
        map.put("query2", "value2");
        map.put("query3", "value3");

        String uri = endpointConfigMapper.mapToUriQueryString(map);
        assertNotNull(uri);
        assertFalse(uri.isEmpty());
        assertEquals("query1=value1&query2=value2&query3=value3", uri);
    }
}
