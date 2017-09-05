package com.sindj.findmyfriends.chat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sindj.findmyfriends.R;

import java.util.List;

public class ChatActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    public static final String MESSAGES_CHILD = "messages";
    private static final String TAG = "ChatActivity";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String CHAT_KEY = null;

    private String mGroupKey;
    private String mUserKey;
    private String mUsername;
    private String m2Username;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<FriendlyMessage, PrivateMessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        mGroupKey = intent.getStringExtra("groupKey");
        mUserKey = intent.getStringExtra("userKey");
        mUsername = intent.getStringExtra("userName");
        setTitle(intent.getStringExtra("groupName") + " chat");

        CHAT_KEY = "groups/" + mGroupKey + "/chat";
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, PrivateMessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_private_message_receive,
                PrivateMessageViewHolder.class,
                mFirebaseDatabaseReference.child(CHAT_KEY).limitToFirst(100)) {
            @Override
            public PrivateMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v;
                if (viewType == 1) { //sender
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_sent, parent, false);
                } else              //receive
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_receive, parent, false);
                return new PrivateMessageViewHolder(v);
            }

            @Override
            public void onBindViewHolder(PrivateMessageViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
            }

            @Override
            public int getItemViewType(int position) {

                if (mUserKey.equals(getItem(position).getKey()))
                    return 1; //sender
                else
                    return 2; //receive
            }

            @Override
            protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {
                return super.parseSnapshot(snapshot);
            }

            @Override
            public FriendlyMessage getItem(int position) {
                return super.getItem(position);
            }

            @Override
            protected void populateViewHolder(final PrivateMessageViewHolder viewHolder, final FriendlyMessage friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
/*                final DatabaseReference messageRef = getRef(position);
                final String messageKey = messageRef.getKey();*/
                viewHolder.bindToPost(friendlyMessage, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // showMessageClickedList(friendlyMessage);

                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FriendlyMessage friendlyMessage = new FriendlyMessage(mUsername, mUserKey, mMessageEditText.getText().toString().trim(), System.currentTimeMillis());
/*                try {
                    String sender = URLEncoder.encode(friendlyMessage.getDisplayName() + ": ", "UTF-8");
                    String message = URLEncoder.encode(friendlyMessage.getText(), "UTF-8");
                    String FCMurl = "https://sendfcmmessage.000webhostapp.com/sendFCM.php?message=" + sender + message + "&title=message&type=normal&uid=" + mUid + "&id=" + m2Token;
                    new SendFCMTask().execute(FCMurl);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                mFirebaseDatabaseReference.child(CHAT_KEY).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });

        mFirebaseDatabaseReference.child(CHAT_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override

            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMessageClickedList(final FriendlyMessage friendlyMessage) {
        final CharSequence[] items;
        items = new CharSequence[]{"Copy message"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(friendlyMessage.getDisplayName() + ": " + friendlyMessage.getText());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("NativesNearby", friendlyMessage.getText());
                        clipboard.setPrimaryClip(clip);
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
