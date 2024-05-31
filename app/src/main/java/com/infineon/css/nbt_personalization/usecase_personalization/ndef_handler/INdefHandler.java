// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.ndef_handler;

import androidx.annotation.NonNull;

import com.infineon.hsw.ndef.exceptions.NdefException;

import java.io.IOException;

/**
 * Handler for Ndef file format
 */
public interface INdefHandler {
    /**
     * Parses the ndef message for the brand protection use case, containing the COTT url and the
     * certificate for the sample
     *
     * @param uri  COTT url
     * @param cert Certificate
     * @return The ndef message as byte array
     * @throws IOException   Signals that an I/O exception of some sort has occurred
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    byte[] createBrandprotectionNdefMessage(@NonNull String uri, @NonNull byte[] cert) throws IOException, NdefException;

    /**
     * Parses the ndef message for the connection handover use case, containing the bluetooth mac
     * address (only supported by Infineon Lib)
     *
     * @param deviceMac MAC address of bluetooth device
     * @return The ndef message as byte array
     * @throws IOException   Signals that an I/O exception of some sort has occurred
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    byte[] createConnectionHandoverNdefMessage(@NonNull byte[] deviceMac) throws IOException, NdefException;

}
