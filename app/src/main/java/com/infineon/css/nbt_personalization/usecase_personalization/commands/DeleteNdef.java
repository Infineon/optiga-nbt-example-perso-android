// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_OFFSET;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_NDEF_FILE;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSet;
import com.infineon.hsw.utils.UtilException;

/**
 * The DeleteNdef class generates and sends the command to delete the complete
 * NDEF File on the presented NBT sample, it only needs to be provided with a ApduChannel
 */
public class DeleteNdef implements ICommandFlow {

    /**
     * The empty ndef file to be written, hardcoded size
     */
    private final byte[] empty_ndef = new byte[850];

    /**
     * Ndef file length info for empty ndef file
     */
    private final byte[] EMPTY_NDEF_LENGTH = new byte[]{(byte) 0x00, (byte) 0x00};

    /**
     * Deletes the entire ndef file by filling it with empty bytes
     *
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    public void execute(@NonNull ApduChannel apduChannel) throws ApduException, UtilException {
        NbtCommandSet commandSet = new NbtCommandSet(apduChannel, 0);
        NbtApduResponse apduResponse = commandSet.selectApplication();
        apduResponse.checkOK();

        apduResponse = commandSet.updateNdefMessage(empty_ndef);
        apduResponse.checkOK();

        apduResponse = commandSet.selectFile(NBT_ID_NDEF_FILE);
        apduResponse.checkOK();

        //Additionally setting the data length of the empty ndef file to zero
        apduResponse = commandSet.updateBinary(DEFAULT_OFFSET, EMPTY_NDEF_LENGTH);
        apduResponse.checkOK();
    }
}
