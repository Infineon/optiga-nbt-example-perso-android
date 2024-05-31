// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.commands;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_GPIO_I2C_IRQ;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_INT_I2C;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_INT_NFC;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_INT_NFC_I2C;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_TAG_COMM_IF_ENABLE;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_TAG_GPIO_FUNCTION;

import androidx.annotation.NonNull;

import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.NbtApduResponse;
import com.infineon.hsw.apdu.nbt.NbtCommandSetConfig;
import com.infineon.hsw.utils.UtilException;

/**
 * The SetInterfaceConfig class generates and sends the commands to configure the interface
 * configuration of a NBT sample, it only needs to be provided with a ApduChannel and
 * the interface settings.
 */
public class SetInterfaceConfig implements ICommandFlow {

    /**
     * Tag for interface setting
     */
    private final short TAG_COMM_IF_ENABLE = NBT_TAG_COMM_IF_ENABLE;

    /**
     * Tag for GPIO functionality setting
     * 1 | Output disabled, 2 | RF-IRQ output, 3 | I2C-IRQ output, 4 | PT + RF-IRQ output
     */
    private final short TAG_GPIO_FUNCTION = NBT_TAG_GPIO_FUNCTION;

    /**
     * Initializing the GPIO configuration
     */
    private byte gpioConfig = NBT_GPIO_I2C_IRQ;

    /**
     * Initializing the I2C configuration
     * 0x01 | I2C interface enabled, 0x10 | NFC interface enabled, 0x11 |  NFC and I2C enabled (default)
     */
    private byte interfaceConfig = NBT_INT_NFC_I2C;

    /**
     * Writes the interface and gpio settings accordingly
     *
     * @param apduChannel APDU channel to the NFC Interface
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    public void execute(@NonNull ApduChannel apduChannel) throws ApduException, UtilException {
        NbtCommandSetConfig configCommandSet = new NbtCommandSetConfig(apduChannel, 0);
        NbtApduResponse apduResponse = configCommandSet.selectConfiguratorApplication();
        apduResponse.checkOK();

        apduResponse = configCommandSet.setConfigData(TAG_GPIO_FUNCTION, gpioConfig);
        apduResponse.checkOK();

        apduResponse = configCommandSet.setConfigData(TAG_COMM_IF_ENABLE, interfaceConfig);
        apduResponse.checkOK();
    }

    /**
     * Setter function for interface and gpio settings to initialize according values
     *
     * @param i2cConfig Boolean if i2c interface is enabled or disabled
     * @param nfcConfig Boolean if nfc interface is enabled or disabled
     * @param gpio      The IRQ setting for the GPIO
     */
    public void setConfig(boolean i2cConfig, boolean nfcConfig, byte gpio) {

        if (nfcConfig && i2cConfig) {
            interfaceConfig = NBT_INT_NFC_I2C;
        } else if (nfcConfig) {
            interfaceConfig = NBT_INT_NFC;
        } else if (i2cConfig) {
            interfaceConfig = NBT_INT_I2C;
        }
        gpioConfig = gpio;
    }
}
