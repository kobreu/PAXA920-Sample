package com.example.rafaelpaiement.pax_beta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.EnumSet;

import io.mpos.accessories.AccessoryFamily;
import io.mpos.accessories.parameters.AccessoryParameters;
import io.mpos.provider.ProviderMode;
import io.mpos.transactions.Currency;
import io.mpos.transactions.Transaction;
import io.mpos.transactions.parameters.TransactionParameters;
import io.mpos.ui.shared.MposUi;
import io.mpos.ui.shared.model.MposUiConfiguration;

public class MainActivity extends AppCompatActivity {

    private final static String merchantIdentifier = "1f9a9a56-6347-4e21-81d0-f59a7bcaa0a8";
    private final static String merchantSecretKey = "r5whofE1sLY9GaiPbl0uzG7oqU0xJ2eU";
   // private final static String merchantIdentifier = "2c564e34-9b7f-4490-abf4-888a7806a8d9";
    //private final static String merchantSecretKey = "wX07YEQ8IUkBPvFgTgTgMsRx828DY4T5";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentButtonClicked();
            }
        });
        findViewById(R.id.refund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    void paymentButtonClicked() {




        MposUi ui = MposUi.initialize(this, ProviderMode.LIVE, merchantIdentifier, merchantSecretKey);
        EditText Payment = findViewById(R.id.amount);

        ui.getConfiguration().setSummaryFeatures(EnumSet.of(
                // Add this line, if you do want to offer Printing Customer Receipt
                MposUiConfiguration.SummaryFeature.PRINT_CUSTOMER_RECEIPT,
                // Add this line, if you do want to offer Printing Merchant Receipt
                MposUiConfiguration.SummaryFeature.PRINT_MERCHANT_RECEIPT,
                // Add this line, if you do want to offer Sending Receipt via Email
                MposUiConfiguration.SummaryFeature.SEND_RECEIPT_VIA_EMAIL)
        );

        AccessoryParameters accessoryParameters = new AccessoryParameters.Builder(AccessoryFamily.PAX).integrated().build();
        ui.getConfiguration().setTerminalParameters(accessoryParameters);
        ui.getConfiguration().setPrinterParameters(accessoryParameters);

        // Add this line if you would like to collect the customer signature on the receipt (as opposed to the digital signature)
        // ui.getConfiguration().setSignatureCapture(MposUiConfiguration.SignatureCapture.ON_RECEIPT);

        if (!TextUtils.isEmpty(Payment.getText().toString().trim())) {
            if (new BigDecimal(Payment.getText().toString()).compareTo(BigDecimal.ZERO) <= 0) {
                ((EditText) findViewById(R.id.amount)).setText("The amount should be more than 0");
                return;
            }

            TransactionParameters transactionParameters = new TransactionParameters.Builder()
                    .charge(new BigDecimal(Payment.getText().toString()), Currency.EUR)
                    .subject("Bouquet of Flowers")
                    .customIdentifier("yourReferenceForTheTransaction")
                    .build();

            Intent intent = ui.createTransactionIntent(transactionParameters);
            startActivityForResult(intent, MposUi.REQUEST_CODE_PAYMENT);
            ((EditText) findViewById(R.id.amount)).setText("");

        }else
        {
            ((EditText) findViewById(R.id.amount)).setText("Please input an amount");
            return;
        }



        }
    }