package com.example.suvajit.chatme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity{
    private ListView listUsers;
    private Firebase mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    List<String> userNames = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("database url");
        mRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    Log.e("User Values",postSnapshot.child("name").getValue().toString());
                    UserData userName = postSnapshot.getValue(UserData.class);
//                    Log.e("Post Data",userNames.getName());
                    userNames.add(userName.getName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        UsersActivity.this,android.R.layout.simple_list_item_1,userNames
                );

//                String[] items = { "Milk", "Butter", "Yogurt", "Toothpaste", "Ice Cream" };
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                        android.R.layout.simple_list_item_1, items);
                listUsers.setAdapter(arrayAdapter);

                for(String userName : userNames) {
                    Log.e("User names", userName);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase Error",firebaseError.getMessage());
            }
        });

    }
}
