// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_BSK;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSet;
import com.infineon.hsw.apdu.nbt.NbtCommandSetPerso;
import com.infineon.hsw.utils.UtilException;

/**
 * The WriteEcKey class generates and sends the commands to write a EC key to a NBT sample.
 * It needs to be provided with ApduChannel and the ec key itself as byte Array.
 */
public class WriteEcKey implements ICommandFlow {

    /**
     * Member to hold the EC key
     */
    private byte[] ec_key;

    /**
     * Selects the application file and writes a sample EC key onto the NBT sample, it can only be called
     * successfully after the EC key was set using setEcKey()
     *
     * @param apduChannel APDU channel to the NFC Interface
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    public void execute(@NonNull ApduChannel apduChannel) throws UtilException, ApduException {
        NbtCommandSet commandSet = new NbtCommandSet(apduChannel, 0);
        NbtApduResponse apduResponse = commandSet.selectApplication();
        apduResponse.checkOK();

        NbtCommandSetPerso persoCommandSet = new NbtCommandSetPerso(apduChannel, 0);
        apduResponse = persoCommandSet.personalizeData(NBT_ID_BSK, ec_key);
        apduResponse.checkOK();
    }

    /**
     * Setter function to set sample EC key, needs to be set before execute() is called
     */
    public void setEcKey(@NonNull byte[] key) {
        ec_key = key;
    }
}

