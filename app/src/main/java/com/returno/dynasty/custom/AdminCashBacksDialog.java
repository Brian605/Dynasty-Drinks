package com.returno.dynasty.custom;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.returno.dynasty.R;
import com.returno.dynasty.adapters.CashBackAdapter;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.callbacks.ClickListener;
import com.returno.dynasty.models.CashBack;
import com.returno.dynasty.utils.PostUtils;
import com.returno.dynasty.utils.UserUtils;

import java.util.List;

public class AdminCashBacksDialog extends DialogFragment {
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private CashBackAdapter adapter;
    private static List<CashBack> cashBackList;

    public static void showDialog(FragmentManager manager, List<CashBack> cashBacks){
        AdminCashBacksDialog dialog=new AdminCashBacksDialog();
        dialog.show(manager,"Credo CashBacks");
        cashBackList=cashBacks;
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
       View view=inflater.inflate(R.layout.orders_layout,container,false);
       toolbar=view.findViewById(R.id.toolbar);
       recyclerView =view.findViewById(R.id.listView);
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       recyclerView.setHasFixedSize(true);
       adapter=new CashBackAdapter(getActivity(), cashBackList, new ClickListener() {
           @Override
           public void onClick(String cashbackId) {
               Dialog pDialog = new Dialog(getActivity());
               pDialog.setContentView(R.layout.progress_dialog_transparent);
               pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               pDialog.setCancelable(false);
               pDialog.show();
               new UploadUtils().deleteCashBack(cashbackId, new CompleteListener() {
                   @Override
                   public void onComplete() {
                       Toast.makeText(getActivity(),"Delete",Toast.LENGTH_LONG).show();

                   }

                   @Override
                   public void onError(String message) {
                       pDialog.dismiss();
                       Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();


                   }
               });
           }
       });
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

        recyclerView.setAdapter(adapter);

        if (cashBackList.isEmpty()){
            MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity());
            builder.setPositiveButton("Ok", (dialog, which) -> {
dialog.dismiss();
            }).setMessage("You have no CashBacks. Buy More Drinks to get CashBacks");
builder.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
Dialog dialog=builder.create();
dialog.show();
        }

    }

}
