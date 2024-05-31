// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.utils;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static java.util.Base64.getDecoder;

/**
 * Holds some generic helpful methods
 */
public class Utils {

    /**
     * Converts an string of hexadecimal values to an array of bytes.
     *
     * @param string The string of hexadecimal values to process
     * @return An array of bytes representing the hexadecimal value
     */
    public static byte[] hexStringToByteArray(String string) {
        int len = string.length();

        if (len % 2 != 0) {
            // incorrect string length. ignore last char
            len--;
        }

        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) +
                    Character.digit(string.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Parses a ec key from a .pem file to a byte array
     *
     * @param ec_key_string The string from the .pem file
     * @return Byte array (ec key)
     */
    public static byte[] parseKeyFromFile(String ec_key_string) {

        try {
            String ec_key = ec_key_string
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replaceAll("\r", "")
                    .replace("-----END PRIVATE KEY-----", "");

            byte[] decoded = getDecoder().decode(ec_key);
            KeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory factory = KeyFactory.getInstance("EC");
            ECPrivateKey key = (ECPrivateKey) factory.generatePrivate(keySpec);
            return key.getS().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses a certificate from a file to a byte array
     *
     * @param cert_string The string from the file
     * @return Byte array (certificate)
     */
    public static byte[] parseCertFromFile(String cert_string) throws CertificateException {

        String cert = cert_string
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replaceAll("\r", "")
                .replace("-----END CERTIFICATE-----", "");

        byte[] decoded = Base64.getDecoder().decode(cert);
        ByteArrayInputStream is = new ByteArrayInputStream(decoded);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(is);

        return certificate.getEncoded();
    }
}
