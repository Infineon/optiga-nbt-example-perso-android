// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.ALLOW;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.BLOCK;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.CC_OFFSET;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_CC_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP1_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP2_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP3_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP4_FILE;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSet;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicy;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.utils.UtilException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The WriteCcFile class transforms the file access policies to the a format fitting for the CC file
 * , builds the CC file with the latest settings and sends the commands to write into the CC file
 * of the NBT sample, it only needs to be provided with a ApduChannel and the file access policies.
 * This is needed since it needs to be ensured that the access conditions in the CC file fit to
 * the access conditions in the FileAccessPolicy of the sample.
 */
public class WriteCcFile implements ICommandFlow {

    /**
     * Holds the full CC file byte array
     */
    private byte[] ccByteArray;

    /**
     * Default CC specific file access policy
     */
    private final FileAccessPolicy FAP_CC_DEFAULT = new FileAccessPolicy(NBT_ID_CC_FILE, ALLOW, BLOCK, ALLOW, BLOCK);
    /**
     * Temporary CC specific file access policy to allow updating the file via NFC
     */
    private final FileAccessPolicy FAP_CC_WRITE = new FileAccessPolicy(NBT_ID_CC_FILE, ALLOW, BLOCK, ALLOW, ALLOW);

    /**
     * Translates the file access policies to the CC file format and builds full byte array to
     * update the CC file
     *
     * @param fap_file1 File Access Policy for proprietary file1
     * @param fap_file2 File Access Policy for proprietary file2
     * @param fap_file3 File Access Policy for proprietary file3
     * @param fap_file4 File Access Policy for proprietary file4
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               I/O exception has occurred, probably by a ByteArrayOutputStream
     */
    public void buildCcFile(FileAccessPolicy fap_file1, FileAccessPolicy fap_file2, FileAccessPolicy fap_file3, FileAccessPolicy fap_file4) throws FileAccessPolicyException, IOException {

        byte[] PP_FILE_HEADER = new byte[]{(byte) 0x05, (byte) 0x06, (byte) 0xE1};
        byte[] PP_FILE_SIZE = new byte[]{(byte) 0x04, (byte) 0x00};

        ByteArrayOutputStream ccFileOutputStream = new ByteArrayOutputStream();
        //Transfer the access condition to a CC file valid format (if fap is 0x40 (=allow) it is set to 0x00 (=allow in cc file)
        byte[] f1_fap = new byte[]{(byte) ((fap_file1.getAccessBytes()[2] == 0x00) ? 0xFF : 0x00), (byte) ((fap_file1.getAccessBytes()[3] == 0x00) ? 0xFF : 0x00)};

        byte[] f2_fap = new byte[]{(byte) ((fap_file2.getAccessBytes()[2] == 0x00) ? 0xFF : 0x00), (byte) ((fap_file2.getAccessBytes()[3] == 0x00) ? 0xFF : 0x00)};

        byte[] f3_fap = new byte[]{(byte) ((fap_file3.getAccessBytes()[2] == 0x00) ? 0xFF : 0x00), (byte) ((fap_file3.getAccessBytes()[3] == 0x00) ? 0xFF : 0x00)};

        byte[] f4_fap = new byte[]{(byte) ((fap_file4.getAccessBytes()[2] == 0x00) ? 0xFF : 0x00), (byte) ((fap_file4.getAccessBytes()[3] == 0x00) ? 0xFF : 0x00)};

        //Combine to one byte array
        ccFileOutputStream.write(PP_FILE_HEADER);
        ccFileOutputStream.write((byte) NBT_ID_PP1_FILE);
        ccFileOutputStream.write(PP_FILE_SIZE);
        ccFileOutputStream.write(f1_fap);
        ccFileOutputStream.write(PP_FILE_HEADER);
        ccFileOutputStream.write((byte) NBT_ID_PP2_FILE);
        ccFileOutputStream.write(PP_FILE_SIZE);
        ccFileOutputStream.write(f2_fap);
        ccFileOutputStream.write(PP_FILE_HEADER);
        ccFileOutputStream.write((byte) NBT_ID_PP3_FILE);
        ccFileOutputStream.write(PP_FILE_SIZE);
        ccFileOutputStream.write(f3_fap);
        ccFileOutputStream.write(PP_FILE_HEADER);
        ccFileOutputStream.write((byte) NBT_ID_PP4_FILE);
        ccFileOutputStream.write(PP_FILE_SIZE);
        ccFileOutputStream.write(f4_fap);

        this.ccByteArray = ccFileOutputStream.toByteArray();
    }

    /**
     * Writes the file access policies for the proprietary files into the CC file. The CC
     * file should be updated every time the FAP file is changed
     *
     * @param apduChannel APDU channel to the NFC Interface
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     */
    public void execute(@NonNull ApduChannel apduChannel) throws ApduException, UtilException, FileAccessPolicyException {
        NbtCommandSet commandSet = new NbtCommandSet(apduChannel, 0);
        NbtApduResponse apduResponse = commandSet.selectApplication();
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_CC_WRITE);
        apduResponse.checkOK();

        apduResponse = commandSet.selectFile(NBT_ID_CC_FILE);
        apduResponse.checkOK();

        apduResponse = commandSet.updateBinary(CC_OFFSET, this.ccByteArray);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_CC_DEFAULT);
        apduResponse.checkOK();
    }
}