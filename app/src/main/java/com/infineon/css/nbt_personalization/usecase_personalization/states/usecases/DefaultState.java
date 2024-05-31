// SPDX-FileCopyrightText: 2024 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization.usecase_personalization.states.usecases;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.DEFAULT_FAP;
import static com.infineon.css.nbt_personalization.usecase_personalization.utils.NbtConstants.NBT_GPIO_I2C_IRQ;

import androidx.annotation.NonNull;

import com.infineon.css.nbt_personalization.usecase_personalization.commands.DeleteNdef;
import com.infineon.css.nbt_personalization.usecase_personalization.commands.ICommandFlow;
import com.infineon.css.nbt_personalization.usecase_personalization.commands.ReadFileAccessPolicy;
import com.infineon.css.nbt_personalization.usecase_personalization.states.StateConfig;
import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.apdu.ApduException;
import com.infineon.hsw.apdu.nbt.model.FileAccessPolicyException;
import com.infineon.hsw.utils.UtilException;

import java.io.IOException;
import java.util.Arrays;

/**
 * The DefaultState Class represents the NBT sample configuration for the Default State. It only
 * needs to be provided with a ApduChannel to reset the NBT sample accordingly. Additionally the
 * class can check if the presented sample is in default state using isDefaultState.
 */
public class DefaultState implements IState {

    /**
     * Establishes an interface channel with protocol (APDU) specific functionality
     */
    private ApduChannel apduChannel;

    /**
     * Executes necessary commands to personalize sample to default state
     * - Deletes data in files
     * - Sets the interface configuration accordingly
     * - Write the File Access Policy accordingly
     *
     * @param apduChannel APDU specific channel
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               Signals that an I/O exception of some sort has occurred
     */
    public void execute(@NonNull ApduChannel apduChannel) throws UtilException, ApduException, FileAccessPolicyException, IOException {

        this.apduChannel = apduChannel;

        //If no parameters are specifically set, they will be set to default state
        StateConfig defaultStateConfig = new StateConfig.StateConfigBuilder(this.apduChannel)
                .setI2cInterfaceConfig(true)
                .setNfcInterfaceConfig(true)
                .setGpioInterfaceConfig(NBT_GPIO_I2C_IRQ)
                .build();
        defaultStateConfig.setStateConfig();

        resetFiles();
    }

    /**
     * Checks the state of the presented device by comparing the FAP to the default FAP
     *
     * @param apduChannel APDU specific channel
     * @return True if tag is in default state
     * @throws UtilException Thrown by libraries utils
     * @throws ApduException Thrown by command set of APDU library
     */
    public static boolean isDefaultState(ApduChannel apduChannel) throws UtilException, ApduException {

        ReadFileAccessPolicy readFileAccessPolicy = new ReadFileAccessPolicy();
        readFileAccessPolicy.execute(apduChannel);

        return Arrays.equals(readFileAccessPolicy.getFap(), DEFAULT_FAP);

    }

    /**
     * Delete data in file ( only the NDEF file)
     *
     * @throws UtilException             Thrown by libraries utils
     * @throws ApduException             Thrown by command set of APDU library
     * @throws FileAccessPolicyException Thrown by APDU library in case of FAP error
     * @throws IOException               I/O exception has occurred, probably by a ByteArrayOutputStream
     */
    private void resetFiles() throws UtilException, FileAccessPolicyException, ApduException, IOException {

        ICommandFlow deleteNdef = new DeleteNdef();
        deleteNdef.execute(this.apduChannel);
    }
}