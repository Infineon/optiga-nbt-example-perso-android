// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSet;
import com.infineon.hsw.utils.UtilException;

/**
 * The ReadFileAccessPolicy class generates and sends the command to read out the complete
 * FileAccessPolicy of a NBT sample, it only needs to be provided with a ApduChannel
 * Note: execute() needs to be called before getFap() can provide any valid data
 */
public class ReadFileAccessPolicy implements ICommandFlow {

    /**
     * Full File Access Policy of device
     */
    private byte[] fap = null;

    /**
     * Reads out the full File Access Policy
     *
     * @param apduChannel APDU channel to the NFC Interface
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    public void execute(@NonNull ApduChannel apduChannel) throws ApduException, UtilException {
        NbtCommandSet commandSet = new NbtCommandSet(apduChannel, 0);
        NbtApduResponse apduResponse = commandSet.selectApplication();
        apduResponse.checkOK();

        apduResponse = commandSet.readFapBytes();
        fap = apduResponse.getData();
    }

    /**
     * Getter function for the File Access Policy read out. It can only provide valid data after
     * execute() was called
     */
    public byte[] getFap() {
        return fap;
    }
}
