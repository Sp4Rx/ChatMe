package com.example.suvajit.chatme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatActivity extends AppCompatActivity {
    Button sendButton, signOutButton;
    ListView messageList;
    TextView currentUserTextView;
    private EditText messageText;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = (Button) findViewById(R.id.buttonSend);
        signOutButton = (Button) findViewById(R.id.buttonSignOut);
        messageText = (EditText) findViewById(R.id.messageText);
        messageList = (ListView) findViewById(R.id.messagesList);
        currentUserTextView = (TextView) findViewById(R.id.textViewCurrentUser);
        mAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        final Firebase mRef = new Firebase("database url");

        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage;

                chatMessage = new ChatMessage(user.getDisplayName(), messageText.getText().toString());

                mRef.push().setValue(chatMessage);

                messageText.setText("");

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });


        Query recent = mRef.limitToLast(50);
        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(
                this, ChatMessage.class, android.R.layout.two_line_list_item, recent
        ) {
            @Override
            protected void populateView(View view, ChatMessage chatMessage, int i) {
                ((TextView) view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.WHITE);
                ((TextView) view.findViewById(android.R.id.text2)).setText(chatMessage.getMessage());
                ((TextView) view.findViewById(android.R.id.text2)).setTextColor(Color.WHITE);
            }
        };
        messageList.setAdapter(adapter);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is Signed In
                  currentUserTextView.setText(user.getDisplayName());
                }else{
                    //User Signed Out
                    finish();  //end this activity(ChatActivity)
                }
            }
        };

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0)
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
