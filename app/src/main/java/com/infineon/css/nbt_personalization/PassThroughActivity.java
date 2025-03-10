// SPDX-FileCopyrightText: Copyright (c) 2024-2025 Infineon Technologies AG
// SPDX-License-Identifier: MIT

package com.infineon.css.nbt_personalization;

import static com.infineon.css.nbt_personalization.usecase_personalization.utils.InterfaceChannel.InitializeChannel;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.infineon.css.nbt_personalization.usecase_personalization.states.usecases.IState;
import com.infineon.css.nbt_personalization.usecase_personalization.states.usecases.PassThroughState;
import com.infineon.hsw.apdu.ApduChannel;
import com.infineon.hsw.channel.IChannel;

/**
 * Detects tag and configures it to the PassThrough use case
 */
public class PassThroughActivity extends AppCompatActivity {

    /**
     * Instruction text to the user
     */
    TextView instructionText;

    /**
     * Passthrough icon for GUI
     */
    ImageView logoPassthrough;

    /**
     * Button to return to the main activity
     */
    ImageButton buttonReturn;

    /**
     * Flag to store personalization status
     */
    boolean isPersonalized;

    /**
     * Duration until activity returns to main after successful personalization
     */
    private static final int WAITING_TIME = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passthough);

        //GUI initialization
        logoPassthrough = findViewById(R.id.image_logo);
        logoPassthrough.setImageResource(R.drawable.icon_passthrough_lightgrey);
        instructionText = findViewById(R.id.textView_cardTap);
        instructionText.setText(R.string.string_passthrough_tap);
        buttonReturn = findViewById(R.id.button_return);

        //Flag to ensure that sample is only configured once
        isPersonalized = false;

        buttonReturn.setOnClickListener(v -> returnToMain());
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

        if (!isPersonalized && (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep com = IsoDep.get(tag);
            IChannel channel = InitializeChannel(com);
            ApduChannel apduChannel = new ApduChannel(channel);

            handleUsecase(apduChannel);

        }
    }

    /**
     * NFC communication channel is used to:
     * 1. Open APDU channel to tag
     * 2. Call and execute the corresponding state class to configure the sample for the use case
     * 3. Close APDU channel to tag
     *
     * @param apduChannel NFC communication channel
     */
    private void handleUsecase(ApduChannel apduChannel) {

        try {
            apduChannel.connect();

            IState passThroughState = new PassThroughState();
            passThroughState.execute(apduChannel);
            setGui();

            apduChannel.disconnect();
        } catch (Exception e) {
            runOnUiThread(() -> instructionText.setText(R.string.string_error));
        }

    }

    /**
     * Set the GUI of the App
     */
    private void setGui() {
        runOnUiThread(() -> {
            logoPassthrough.setImageResource(R.drawable.icon_passthrough_green);
            instructionText.setText(R.string.string_passthrough_success);
            isPersonalized = true;
            buttonReturn.setImageResource(R.drawable.icon_return_grey);
            buttonReturn.setEnabled(false);
        });
        // Returns to main activity after a delay
        new Handler(Looper.getMainLooper()).postDelayed(this::returnToMain, WAITING_TIME);
    }

    /**
     * Returns to main activity
     */
    private void returnToMain() {
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(activityIntent);
        finish();
    }
}