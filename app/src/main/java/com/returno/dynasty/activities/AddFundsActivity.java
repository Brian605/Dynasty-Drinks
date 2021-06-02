package com.returno.dynasty.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bdhobare.mpesa.Mode;
import com.bdhobare.mpesa.Mpesa;
import com.bdhobare.mpesa.interfaces.AuthListener;
import com.bdhobare.mpesa.interfaces.MpesaListener;
import com.bdhobare.mpesa.models.STKPush;
import com.bdhobare.mpesa.utils.Pair;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.Urls;
import com.returno.dynasty.utils.UserUtils;

import timber.log.Timber;

public class AddFundsActivity extends AppCompatActivity implements AuthListener, MpesaListener {

    private int amountToAdd;
    private Dialog pDialog;
    private MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_input);

        TextInputEditText amountInPut=findViewById(R.id.input);
        MaterialButton continueBtn=findViewById(R.id.btnContinue);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(amountInPut.getText().toString().trim())){
                    amountToAdd= Integer.parseInt(amountInPut.getText().toString().trim());
                    startPayment();
                }

            }
        });
        pDialog=new Dialog(AddFundsActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        Mpesa.with(this, Constants.CUSTOMER_KEY,Constants.CUSTOMER_SECRET, Mode.SANDBOX);

        pDialog.show();
    }

    private void startPayment() {
        pDialog.show();
        User user= UserUtils.getUser(AddFundsActivity.this);
        STKPush.Builder builder=new STKPush.Builder(Constants.SHORT_CODE,
                Constants.PASS_KEY,
                amountToAdd,
                Constants.SHORT_CODE,
                user.getPhoneNumber());
        builder.setDescription("Add Funds");
        builder.setCallBackURL(Urls.M_PESA_FALL_BACK_URL);
        STKPush push=builder.build();

        Mpesa.getInstance().pay(this,push);
    }

    @Override
    public void onAuthError(Pair<Integer, String> result) {
        Timber.e(result.message);
        Timber.e(String.valueOf(result.code));
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"An Error Occured While Processing",Toast.LENGTH_LONG).show();
        finish();

    }

    @Override
    public void onAuthSuccess() {
        if (pDialog.isShowing())pDialog.dismiss();
    }

    @Override
    public void onMpesaError(Pair<Integer, String> result) {
        Timber.e(result.message);
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"An Error Occured While Processing",Toast.LENGTH_LONG).show();
finish();
    }

    @Override
    public void onMpesaSuccess(String MerchantRequestID, String CheckoutRequestID, String CustomerMessage) {
        Timber.e(CustomerMessage);
        if (pDialog.isShowing())pDialog.dismiss();
        Toast.makeText(getApplicationContext(),"Wait For The STK",Toast.LENGTH_LONG).show();
finish();
    }

}