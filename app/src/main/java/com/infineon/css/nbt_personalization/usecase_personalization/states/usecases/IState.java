// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.states.usecases;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.ndef.exceptions.NdefException;
import com.infineon.hsw.utils.UtilException;

import java.io.IOException;
import java.security.cert.CertificateException;

/**
 * Interface to multiple possible states/configurations of NBT samples
 */
public interface IState {

    /**
     * Execute will set necessary configuration values and trigger a cascade of commands to
     * personalize a NBT sample to a certain specific state
     *
     * @param apduChannel APDU specific channel
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               Signals that an I/O exception of some sort has occurred
     * @throws NdefException             An exception in the NDEF file specific library occurred
     * @throws CertificateException      Thrown if parsing of certificate fails
     */
    void execute(@NonNull ApduChannel apduChannel) throws UtilException, ApduException, CertificateException, IOException, FileAccessPolicyException, NdefException;
}
