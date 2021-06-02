package com.returno.dynasty.admin.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.adapters.MessagesAdapter;
import com.returno.dynasty.admin.listeners.CompleteListener;
import com.returno.dynasty.admin.listeners.DeleteListener;
import com.returno.dynasty.admin.listeners.SelectionListener;
import com.returno.dynasty.admin.models.Message;
import com.returno.dynasty.admin.utils.FetchUtils;
import com.returno.dynasty.admin.utils.UploadUtils;
import com.returno.dynasty.callbacks.FetchCallBacks;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private TextInputEditText messageInput;
    private MaterialButton sendButton;
    private MessagesAdapter adapter;
    private List<Message> messageList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        View root = inflater.inflate(R.layout.fragment_messages, container, false);
        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        MaterialButton newButton = root.findViewById(R.id.newMessage);
        RecyclerView messagesRecycler = root.findViewById(R.id.recycler);
        messageList=new ArrayList<>();

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Messages");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
                activity.recreate();

            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.new_message_dialog);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
                messageInput=dialog.findViewById(R.id.messageInput);
                sendButton=dialog.findViewById(R.id.sendMessage);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message=messageInput.getText().toString();
                        if (TextUtils.isEmpty(message)){
                            return;
                        }
                        sendMessage(message);
                    }
                });
                dialog.show();

            }
        });
        messagesRecycler.setHasFixedSize(true);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter=new MessagesAdapter(getActivity(), messageList, new SelectionListener() {
            @Override
            public void onSelect(Message message) {
deleteMessage(message);
            }


        });

        messagesRecycler.setAdapter(adapter);
        fetchMessages();

        return root;
    }

    private void deleteMessage(Message message) {
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getActivity());
        builder.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
        builder.setMessage("Sure to delete this message? Users will no longer see it. This action cannot be undone");
        builder.setPositiveButton("Delete", (dialogInterface, i) -> new UploadUtils().deleteAMessage(message.getMessageId(), new DeleteListener() {
            @Override
            public void onItemDeleted() {
                dialogInterface.dismiss();
                getActivity().recreate();
                Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(String message1) {
dialogInterface.dismiss();
                //Toast.makeText(getActivity(),"An Error Occurred",Toast.LENGTH_LONG).show();

            }
        }));
   Dialog dialog=builder.create();
   dialog.show();
    }

    private void fetchMessages() {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();
        new FetchUtils().getAllMessages(new FetchCallBacks() {
            @Override
            public void onError(String message) {
                pDialog.dismiss();
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFetchMessage(List<Message> messages) {
                pDialog.dismiss();
messageList.addAll(messages);
adapter.notifyDataSetChanged();
            }
        });
    }

    private void sendMessage(String message) {
        Dialog pDialog=new Dialog(getActivity());
        pDialog.setContentView(R.layout.progress_dialog);
        pDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.bg_white_round_corner));
        pDialog.setCancelable(false);
        pDialog.show();

        new UploadUtils().getApiKey(new CompleteListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onComplete(String key) {
                new UploadUtils().sendMessage(key,message, new CompleteListener() {
                    @Override
                    public void onComplete() {
                        pDialog.dismiss();
                        Toast.makeText(getActivity(),"Sent", Toast.LENGTH_LONG).show();
                        messageList.clear();
                        fetchMessages();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                });
            }
        });


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