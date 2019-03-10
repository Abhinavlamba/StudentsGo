package com.travis.android.studentsgo.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.travis.android.studentsgo.R;


public class LoginActivity extends AppCompatActivity {
    private Button button,button1;
    int a=0;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.signup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,Signinactivity.class));

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(LoginActivity.this, PdfPicker.class));
            finish();
        }
    }
}
