// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.states.usecases;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.ALLOW;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.BLOCK;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_GPIO_PT_RF_IRQ;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_NDEF_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP1_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP2_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP3_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP4_FILE;

import androidx.annotation.NonNull;

import com.infineon.css.nbt_personalization.usecase_personalization.commands.WriteNdef;
import com.infineon.css.nbt_personalization.usecase_personalization.ndef_handler.INdefHandler;
import com.infineon.css.nbt_personalization.usecase_personalization.ndef_handler.InfineonHandler;
import com.infineon.css.nbt_personalization.usecase_personalization.states.StateConfig;
import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicy;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.ndef.exceptions.NdefException;
import com.infineon.hsw.utils.UtilException;

import java.io.IOException;

/**
 * The ConnectionHandoverState Class represents the NBT sample configuration for the Connection Handover
 * use case. It only needs to be provided with a ApduChannel to configure the NBT sample accordingly.
 */
public class ConnectionHandoverState implements IState {

    /**
     * Establishes an interface channel with protocol (APDU) specific functionality
     */
    private ApduChannel apduChannel;

    /**
     * MAC address of bluetooth device
     */
    private final byte[] bluetooth_device;

    /**
     * Initializing use case specific NDEF file file access policy
     */
    private final FileAccessPolicy FAP_NDEF = new FileAccessPolicy(NBT_ID_NDEF_FILE, ALLOW, ALLOW, ALLOW, BLOCK);

    /**
     * Initializing proprietary file1 specific file access policy
     */
    private final FileAccessPolicy FAP_FILE1 = new FileAccessPolicy(NBT_ID_PP1_FILE, BLOCK, BLOCK, BLOCK, BLOCK);

    /**
     * Initializing proprietary file2 specific file access policy
     */
    private final FileAccessPolicy FAP_FILE2 = new FileAccessPolicy(NBT_ID_PP2_FILE, BLOCK, BLOCK, BLOCK, BLOCK);

    /**
     * Initializing proprietary file3 specific file access policy
     */
    private final FileAccessPolicy FAP_FILE3 = new FileAccessPolicy(NBT_ID_PP3_FILE, BLOCK, BLOCK, BLOCK, BLOCK);

    /**
     * Initializing proprietary file4 specific file access policy
     */
    private final FileAccessPolicy FAP_FILE4 = new FileAccessPolicy(NBT_ID_PP4_FILE, BLOCK, BLOCK, BLOCK, BLOCK);

    /**
     * Constructor setting necessary values for members
     *
     * @param deviceMac MAC address of bluetooth device
     */
    public ConnectionHandoverState(byte[] deviceMac) {

        bluetooth_device = deviceMac;
    }

    /**
     * Executes necessary commands to personalize sample to connection handover state
     * - Write the File Access Policy accordingly
     * - Sets the interface configuration accordingly
     *
     * @param apduChannel APDU specific channel
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               I/O exception has occurred, probably by a ByteArrayOutputStream
     * @throws NdefException             An exception in the NDEF file specific library occurred
     */
    public void execute(@NonNull ApduChannel apduChannel) throws UtilException, ApduException, FileAccessPolicyException, IOException, NdefException {

        this.apduChannel = apduChannel;

        writeConnectionHandoverNdefMessage();

        StateConfig connectionHandoverStateConfig = new StateConfig.StateConfigBuilder(this.apduChannel)
                .setFapNdef(FAP_NDEF)
                .setFapFile1(FAP_FILE1)
                .setFapFile2(FAP_FILE2)
                .setFapFile3(FAP_FILE3)
                .setFapFile4(FAP_FILE4)
                .setI2cInterfaceConfig(true)
                .setNfcInterfaceConfig(true)
                .setGpioInterfaceConfig(NBT_GPIO_PT_RF_IRQ)
                .build();
        connectionHandoverStateConfig.setStateConfig();
    }


    /**
     * Creates and writes Bluetooth Connection Handover information to NDEF file
     *
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     * @throws IOException   I/O exception has occurred, probably by a ByteArrayOutputStream
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    private void writeConnectionHandoverNdefMessage() throws UtilException, ApduException, IOException, NdefException {

        // Choose between Android or Infineon specific NDEF library using the corresponding handler
        INdefHandler handler = new InfineonHandler();
        byte[] ndefMessage = handler.createConnectionHandoverNdefMessage(bluetooth_device);

        WriteNdef writeNdef = new WriteNdef();
        writeNdef.setNdefMessage(ndefMessage);
        writeNdef.execute(this.apduChannel);     //Ndef message needs to be set before executed
    }
}