// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.ndef_handler;

import androidx.annotation.NonNull;

import com.infineon.hsw.ndef.NdefManager;
import com.infineon.hsw.ndef.IfxNdefMessage;
import com.infineon.hsw.ndef.bp.BrandProtectionRecord;
import com.infineon.hsw.ndef.bp.certificate.CertificateException;
import com.infineon.hsw.ndef.exceptions.NdefException;
import com.infineon.hsw.ndef.records.model.DataTypes;
import com.infineon.hsw.ndef.records.rtd.BluetoothRecord;
import com.infineon.hsw.ndef.records.rtd.IfxNdefRecord;
import com.infineon.hsw.ndef.records.rtd.UriRecord;
import com.infineon.hsw.ndef.utils.NdefConstants;

import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Handler using the a Infineon Java NDEF library to handle the standardized NDEF file format,
 * parse and validate the certificate
 */
public class InfineonHandler implements INdefHandler {

    /**
     * Parses the ndef message for the brand protection use case, containing the COTT url and the
     * certificate for the sample
     *
     * @param url  COTT url
     * @param cert Certificate
     * @return The ndef message as byte array
     * @throws IOException   Signals that an I/O exception of some sort has occurred
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    public byte[] createBrandprotectionNdefMessage(@NonNull String url, @NonNull byte[] cert) throws IOException, NdefException {

        UriRecord uriRecord = createUriRecordFromString(url);
        IfxNdefRecord extRecord = createExternalRecordFromString(cert);
        IfxNdefMessage message = new IfxNdefMessage(uriRecord, extRecord);

        return NdefManager.getInstance().encode(message);
    }

    /**
     * Parses a URL to Ndef Record for the brand protection use case, based on Android NDEF lib
     *
     * @param url COTT url
     * @return The ndef record containing the cott url
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    private UriRecord createUriRecordFromString(@NonNull String url) throws NdefException {

        return new UriRecord(UriRecord.UriIdentifier.URI_HTTP, url);
    }

    /**
     * Parses Certificate String to Ndef Record for the brand protection use case, based on Android NDEF lib.
     *
     * @param cert Root certificate as string
     * @return The ndef record containing the certificate
     */
    private IfxNdefRecord createExternalRecordFromString(byte[] cert) {

        String record_type_cert = "infineon technologies:infineon.com:nfc-bridge-tag.x509";
        byte[] ID = new byte[]{(byte) 0x00};

        return new IfxNdefRecord(NdefConstants.TNF_EXTERNAL_TYPE, record_type_cert.getBytes(), ID, cert);
    }

    /**
     * Parses the ndef message for the connection handover use case, containing the bluetooth mac
     * address
     *
     * @param deviceMac MAC address of bluetooth device
     * @return The ndef message as byte array
     * @throws IOException   Signals that an I/O exception of some sort has occurred
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    public byte[] createConnectionHandoverNdefMessage(@NonNull byte[] deviceMac) throws IOException, NdefException {

        BluetoothRecord record = createConnectionHandoverRecord(deviceMac, "NBT");
        IfxNdefMessage message = new IfxNdefMessage(record);

        return NdefManager.getInstance().encode(message);
    }

    /**
     * Creates and returns a ndef record for the bluetooth connection handover (OOB data) based on the given
     * mac address of the bluetooth device. Additionally a general local name for the bluetooth device
     * is added to the record
     *
     * @param deviceMac  MAC address of bluetooth device
     * @param local_name Local name of the bluetooth device, will be added to record
     * @return Ndef record
     */
    @SuppressWarnings("SameParameterValue")
    private BluetoothRecord createConnectionHandoverRecord(@NonNull byte[] deviceMac, String local_name) {

        BluetoothRecord record = new BluetoothRecord(deviceMac);
        if (local_name != null) {
            record.setName(DataTypes.COMPLETE_LOCAL_NAME, local_name);
        }
        return record;
    }

    /**
     * Parses Certificate to Ndef Record for the brand protection use case, based on Android NDEF lib
     *
     * @param certificate Root certificate
     * @return The ndef record containing the certificate
     * @throws CertificateException If encoding of certificate fails
     */
    @SuppressWarnings("unused")
    public BrandProtectionRecord createExternalRecordFromCert(@NonNull X509Certificate certificate) throws CertificateException {

        BrandProtectionRecord brandProtectionRecord = new BrandProtectionRecord();
        brandProtectionRecord.setCertificate(certificate);

        return brandProtectionRecord;
    }

}
