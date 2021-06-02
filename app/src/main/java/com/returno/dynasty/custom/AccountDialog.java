package com.returno.dynasty.custom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.returno.dynasty.R;
import com.returno.dynasty.activities.AddFundsActivity;

import timber.log.Timber;

public class AccountDialog extends DialogFragment  {
    private MaterialToolbar toolbar;
    private MaterialTextView balanceView;
    private MaterialButton addFundsButton;
    private static String balance;


    public static void showDialog(FragmentManager manager, String balanc){
        AccountDialog dialog=new AccountDialog();
        dialog.show(manager,"User Account");
        balance=balanc;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
       View view=inflater.inflate(R.layout.account_layout,container,false);
       toolbar=view.findViewById(R.id.toolbar);
       balanceView=view.findViewById(R.id.balance);
       addFundsButton=view.findViewById(R.id.add_funds);
       Timber.e(balance);
       return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        Timber.e(AccountDialog.balance);
        balanceView.setText(String.format("Ksh.%s", balance));

        addFundsButton.setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), AddFundsActivity.class);
            startActivity(intent);

        });


    }



   }
