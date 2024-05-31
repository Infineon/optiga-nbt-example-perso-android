// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_CC;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FAP;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE1;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE2;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE3;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE4;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_NDEF;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSet;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicy;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.utils.UtilException;

import java.io.IOException;

/**
 * The SetFileAccessPolicy class generates and sends the commands to configure all single
 * FileAccessPolicy Files of a NBT sample, it only needs to be provided with a ApduChannel and
 * the FileAccessPolicy that needs to be changed. Missing FileAccessPolicies will be configured
 * with the default setting.
 */
public class SetFileAccessPolicy implements ICommandFlow {

    /**
     * Initializing CC file specific file access policy
     */
    private FileAccessPolicy FAP_CC = DEFAULT_FAP_CC;

    /**
     * Initializing NDEF file specific file access policy
     */
    private FileAccessPolicy FAP_NDEF = DEFAULT_FAP_NDEF;

    /**
     * Initializing FAP file specific file access policy
     */
    private FileAccessPolicy FAP_FAP = DEFAULT_FAP_FAP;

    /**
     * Initializing proprietary file1 specific file access policy
     */
    private FileAccessPolicy FAP_FILE1 = DEFAULT_FAP_FILE1;

    /**
     * Initializing proprietary file2 specific file access policy
     */
    private FileAccessPolicy FAP_FILE2 = DEFAULT_FAP_FILE2;

    /**
     * Initializing proprietary file3 specific file access policy
     */
    private FileAccessPolicy FAP_FILE3 = DEFAULT_FAP_FILE3;

    /**
     * Initializing proprietary file4 specific file access policy
     */
    private FileAccessPolicy FAP_FILE4 = DEFAULT_FAP_FILE4;

    /**
     * Setter function for all file access policies, all not-set FAPs will be set to default
     *
     * @param fap_cc    File Access Policy for CC file, will be set to default if null
     * @param fap_ndef  File Access Policy for NDEF file, will be set to default if null
     * @param fap_fap   File Access Policy for FAP file, will be set to default if null
     * @param fap_file1 File Access Policy for proprietary file1, will be set to default if null
     * @param fap_file2 File Access Policy for proprietary file2, will be set to default if null
     * @param fap_file3 File Access Policy for proprietary file3, will be set to default if null
     */
    public void setFileAccessPolicy(FileAccessPolicy fap_cc, FileAccessPolicy fap_ndef, FileAccessPolicy fap_fap, FileAccessPolicy fap_file1, FileAccessPolicy fap_file2, FileAccessPolicy fap_file3, FileAccessPolicy fap_file4) {

        if (fap_cc != null) FAP_CC = fap_cc;

        if (fap_ndef != null) FAP_NDEF = fap_ndef;

        if (fap_fap != null) FAP_FAP = fap_fap;

        if (fap_file1 != null) FAP_FILE1 = fap_file1;

        if (fap_file2 != null) FAP_FILE2 = fap_file2;

        if (fap_file3 != null) FAP_FILE3 = fap_file3;

        if (fap_file4 != null) FAP_FILE4 = fap_file4;
    }

    /**
     * Writes all File Access Policies accordingly.
     * Since it is essential that the FAPs access conditions are equal to the access conditions
     * configured in the CC file, updating of the CC file is also triggered in this method.
     *
     * @param apduChannel APDU channel to the NFC Interface
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               I/O exception has occurred, probably by a ByteArrayOutputStream
     */
    public void execute(@NonNull ApduChannel apduChannel) throws ApduException, UtilException, FileAccessPolicyException, IOException {
        NbtCommandSet commandSet = new NbtCommandSet(apduChannel, 0);
        NbtApduResponse apduResponse = commandSet.selectApplication();
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_CC);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_NDEF);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_FAP);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_FILE1);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_FILE2);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_FILE3);
        apduResponse.checkOK();

        apduResponse = commandSet.updateFap(FAP_FILE4);
        apduResponse.checkOK();

        //Updating the CC file according to the FAP
        WriteCcFile writeCcFile = new WriteCcFile();
        writeCcFile.buildCcFile(FAP_FILE1, FAP_FILE2, FAP_FILE3, FAP_FILE4);
        writeCcFile.execute(apduChannel);
    }
}