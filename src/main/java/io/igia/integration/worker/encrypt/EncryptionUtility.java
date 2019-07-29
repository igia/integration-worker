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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.camel.Exchange;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.igia.integration.worker.config.ApplicationProperties;
import io.igia.integration.worker.datapipeline.dto.AuditMessage;
import io.igia.integration.worker.datapipeline.dto.AuditMessageExchange;
import io.igia.integration.worker.datapipeline.dto.AuditMessageInfo;

@Component
public class EncryptionUtility {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ALGORITHM = "AES";

    String getKey() {
        return applicationProperties.getSecretKey();
    }

    private SecretKeySpec getSecretKeySpec() {
        byte[] key = getKey().getBytes(StandardCharsets.UTF_8);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, ALGORITHM);
    }

    public CryptoDataFormat getCryptoDataFormat() {
        return new CryptoDataFormat(ALGORITHM, getSecretKeySpec());
    }

    public String encrypt(String strToEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = getSecretKeySpec();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String strToDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = getSecretKeySpec();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

    public void decrypt(Exchange exchange) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {
        AuditMessage auditMessage = objectMapper.readValue(exchange.getIn().getBody(String.class),
                new TypeReference<AuditMessage>() { });
        AuditMessageExchange auditMessageExchange = auditMessage.getExchange();
        for (AuditMessageInfo header : auditMessageExchange.getHeaders()) {
            if (header.getValue() != null && !header.getValue().toString().isEmpty()) {
                header.setValue(decrypt(header.getValue().toString()));
            }
        }
        for (AuditMessageInfo property : auditMessageExchange.getProperties()) {
            if (property.getValue() != null && !property.getValue().toString().isEmpty()) {
                property.setValue(decrypt(property.getValue().toString()));
            }
        }
        auditMessageExchange.setBody(decrypt(auditMessageExchange.getBody()));
        auditMessage.setExchange(auditMessageExchange);
        exchange.getIn().setBody(objectMapper.writeValueAsString(auditMessage));
    }
}
