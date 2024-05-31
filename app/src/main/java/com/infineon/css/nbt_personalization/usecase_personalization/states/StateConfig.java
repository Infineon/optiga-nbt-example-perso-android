// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.states;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_CC;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FAP;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE1;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE2;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE3;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_FILE4;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP_NDEF;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_GPIO_I2C_IRQ;

import androidx.annotation.NonNull;

import com.infineon.css.nbt_personalization.usecase_personalization.commands.SetFileAccessPolicy;
import com.infineon.css.nbt_personalization.usecase_personalization.commands.SetInterfaceConfig;
import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicy;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.utils.UtilException;

import java.io.IOException;

/**
 * Builder class to define the states/configurations for the different use cases
 */
@SuppressWarnings("unused")
public class StateConfig {

    /**
     * Establishes an interface channel with protocol (APDU) specific functionality
     */
    private final ApduChannel apduChannel;

    /**
     * CC file access policy
     */
    private final FileAccessPolicy fap_cc;

    /**
     * NDEF file access policy
     */
    private final FileAccessPolicy fap_ndef;

    /**
     * FAP file access policy
     */
    private final FileAccessPolicy fap_fap;

    /**
     * Proprietary file1 file access policy
     */
    private final FileAccessPolicy fap_file1;

    /**
     * Proprietary file2 file access policy
     */
    private final FileAccessPolicy fap_file2;

    /**
     * Proprietary file3 file access policy
     */
    private final FileAccessPolicy fap_file3;

    /**
     * Proprietary file4 file access policy
     */
    private final FileAccessPolicy fap_file4;

    /**
     * Initializing the nfc configuration
     */
    private final boolean nfcInterfaceConfig;

    /**
     * Initializing the i2c configuration
     */
    private final boolean i2cInterfaceConfig;

    /**
     * Initializing the GPIO configuration
     */
    private final byte gpioConfig;

    /**
     * Getter function for the CC file access policy
     *
     * @return Returns the CC file access policy
     */
    public FileAccessPolicy getFapCc() {
        return fap_cc;
    }

    /**
     * Getter function for the NDEF file access policy
     *
     * @return Returns the NDEF file access policy
     */
    public FileAccessPolicy getFapNdef() {
        return fap_ndef;
    }

    /**
     * Getter function for the FAP file access policy
     *
     * @return Returns the FAP file access policy
     */
    public FileAccessPolicy getFapFap() {
        return fap_fap;
    }

    /**
     * Getter function for the proprietary file1 file access policy
     *
     * @return Returns the proprietary file1 file access policy
     */
    public FileAccessPolicy getFapFile1() {
        return fap_file1;
    }

    /**
     * Getter function for the proprietary file2 file access policy
     *
     * @return Returns the proprietary file2 file access policy
     */
    public FileAccessPolicy getFapFile2() {
        return fap_file2;
    }

    /**
     * Getter function for the proprietary file2 file access policy
     *
     * @return Returns the proprietary file3 file access policy
     */
    public FileAccessPolicy getFapFile3() {
        return fap_file3;
    }

    /**
     * Getter function for the proprietary file4 file access policy
     *
     * @return Returns the proprietary file4 file access policy
     */
    public FileAccessPolicy getFapFile4() {
        return fap_file4;
    }

    /**
     * Getter function for the I2C interface config
     *
     * @return Returns the interface config
     */
    public boolean getI2cInterfaceConfig() {
        return i2cInterfaceConfig;
    }

    /**
     * Getter function for the NFC interface config
     *
     * @return Returns the interface config
     */
    public boolean getNfcInterfaceConfig() {
        return nfcInterfaceConfig;
    }

    /**
     * Getter function for the gpio config
     *
     * @return Returns the gpio config
     */
    public byte getGpioConfig() {
        return gpioConfig;
    }

    /**
     * Executes the configuration of the sample, based on the use case specific settings
     *
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               Signals that an I/O exception of some sort has occurred
     */
    public void setStateConfig() throws UtilException, FileAccessPolicyException, ApduException, IOException {

        SetFileAccessPolicy setFileAccessPolicy = new SetFileAccessPolicy();
        setFileAccessPolicy.setFileAccessPolicy(fap_cc, fap_ndef, fap_fap, fap_file1, fap_file2, fap_file3, fap_file4);
        setFileAccessPolicy.execute(apduChannel);

        SetInterfaceConfig setInterfaceConfig = new SetInterfaceConfig();
        setInterfaceConfig.setConfig(i2cInterfaceConfig, nfcInterfaceConfig, gpioConfig);
        setInterfaceConfig.execute(apduChannel);
    }

    /**
     * Constructor validates parameters and saves them to members
     *
     * @param builder State config builder
     */
    private StateConfig(StateConfigBuilder builder) {
        this.nfcInterfaceConfig = builder.nfcInterfaceConfig;
        this.i2cInterfaceConfig = builder.i2cInterfaceConfig;
        this.gpioConfig = builder.gpioConfig;
        this.fap_cc = builder.fap_cc;
        this.fap_ndef = builder.fap_ndef;
        this.fap_fap = builder.fap_fap;
        this.fap_file1 = builder.fap_file1;
        this.fap_file2 = builder.fap_file2;
        this.fap_file3 = builder.fap_file3;
        this.fap_file4 = builder.fap_file4;
        this.apduChannel = builder.apduChannel;
    }

    /**
     * Builder class for the sample state configuration
     */
    public static class StateConfigBuilder {

        /**
         * Initializing the nfc configuration
         */
        private boolean nfcInterfaceConfig = true;

        /**
         * Initializing the i2c configuration
         */
        private boolean i2cInterfaceConfig = true;

        /**
         * Initializing the GPIO configuration
         */
        private byte gpioConfig = NBT_GPIO_I2C_IRQ;

        /**
         * Establishes an interface channel with protocol (APDU) specific functionality
         */
        private final ApduChannel apduChannel;

        /**
         * Initializing default CC file file access policy
         */
        private FileAccessPolicy fap_cc = DEFAULT_FAP_CC;

        /**
         * Initializing default NDEF file file access policy
         */
        private FileAccessPolicy fap_ndef = DEFAULT_FAP_NDEF;

        /**
         * Initializing default FAP file file access policy
         */
        private FileAccessPolicy fap_fap = DEFAULT_FAP_FAP;

        /**
         * Initializing default proprietary file1 specific file access policy
         */
        private FileAccessPolicy fap_file1 = DEFAULT_FAP_FILE1;

        /**
         * Initializing default proprietary file2 specific file access policy
         */
        private FileAccessPolicy fap_file2 = DEFAULT_FAP_FILE2;

        /**
         * Initializing default proprietary file3 file access policy
         */
        private FileAccessPolicy fap_file3 = DEFAULT_FAP_FILE3;

        /**
         * Initializing default proprietary file4 file access policy
         */
        private FileAccessPolicy fap_file4 = DEFAULT_FAP_FILE4;

        /**
         * Constructor validates parameters and saves them to members
         *
         * @param apduChannel APDU specific channel
         */
        public StateConfigBuilder(@NonNull ApduChannel apduChannel) {

            this.apduChannel = apduChannel;
        }

        /**
         * Setter function for the CC file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapCc(FileAccessPolicy fap) {
            this.fap_cc = fap;
            return this;
        }

        /**
         * Setter function for the NDEF file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapNdef(FileAccessPolicy fap) {
            this.fap_ndef = fap;
            return this;
        }

        /**
         * Setter function for the FAP file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapFap(FileAccessPolicy fap) {
            this.fap_fap = fap;
            return this;
        }

        /**
         * Setter function for the proprietary file1 file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapFile1(FileAccessPolicy fap) {
            this.fap_file1 = fap;
            return this;
        }

        /**
         * Setter function for the proprietary file2 file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapFile2(FileAccessPolicy fap) {
            this.fap_file2 = fap;
            return this;
        }

        /**
         * Setter function for the proprietary file3 file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapFile3(FileAccessPolicy fap) {
            this.fap_file3 = fap;
            return this;
        }

        /**
         * Setter function for the proprietary file4 file access policy
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setFapFile4(FileAccessPolicy fap) {
            this.fap_file4 = fap;
            return this;
        }

        /**
         * Setter function for the i2c interface config
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setI2cInterfaceConfig(boolean i2cInterfaceConfig) {
            this.i2cInterfaceConfig = i2cInterfaceConfig;
            return this;
        }

        /**
         * Setter function for the nfc interface config
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setNfcInterfaceConfig(boolean nfcInterfaceConfig) {
            this.nfcInterfaceConfig = nfcInterfaceConfig;
            return this;
        }

        /**
         * Setter function for the gpio config
         *
         * @return StateConfigBuilder context
         */
        public StateConfigBuilder setGpioInterfaceConfig(byte gpioConfig) {
            this.gpioConfig = gpioConfig;
            return this;
        }

        /**
         * Constructor of the builder class
         *
         * @return StateConfig class
         */
        public StateConfig build() {
            return new StateConfig(this);
        }

    }
}
