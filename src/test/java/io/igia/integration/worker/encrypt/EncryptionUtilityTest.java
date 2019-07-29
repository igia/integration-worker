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
package io.igia.integration.worker.encrypt;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

import io.igia.integration.worker.config.ApplicationProperties;
import io.igia.integration.worker.datapipeline.dto.EndpointConfiguration;
import io.igia.integration.worker.encrypt.EncryptionUtility;

@Component
public class EncryptionUtilityTest {

    @InjectMocks
    private EncryptionUtility encryptionUtility;

    @Mock
    private ApplicationProperties applicationProperties;

    private final String SECRET_KEY = "secret_key";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEncryptString() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToEncrypt = "Integration-Worker";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(strToEncrypt);
        assertEquals("jBRH9XCM4YVbx8SlgGHl+XV1d8Iyr8bePWqaKJbKPcM=", encryptedString);
    }

    @Test
    public void testDecryptString() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "jBRH9XCM4YVbx8SlgGHl+XV1d8Iyr8bePWqaKJbKPcM=";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("Integration-Worker", decryptedString);
    }

    @Test
    public void testEncryptJsonObject() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, JsonProcessingException {
        JsonObject json = new JsonObject();
        json.addProperty("name", "james");
        json.addProperty("surname", "williams");
        Object objToEncrypt = json;
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(objToEncrypt.toString());
        assertEquals("Ob9WNGYFIUbZPATV/Tiqt2LHZUX3rRoQ51s7pkIpMslxHxrROI/4/C8MER1OIXr+", encryptedString);
    }

    @Test
    public void testDecryptJsonObject() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "Ob9WNGYFIUbZPATV/Tiqt2LHZUX3rRoQ51s7pkIpMslxHxrROI/4/C8MER1OIXr+";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("{\"name\":\"james\",\"surname\":\"williams\"}", decryptedString);
    }

    @Test
    public void testEncryptMap() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "james");
        map.put("surname", "williams");
        Object objToEncrypt = map;
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(objToEncrypt.toString());
        assertEquals("wcGWJJx/m6fNPHSmOm9/AcoRGFWO8ANP4FnH1fO2m/c=", encryptedString);
    }

    @Test
    public void testDecryptMap() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "wcGWJJx/m6fNPHSmOm9/AcoRGFWO8ANP4FnH1fO2m/c=";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("{surname=williams, name=james}", decryptedString);
    }

    @Test
    public void testEncryptMapWithJsonObject()
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, JsonProcessingException {
        JsonObject json = new JsonObject();
        json.addProperty("name", "james");
        Map<String, JsonObject> map = new HashMap<>();
        map.put("json", json);
        Object objToEncrypt = map;
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(objToEncrypt.toString());
        assertEquals("3YOrGHbYSjNcRUyVdiNi9BeDd1fTjonl/iERNnEOazo=", encryptedString);
    }

    @Test
    public void testDecryptMapWithJsonObject() throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "3YOrGHbYSjNcRUyVdiNi9BeDd1fTjonl/iERNnEOazo=";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("{json={\"name\":\"james\"}}", decryptedString);
    }

    @Test
    public void testEncryptList() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, JsonProcessingException {
        List<String> list = new ArrayList<>();
        list.add("source");
        list.add("destination");
        Object objToEncrypt = list;
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(objToEncrypt.toString());
        assertEquals("T4Vc9blbht1ibgnKskJO98Mitlp74ZhraTE3Z4Yhmao=", encryptedString);
    }

    @Test
    public void testDecryptList() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "T4Vc9blbht1ibgnKskJO98Mitlp74ZhraTE3Z4Yhmao=";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("[source, destination]", decryptedString);
    }

    @Test
    public void testEncryptListWithDTO() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, JsonProcessingException {
        List<EndpointConfiguration> list = new ArrayList<>();
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.setKey("hostname");
        endpointConfiguration.setValue("localhost");
        list.add(endpointConfiguration);
        Object objToEncrypt = list;
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String encryptedString = encryptionUtility.encrypt(objToEncrypt.toString());
        assertEquals("mM+jQ18iU6oJnde6gRAK8YuTHoJjHoZULI9TJ8cEAyV33x6QAWSvk9b+lZyDumcQ", encryptedString);
    }

    @Test
    public void testDecryptListWithDTO() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String strToDecrypt = "mM+jQ18iU6oJnde6gRAK8YuTHoJjHoZULI9TJ8cEAyV33x6QAWSvk9b+lZyDumcQ";
        Mockito.when(encryptionUtility.getKey()).thenReturn(SECRET_KEY);
        String decryptedString = encryptionUtility.decrypt(strToDecrypt);
        assertEquals("[EndpointConfigurations{key='hostname'}]", decryptedString);
    }
}
