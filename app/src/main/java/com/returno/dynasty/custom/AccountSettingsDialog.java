package com.returno.dynasty.custom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.opensooq.supernova.gligar.GligarPicker;
import com.returno.dynasty.R;
import com.returno.dynasty.activities.MapsActivity;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.listeners.CompressListener;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.admin.utils.FileUtils;
import com.returno.dynasty.utils.Constants;
import com.returno.dynasty.utils.GeocoderHelper;
import com.returno.dynasty.utils.UserUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AccountSettingsDialog extends DialogFragment {
    private MaterialToolbar toolbar;
    SimpleDraweeView avatarView;
    MaterialButton saveNameBtn,saveLocationBtn;
    TextInputEditText nameEdit;
    ImageView editAvatar;
    MaterialTextView locationView;
    FusedLocationProviderClient locationProviderClient;
    String userLocation;


    public static void showDialog(FragmentManager manager){
        AccountSettingsDialog dialog=new AccountSettingsDialog();
        dialog.show(manager,"Account Settings");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_LiquorStore_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if (dialog!=null){
            int width= ViewGroup.LayoutParams.MATCH_PARENT;
            int height=ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width,height);
            dialog.getWindow().setWindowAnimations(R.style.Theme_LiquorStore_Slide);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.edit_profiles_layout,container,false);
       toolbar=view.findViewById(R.id.toolbar);
       avatarView =view.findViewById(R.id.user_avatar);
       nameEdit=view.findViewById(R.id.nameEdit);
       saveNameBtn=view.findViewById(R.id.editName);
       saveLocationBtn=view.findViewById(R.id.editLocation);
       editAvatar=view.findViewById(R.id.edit_avatar);
       locationView=view.findViewById(R.id.locationView);

       return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        User user= UserUtils.getUser(getActivity());
        avatarView.setImageURI(user.getImageUrl());
        nameEdit.setText(user.getUserName());
        locationView.setText(String.format(Locale.getDefault(),"Delivery Location: %s", user.getLocation()));

        editAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfImage();
            }
        });

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfImage();
            }
        });

        saveNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(nameEdit.getText().toString().trim())){
updateName();
                }
            }
        });

        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceDialog();
            }
        });
    }

    private void showChoiceDialog() {
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity());
        builder.setItems(new String[]{"Use GPS/Network Location", "Pick a Place On Google Maps"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
if (which==0){
    dialog.dismiss();
    pickGPSLocation();
}
if (which==1){
    startActivityForResult(new Intent(getActivity(), MapsActivity.class),500);
}
            }
        });
        builder.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
        Dialog dialog=builder.create();
        dialog.show();
    }

    private void pickGPSLocation() {
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
                            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getActivity().getPackageName())),400);
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
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),400);
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
        updateLocation();
    }

    private void updateLocation() {
        User user= UserUtils.getUser(getActivity());
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
UserUtils.updateLocation(userLocation, user.getPhoneNumber(), new CompleteListener() {
    @Override
    public void onComplete() {

    }
    @Override
    public void onComplete(String message) {
        user.setLocation(message);
        UserUtils.saveUser(user,getActivity());
        Toast.makeText(getActivity(),"Updated!",Toast.LENGTH_LONG).show();
        locationView.setText(String.format(Locale.getDefault(),"Delivery Location: %s", user.getLocation()));
        pDialog.dismiss();

    }

    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(),"Could Not Update Location",Toast.LENGTH_LONG).show();
        pDialog.dismiss();
    }
});
    }


    private void updateName() {
        User user= UserUtils.getUser(getActivity());
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        UserUtils.updateName(nameEdit.getText().toString(), user.getPhoneNumber(), new CompleteListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onComplete(String message) {
                user.setUserName(message);
                UserUtils.saveUser(user,getActivity());
                Toast.makeText(getActivity(),"Updated!",Toast.LENGTH_LONG).show();
                nameEdit.setText(message);
                pDialog.dismiss();

            }

            @Override
            public void onError(String message) {
                Toast.makeText(getActivity(),"Could Not Update Name",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });
    }

    private void editProfImage() {
        new GligarPicker().withFragment(AccountSettingsDialog.this).limit(1).requestCode(300).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Timber.e(data.getData().toString());
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return;
        }

        if (requestCode == 300) {

            String[] paths = data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT);
            if (paths.length>0) {
                String path = paths[0];
                new FileUtils().compressFile(path, getActivity(), new CompressListener() {
                    @Override
                    public void onComplete(Uri compressPath) {
                        User user= UserUtils.getUser(getActivity());
                        Dialog pDialog=new Dialog(getActivity());
                        pDialog.setContentView(R.layout.progress_dialog_transparent);
                        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        pDialog.setCancelable(false);
                        pDialog.show();
                        UserUtils.updateAvatar(new File(compressPath.getPath()), user.getPhoneNumber(), new CompleteListener() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onComplete(String message) {
user.setImageUrl(message);
UserUtils.saveUser(user,getActivity());
                                Toast.makeText(getActivity(),"Updated!",Toast.LENGTH_LONG).show();
                                avatarView.setImageURI(message);
                                pDialog.dismiss();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(getActivity(),"Could Not Update DP",Toast.LENGTH_LONG).show();
pDialog.dismiss();
                            }
                        });
                    }
                });

            }
        }

        if (requestCode==400){
            checkPermission();
        }

        if (requestCode==500 && data.getStringExtra(Constants.USER_LOCATION)!=null){
            userLocation=data.getStringExtra(Constants.USER_LOCATION);
            updateLocation();
        }

    }

}
