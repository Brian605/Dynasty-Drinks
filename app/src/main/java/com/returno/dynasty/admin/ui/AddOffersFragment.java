package com.returno.dynasty.admin.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.opensooq.supernova.gligar.GligarPicker;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.listeners.CompressListener;
import com.returno.dynasty.admin.listeners.UploadListener;
import com.returno.dynasty.admin.utils.FileUtils;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.models.Offer;

import timber.log.Timber;


public class AddOffersFragment extends Fragment {

    private ImageView imageView;
private TextView selectView;
private TextInputEditText titleInput,prevInput,currInput;
private final int GALLERY_REQUEST_CODE=300;
private Uri filePath;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
         View root = inflater.inflate(R.layout.fragment_post, container, false);
         imageView=root.findViewById(R.id.imageBtn);
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
         selectView=root.findViewById(R.id.textview);
         titleInput=root.findViewById(R.id.titleInput);
         prevInput=root.findViewById(R.id.prePriceInput);
         currInput=root.findViewById(R.id.currPriceInput);
        MaterialButton categoriesButton = root.findViewById(R.id.addCategory);
        MaterialButton postButton = root.findViewById(R.id.postBtn);

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Add Offers");
         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Timber.e("clicked");
                 activity.onBackPressed();
                 activity.recreate();

             }
         });


        categoriesButton.setVisibility(View.GONE);
        //<editor-fold defaultstate="collapsed" desc="picking image from gallery">
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GligarPicker().requestCode(GALLERY_REQUEST_CODE).withFragment(AddOffersFragment.this).limit(1).show();
            }
        });
        // </editor-fold>

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPrice=currInput.getText().toString();
                String prevPrice=prevInput.getText().toString();
                String title=titleInput.getText().toString();

                if (TextUtils.isEmpty(title)){
                    titleInput.setError("Input the title or description");
                    titleInput.requestFocus();
                    return;
                }
                if (title.length()>50){
                    titleInput.setError("Use up to 50 characters for title");
                    titleInput.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(prevPrice)){
                    prevInput.setError("Input the previous Price");
                    prevInput.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(currentPrice)){
                    currInput.setError("Input the Current Price");
                    currInput.requestFocus();
                    return;
                }
                if (Integer.parseInt(prevPrice)<Integer.parseInt(currentPrice)){
                    prevInput.setError("The previous price cannot be lower than the current price");
                    prevInput.requestFocus();
                    return;
                }

                if (filePath==null){
                    Snackbar.make(imageView,"Please Select an Image for this item",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.black))
                            .setTextColor(getResources().getColor(R.color.orange)).show();
                    return;
                }

                Dialog pDialog=new Dialog(getActivity());
                pDialog.setContentView(R.layout.progress_dialog);
                pDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_round_corner);
                pDialog.setCancelable(false);
                pDialog.show();

                Offer offer=new Offer(title,filePath.toString(),Integer.parseInt(prevPrice),Integer.parseInt(currentPrice));
                new UploadUtils().uploadOffers(offer, new UploadListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getActivity(),"Posted Successfully", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                        titleInput.setText("");
                        prevInput.setText("");
                        currInput.setText("");
                        imageView.setImageResource(R.drawable.ic_baseline_add_to_photos_24);
                    }

                    @Override
                    public void onError(String message) {
pDialog.dismiss();
                    }
                });
            }
        });


        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Timber.e(data.getData().toString());
        if (resultCode != AppCompatActivity.RESULT_OK) {
            Timber.e(String.valueOf(requestCode));
            return;
        }

        if (requestCode == GALLERY_REQUEST_CODE) {

            String[] paths = data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT);
           if (paths.length>0) {
               String path = paths[0];
               Timber.e(path);
               new FileUtils().compressFile(path, getActivity(), new CompressListener() {
                   @Override
                   public void onComplete(Uri compressPath) {
                     imageView.setImageURI(compressPath);
                     filePath=compressPath;
                     selectView.setVisibility(View.GONE);
                   }
               });

           }
        }

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item=menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

    }
}