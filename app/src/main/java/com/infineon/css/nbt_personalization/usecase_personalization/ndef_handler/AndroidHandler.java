// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.ndef_handler;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import androidx.annotation.NonNull;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;


/**
 * Handler using the default Android NDEF library to handle the standardized NDEF file format,
 * parse and validate the certificate
 */
@SuppressWarnings("unused")
public class AndroidHandler implements INdefHandler {

    /**
     * Parses the ndef message for the brand protection use case, containing the COTT url and the
     * certificate for the sample
     *
     * @param url  COTT url
     * @param cert Certificate
     * @return The ndef message as byte array
     */
    public byte[] createBrandprotectionNdefMessage(@NonNull String url, @NonNull byte[] cert) {

        NdefRecord uriRecord = createUriRecordFromString(url);
        NdefRecord extRecord = createExternalRecordFromString(cert);

        NdefMessage ndefMessage = new NdefMessage(uriRecord, extRecord);

        return ndefMessage.toByteArray();
    }

    /**
     * Parses a URL to Ndef Record for the brand protection use case, based on Android NDEF lib
     *
     * @param url COTT url
     * @return The ndef record containing the cott url
     */
    private NdefRecord createUriRecordFromString(@NonNull String url) {

        String cott_placeholder = "?cott=PLACEHOLDERPLACEHOLDERPLACEHOLDERPLACEHOLDER";
        String full_url = url + cott_placeholder;

        return NdefRecord.createUri(full_url);
    }

    /**
     * Parses Certificate String to Ndef Record for the brand protection use case, based on Android NDEF lib.
     *
     * @param cert Root certificate as string
     * @return The ndef record containing the certificate
     */
    private NdefRecord createExternalRecordFromString(byte[] cert) {

        String record_type_cert = "infineon.com:nfc-bridge-tag.x509";
        String domain = "Infineon Technologies";

        return NdefRecord.createExternal(domain, record_type_cert, cert);
    }

    /**
     * Creates and returns a hardcoded ndef message for the bluetooth connection handover
     * (OOB data) based on the given mac address of the bluetooth device.
     *
     * @param deviceMac MAC address of bluetooth device
     * @return The ndef message as byte array
     */
    public byte[] createConnectionHandoverNdefMessage(@NonNull byte[] deviceMac) {

        //Android does not provide a library to generate Bluetooth OOB, therefore hardcoded
        byte[] hardcodedNdefType = new byte[]{(byte) 0xD2, (byte) 0x20, (byte) 0x08, (byte) 0x61,
                (byte) 0x70, (byte) 0x70, (byte) 0x6C, (byte) 0x69, (byte) 0x63, (byte) 0x61, (byte) 0x74,
                (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x2F, (byte) 0x76, (byte) 0x6E, (byte) 0x64,
                (byte) 0x2E, (byte) 0x62, (byte) 0x6C, (byte) 0x75, (byte) 0x65, (byte) 0x74, (byte) 0x6F,
                (byte) 0x6F, (byte) 0x74, (byte) 0x68, (byte) 0x2E, (byte) 0x65, (byte) 0x70,
                (byte) 0x2E, (byte) 0x6F, (byte) 0x6F, (byte) 0x62, (byte) 0x08, (byte) 0x00};

        //Adding bluetooth device address to ndef message
        byte[] fullNdef = new byte[hardcodedNdefType.length + 6];

        System.arraycopy(hardcodedNdefType, 0, fullNdef, 0, hardcodedNdefType.length);
        System.arraycopy(deviceMac, 0, fullNdef, hardcodedNdefType.length, deviceMac.length);

        return fullNdef;
    }

    /**
     * Parses Certificate to Ndef Record for the brand protection use case, based on Android NDEF lib
     *
     * @param certificate Root certificate
     * @return The ndef record containing the certificate
     * @throws CertificateEncodingException If encoding of certificate fails
     */
    public NdefRecord createExternalRecordFromCert(@NonNull X509Certificate certificate) throws CertificateEncodingException {

        String record_type_cert = "infineon.com:nfc-bridge-tag.x509";
        String domain = "Infineon Technologies";

        byte[] certificateBytes = certificate.getEncoded();

        return NdefRecord.createExternal(domain, record_type_cert, certificateBytes);
    }
}
