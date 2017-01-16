package com.example.suvajit.chatme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    Button buttonRegister;
    private EditText editTextEmail, editTextPassword, editTextName;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = (Button) findViewById(R.id.buttonSignUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        progressDialog = new ProgressDialog(this);

        Firebase.setAndroidContext(this);
        mRef = new  Firebase("database url");
        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //After pressing Register button it will go to Register Function
                registerUser();
            }
        });

        //To enable registering from keyboard done button
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerUser();
                }
                return false;
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is signed in
                    Intent intent = new Intent(RegisterActivity.this,ChatActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    private void registerUser() {
        //Email and passwords are trimmed to avoid errors
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            //If Password is empty gives an error message
            editTextName.setError("Enter Name");
            editTextName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            //If Email is empty gives an error message
            editTextEmail.setError("Enter Email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //If Password is empty gives an error message
            editTextPassword.setError("Enter Password");
            editTextPassword.requestFocus();
            return;
        }

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering User");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Successfully Registered
                            String userId = mAuth.getCurrentUser().getUid();
                            Firebase usersRef = mRef.child("users");
                            Firebase uidRef = usersRef.child(userId);
                            uidRef.child("name").setValue(editTextName.getText().toString());

                            //also update profile
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editTextName.getText().toString())
                                    .build();
                            user.updateProfile(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(!task.isSuccessful()){
//                                        //Profile updated
//
//                                    }
                                }
                            });
                        } else {
                            //Error occurred during registration
                            Toast.makeText(RegisterActivity.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editTextPassword.setError(e.getMessage());
                                editTextPassword.requestFocus();
                            } catch (FirebaseAuthUserCollisionException | FirebaseAuthInvalidCredentialsException e) {
                                editTextEmail.setError(e.getMessage());
                                editTextEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(RegisterActivity.class.getName(), e.getMessage());
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