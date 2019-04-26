package com.example.pax_beta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.EnumSet;

import io.mpos.accessories.AccessoryFamily;
import io.mpos.accessories.parameters.AccessoryParameters;
import io.mpos.provider.ProviderMode;
import io.mpos.transactions.Currency;
import io.mpos.transactions.parameters.TransactionParameters;
import io.mpos.ui.shared.MposUi;
import io.mpos.ui.shared.model.MposUiConfiguration;

public class MainActivity extends AppCompatActivity {

    private final static String merchantIdentifier = "f415cd8f-e44c-43f3-90ad-d7f306e2451c";
    private final static String merchantSecretKey = "6lUXylBCzWDOyQqiet7tvrpCMN3bJsYt";

    private final static ProviderMode mode = ProviderMode.TEST;
    private final static Currency currency = Currency.USD;

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
    }

    void paymentButtonClicked() {
        MposUi ui = MposUi.initialize(this, mode, merchantIdentifier, merchantSecretKey);
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
                    .charge(new BigDecimal(Payment.getText().toString()), Currency.USD)
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