package com.returno.dynasty.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.utils.FetchUtils;
import com.returno.dynasty.utils.Urls;
import com.returno.dynasty.utils.UserUtils;

import timber.log.Timber;

public class LoginFragment extends Fragment {
    private MaterialToolbar toolbar;
    private TextInputEditText phoneEdit,codeEdit;
    private TextInputLayout codeLayout;
    private MaterialButton signInButton,getOtpButton;
    private String userPhone,phoneNumber;
    private int code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        View view=inflater.inflate(R.layout.login_layout,container,false);
        toolbar=view.findViewById(R.id.toolBar);
        phoneEdit=view.findViewById(R.id.user_phone);
        codeEdit=view.findViewById(R.id.otp_code);
        codeLayout=view.findViewById(R.id.codeLayout);
        signInButton=view.findViewById(R.id.sign_in);
        getOtpButton=view.findViewById(R.id.get_otp);

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ((MainActivity)getActivity()).setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.e("clicked");
                activity.onBackPressed();
                activity.recreate();

            }
        });

        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp();
            }
        });
return view;
    }

    private void getOtp() {
        String phone=phoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)){
            phoneEdit.setError("The phoneNumber cannot be empty");
            return;
        }

        if (phone.length()!=10 || !phone.startsWith("0")){
            phoneEdit.setError("The phoneNumber is invalid");
            return;
        }
        phoneNumber=phone;
        if (phone.startsWith("01")){
            phone=phone.replaceFirst("01","2547");
        }
        if (phone.startsWith("07")){
            phone=phone.replaceFirst("07","2547");
        }
        userPhone=phone;
        this.code= UserUtils.generateRandomOTP();
        Timber.e(String.valueOf(this.code));

        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.get(Urls.GET_API_KEY_URL)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AndroidNetworking.post(Urls.SMS_URL)
                                .addBodyParameter("username","Dynasty")
                                .addBodyParameter("api_key",response)
                                .addBodyParameter("sender","SMARTLINK")
                                .addBodyParameter("to",userPhone)
                                .addBodyParameter("message","To Verify Your Dynasty account use: "+code+" Dial *495*5# to Unsubscribe")
                                .addBodyParameter("msgtype","5")
                                .addBodyParameter("dlr","0")
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                       pDialog.dismiss();
                                        setUpValidation();
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Timber.e(anError);
anError.printStackTrace();
                                    }
                                });
                    }

                    @Override
                    public void onError(ANError anError) {
                        Timber.e(anError);
anError.printStackTrace();
                    }
                });
    }

    private void setUpValidation() {
        codeLayout.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentCode=codeEdit.getText().toString();
                if (code==Integer.parseInt(sentCode)){
                    Dialog pDialog=new Dialog(getActivity());
                    pDialog.setContentView(R.layout.progress_dialog_transparent);
                    pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    pDialog.setCancelable(false);
                    pDialog.show();
                   new FetchUtils().fetchUserDetails(phoneNumber, new FetchCallBacks() {
                       @Override
                       public void onError(String message) {
                           if (pDialog.isShowing())
                           pDialog.dismiss();

                           Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();

                       }

                       @Override
                       public void onUserFetched(User user) {
                           UserUtils.saveUser(user,getActivity());
                           pDialog.dismiss();
                           Toast.makeText(getActivity(),"Login Success",Toast.LENGTH_LONG).show();

                           FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                           transaction.replace(R.id.fragmentContainer,new ProfileFragment())
                                   .commit();
                           transaction.addToBackStack(null);
                       }
                   });
                }else {

                    Toast.makeText(getActivity(),"Wrong Code",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
