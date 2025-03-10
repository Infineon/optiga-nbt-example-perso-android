// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.InterfaceChannel.InitializeChannel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.infineon.css.nbt_personalization.usecase_personalization.states.usecases.DefaultState;
import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.channel.IChannel;

/**
 * Main activity of the app which is intended to configure a NBT sample to the desired use case
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Button to open brand protection activity
     */
    ImageButton buttonBrandProtection;

    /**
     * Button to open ADT activity
     */
    ImageButton buttonAdt;

    /**
     * Button to open connection handover activity
     */
    ImageButton buttonConnectionHandover;

    /**
     * Button to open pass through activity
     */
    ImageButton buttonPassThrough;

    /**
     * Button to reset TAG to default settings
     */
    ImageButton buttonReset;

    /**
     * Instruction text to the user
     */
    TextView instructionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization of GUI elements
        instructionText = findViewById(R.id.textView_cardTap);
        buttonAdt = findViewById(R.id.button_adt);
        buttonAdt.setEnabled(false);
        buttonAdt.setImageResource(R.drawable.icon_adt_lightgrey);
        buttonBrandProtection = findViewById(R.id.button_brandProtection);
        buttonBrandProtection.setEnabled(false);
        buttonBrandProtection.setImageResource(R.drawable.icon_cert_lightgrey);
        buttonConnectionHandover = findViewById(R.id.button_connectionHandover);
        buttonConnectionHandover.setEnabled(false);
        buttonConnectionHandover.setImageResource(R.drawable.icon_ble_lightgrey);
        buttonPassThrough = findViewById(R.id.button_passThrough);
        buttonPassThrough.setEnabled(false);
        buttonPassThrough.setImageResource(R.drawable.icon_passthrough_lightgrey);
        buttonReset = findViewById(R.id.button_reset);
        buttonReset.setImageResource(R.drawable.icon_reset_lightgrey);
        buttonReset.setEnabled(false);

        //This button starts the Brand Protection Activity and allows to run it's personalization
        buttonBrandProtection.setOnClickListener(v -> {
            Intent activityIntent = new Intent(getApplicationContext(), BrandProtectionActivity.class);
            startActivity(activityIntent);
            finish();
        });

        //This button starts the ADT Activity and allows to run it's personalization
        buttonAdt.setOnClickListener(v -> {
            Intent activityIntent = new Intent(getApplicationContext(), AdtActivity.class);
            startActivity(activityIntent);
            finish();
        });

        //This button starts the Connection Handover Activity and allows to run it's personalization
        buttonConnectionHandover.setOnClickListener(v -> {
            Intent activityIntent = new Intent(getApplicationContext(), ConnectionHandoverActivity.class);
            startActivity(activityIntent);
            finish();
        });

        //This button starts the Pass Through Activity and allows to run it's personalization
        buttonPassThrough.setOnClickListener(v -> {
            Intent activityIntent = new Intent(getApplicationContext(), PassThroughActivity.class);
            startActivity(activityIntent);
            finish();
        });

        //This button starts the reset Activity and allows to reset the sample
        buttonReset.setOnClickListener(v -> {
            Intent activityIntent = new Intent(getApplicationContext(), ResetActivity.class);
            startActivity(activityIntent);
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Setting intent to recognized NFC Type A tags with NFC interface
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        IntentFilter[] intentFiltersArray = new IntentFilter[]{ndef,};
        String[][] techListsArray = new String[][]{new String[]{NfcA.class.getName()}};
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    /**
     * Intent is called when a NFC tag with a NDEF message is detected. Communication channel is
     * opened and further use case steps are triggered
     *
     * @param intent NFC tag intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep com = IsoDep.get(tag);
            IChannel channel = InitializeChannel(com);
            ApduChannel apduChannel = new ApduChannel(channel);

            handleUsecase(apduChannel);

        }
    }

    /**
     * NFC communication channel is used to read state of presented device to provide
     * next activity fitting to the state
     *
     * @param apduChannel NFC communication channel
     */
    private void handleUsecase(ApduChannel apduChannel) {

        try {
            apduChannel.connect();

            if (DefaultState.isDefaultState(apduChannel)) {
                setPersoGui();
            } else {
                setResetGui();
            }

            apduChannel.disconnect();
        } catch (Exception e) {
            runOnUiThread(() -> instructionText.setText(R.string.string_error));
        }
    }

    /**
     * Sets the GUI according to the tags status
     */
    private void setPersoGui() {
        runOnUiThread(() -> {
            instructionText.setText(R.string.string_select_usecase);
            buttonAdt.setEnabled(true);
            buttonAdt.setImageResource(R.drawable.icon_adt_green);
            buttonBrandProtection.setEnabled(true);
            buttonBrandProtection.setImageResource(R.drawable.icon_cert_green);
            buttonConnectionHandover.setEnabled(true);
            buttonConnectionHandover.setImageResource(R.drawable.icon_ble_green);
            buttonPassThrough.setEnabled(true);
            buttonPassThrough.setImageResource(R.drawable.icon_passthrough_green);
        });
    }

    /**
     * Sets GUI to only allow a reset of the tag since it is already personalized
     */
    private void setResetGui() {
        runOnUiThread(() -> {
            buttonReset.setEnabled(true);
            instructionText.setText(R.string.string_already_personalized);
            buttonReset.setImageResource(R.drawable.icon_reset_green);
        });
    }
}