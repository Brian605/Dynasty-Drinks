package com.returno.dynasty.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.returno.dynasty.R;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.GeocoderHelper;
import com.returno.dynasty.utils.Urls;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegisterFragment extends Fragment {
    private MaterialToolbar toolbar;
    private TextInputEditText phoneEdit,nameEdit;
    private MaterialButton signInButton,signUpButton;
    private MaterialCheckBox consentCheck;
    FusedLocationProviderClient locationProviderClient;
    private String name,phone,userLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       container.removeAllViews();
        View view=inflater.inflate(R.layout.register_layout,container,false);
        toolbar=view.findViewById(R.id.toolBar1);
        phoneEdit=view.findViewById(R.id.user_phone);
        nameEdit=view.findViewById(R.id.user_name);
        signInButton=view.findViewById(R.id.sign_in);
        signUpButton=view.findViewById(R.id.sign_up);
        consentCheck=view.findViewById(R.id.consent);

        SpannableString spannableString=new SpannableString(getResources().getString(R.string.consent));
        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan,20,41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        consentCheck.setText(spannableString);
        consentCheck.setMovementMethod(LinkMovementMethod.getInstance());

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        //activity.findViewById(R.id.toolBar).setVisibility(View.GONE);
        ((MainActivity)getActivity()).setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
                activity.recreate();

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer,new LoginFragment())
                        .commit();
                transaction.addToBackStack(null);
            }
        });
return view;
    }

    private void registerUser() {
        if (!consentCheck.isChecked()){
            consentCheck.setError("You need to accept the terms of service");
            return;
        }

        String name=nameEdit.getText().toString();
        String phone=phoneEdit.getText().toString();

        if (TextUtils.isEmpty(name)){
            nameEdit.setError("The username cannot be empty");
            return;
        }
        if (TextUtils.isEmpty(phone)){
            phoneEdit.setError("The phoneNumber cannot be empty");
            return;
        }

        if (phone.length()!=10 || !phone.startsWith("0")){
            phoneEdit.setError("The phoneNumber is invalid");
            return;
        }

        this.name=name;
        this.phone=phone;

        checkPermission();

    }

    private void checkPermission(){
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(getActivity(),"You need to enable location Permissions. Click Permissions then Click Enable",Toast.LENGTH_LONG).show();
                            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getActivity().getPackageName())),300);
                            return;
                        }
checkLocationProviders();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
token.continuePermissionRequest();
                    }
                }).check();
    }

    private void checkLocationProviders() {
        LocationManager manager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )){
            Toast.makeText(getActivity(),"You need to enable GPS ",Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),300);
            return;
        }

        getUserLocation();
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.getResult()!=null){
                    geoCode(task.getResult());

                }else {
                    registerLocationListener();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @SuppressLint("MissingPermission")
    private void registerLocationListener() {
        LocationRequest request=new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5);
        request.setFastestInterval(0);
        request.setNumUpdates(1);
        locationProviderClient=LocationServices.getFusedLocationProviderClient(getActivity());
        locationProviderClient.requestLocationUpdates(request, new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location=locationResult.getLastLocation();
                geoCode(location);
            }
        }, Looper.myLooper());
    }

    private void geoCode(Location result) {
        GeocoderHelper helper=new GeocoderHelper(getActivity());
        String location;
        try {
            location =helper.execute(result).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            location="Unknown location";

        }
        userLocation=location;
        uploadData();
    }

    private void uploadData() {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        AndroidNetworking.post(Urls.ADD_USER_URL)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.USER_NAME,name)
                .addBodyParameter(Constants.USER_PHONE,phone)
                .addBodyParameter(Constants.USER_LOCATION,userLocation)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                        if (response.equals("Success") || response.equals("user already registered")){
                            FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragmentContainer,new LoginFragment())
                                    .commit();
                            transaction.addToBackStack(null);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();

                        pDialog.dismiss();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkPermission();
    }
}
