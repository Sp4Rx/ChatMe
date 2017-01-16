package com.example.suvajit.chatme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    EditText editTextEmail,editTextPassword;
    TextView textViewRegister;
    Button buttonSignIn;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = (EditText) findViewById(R.id.editTextEmailSignIn);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordSignIn);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //Listener to check user login status
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    signInUser();
                }
                return false;
            }
        });

        //User sign in process
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

    }

    private void signInUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Enter Email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter Password");
            editTextPassword.requestFocus();
            return;
        }
        progressDialog.setTitle(("Please wait"));
        progressDialog.setMessage(("Signing in"));
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
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
}
