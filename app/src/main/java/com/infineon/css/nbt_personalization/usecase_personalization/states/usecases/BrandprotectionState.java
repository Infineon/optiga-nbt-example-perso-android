// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.states.usecases;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.ALLOW;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.BLOCK;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_CC_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_NDEF_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_GPIO_NO_IRQ;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP1_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP2_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP3_FILE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_ID_PP4_FILE;

import androidx.annotation.NonNull;

import com.infineon.css.nbt_personalization.usecase_personalization.commands.WriteEcKey;
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
import java.security.cert.CertificateException;

/**
 * The BrandprotectionState Class represents the NBT sample configuration for the Brand Protection
 * use case. It  needs to be provided with a ApduChannel, the COTT link and a certificate to
 * configure the NBT sample accordingly.
 */
public class BrandprotectionState implements IState {

    /**
     * Default COTT url
     */
    private String cott_url = "http://www.infineon.com/?cott=PLACEHOLDERPLACEHOLDERPLACEHOLDERPLACEHOLDER";

    /**
     * Holds the sample demo certificate for members
     */
    private static byte[] cert;

    /**
     * Holds the sample demo key for members
     */
    private static byte[] ec_key;

    /**
     * Establishes an interface channel with protocol (APDU) specific functionality
     */
    private ApduChannel apduChannel;

    /**
     * Initializing use case specific CC file file access policy
     */
    private final FileAccessPolicy FAP_CC = new FileAccessPolicy(NBT_ID_CC_FILE, BLOCK, BLOCK, ALLOW, BLOCK);

    /**
     * Initializing use case specific NDEF file file access policy
     */
    private final FileAccessPolicy FAP_NDEF = new FileAccessPolicy(NBT_ID_NDEF_FILE, BLOCK, BLOCK, ALLOW, BLOCK);

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
     * @param url         COTT url
     * @param sample_cert Certificate for the sample
     * @param sample_key  EC key for the sample
     */
    public BrandprotectionState(String url, @NonNull byte[] sample_cert, @NonNull byte[] sample_key) {

        if (url != null) {
            String cott_placeholder = "?cott=PLACEHOLDERPLACEHOLDERPLACEHOLDERPLACEHOLDER";
            this.cott_url = url + cott_placeholder;
        }
        cert = sample_cert;
        ec_key = sample_key;
    }

    /**
     * Executes necessary commands to personalize sample to brandprotection state
     * - Write the NDEF accordingly
     * - Write the sample EC key
     * - Write the File Access Policy accordingly
     * - Sets the interface configuration accordingly
     *
     * @param apduChannel APDU specific channel
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws CertificateException      Thrown if parsing of certificate fails
     * @throws IOException               Thrown if reading of key/certificate file fails
     * @throws NdefException             An exception in the NDEF file specific library occurred
     */
    public void execute(@NonNull ApduChannel apduChannel) throws UtilException, ApduException, CertificateException, IOException, FileAccessPolicyException, NdefException {

        this.apduChannel = apduChannel;

        writeSampleEcKey();

        writeBrandprotectionNdefMessage();

        StateConfig brandprotectionStateConfig = new StateConfig.StateConfigBuilder(this.apduChannel)
                .setFapCc(FAP_CC)
                .setFapNdef(FAP_NDEF)
                .setFapFile1(FAP_FILE1)
                .setFapFile2(FAP_FILE2)
                .setFapFile3(FAP_FILE3)
                .setFapFile4(FAP_FILE4)
                .setI2cInterfaceConfig(true)
                .setNfcInterfaceConfig(true)
                .setGpioInterfaceConfig(NBT_GPIO_NO_IRQ)
                .build();
        brandprotectionStateConfig.setStateConfig();
    }

    /**
     * Creates and writes COTT link and certificate to NDEF file, according to the brand protection use case
     *
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     * @throws IOException   Signals that an I/O exception of some sort has occurred
     * @throws NdefException An exception in the NDEF file specific library occurred
     */
    private void writeBrandprotectionNdefMessage() throws UtilException, ApduException, IOException, NdefException {

        // Choose between Android or Infineon specific NDEF library using the corresponding handler
        INdefHandler handler = new InfineonHandler();

        byte[] ndefMessage = handler.createBrandprotectionNdefMessage(cott_url, cert);

        WriteNdef writeNdef = new WriteNdef();
        writeNdef.setNdefMessage(ndefMessage);
        writeNdef.execute(this.apduChannel);     //Ndef message needs to be set before executed
    }

    /**
     * The EC key in the BSK file needs to be overwritten for this demonstrator purpose to ensure
     * that the sample data also works with productive samples.
     *
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    private void writeSampleEcKey() throws UtilException, ApduException {
        WriteEcKey writeEcKey = new WriteEcKey();
        writeEcKey.setEcKey(ec_key);
        writeEcKey.execute(this.apduChannel);
    }
}