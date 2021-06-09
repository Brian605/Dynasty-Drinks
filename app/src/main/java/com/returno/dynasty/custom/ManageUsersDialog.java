package com.returno.dynasty.custom;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.adapters.UsersAdapter;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.models.User;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.utils.UserUtils;

import java.util.List;

public class ManageUsersDialog extends DialogFragment {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private static List<User> userList;

    public static void showDialog(FragmentManager manager, List<User> users){
        ManageUsersDialog dialog=new ManageUsersDialog();
        dialog.show(manager,"Manage Users");
        userList=users;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_LiquorStore_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!UserUtils.getAuthStatus(getActivity())){
            Toast.makeText(getActivity(),"You need to log in First. Click on Account to Log in",Toast.LENGTH_LONG).show();
        dismiss();
        }

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
       View view=inflater.inflate(R.layout.users_layout,container,false);
       toolbar=view.findViewById(R.id.toolbar);
       recyclerView =view.findViewById(R.id.listView);

       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       recyclerView.setHasFixedSize(true);
       adapter=new UsersAdapter(getActivity(), userList, new ClickListener() {
           @Override
           public void onClick(View optionsView,String userId) {
               PopupMenu popupMenu=new PopupMenu(getActivity(),optionsView);
               popupMenu.getMenuInflater().inflate(R.menu.user_options_pop_menu,popupMenu.getMenu());
               popupMenu.setGravity(Gravity.BOTTOM);

               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem item) {
                       if (item.getItemId()==R.id.delete){
                           popupMenu.dismiss();
                           deleteUser(userId);
                       }
                       if (item.getItemId()==R.id.cashbacks){
                           popupMenu.dismiss();
                           giveCashBack(userId);
                       }
                       return false;
                   }
               });

               popupMenu.show();

           }
       });
       return view;

    }

    private void giveCashBack(String userId) {
        Dialog dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.single_input);
        AppBarLayout toolbar=dialog.findViewById(R.id.appBar);
        TextInputEditText amtInput=dialog.findViewById(R.id.input);
        MaterialButton btnContinue=dialog.findViewById(R.id.btnContinue);
        toolbar.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(amtInput.getText().toString())){
                    dialog.dismiss();
                    Dialog pDialog = new Dialog(getActivity());
                    pDialog.setContentView(R.layout.progress_dialog_transparent);
                    pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    pDialog.setCancelable(false);
                    pDialog.show();
new UploadUtils().giveCashBack(userId, amtInput.getText().toString(), new CompleteListener() {
    @Override
    public void onComplete() {
        pDialog.dismiss();
        Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onError(String message) {
        pDialog.dismiss();
        Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();

    }
});
                }
            }
        });
dialog.show();

    }

    private void deleteUser(String userId) {
        Dialog pDialog = new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        new UploadUtils().deleteUser(userId, new CompleteListener() {
            @Override
            public void onComplete() {
                pDialog.dismiss();
                Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String message) {
              pDialog.dismiss();
                Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();

            }
        });
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

        recyclerView.setAdapter(adapter);

        if (userList.isEmpty()){
            dismiss();
            MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity());
            builder.setPositiveButton("Ok", (dialog, which) -> {
dialog.dismiss();
            }).setMessage("You No Users Found");
builder.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
Dialog dialog=builder.create();
dialog.show();
        }

    }

}
