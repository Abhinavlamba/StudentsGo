package com.travis.android.studentsgo.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.travis.android.studentsgo.R;

import java.util.concurrent.TimeUnit;

public class Signinactivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    EditText editText1, editText2, editText3;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinactivity);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.submit);
        editText1 = findViewById(R.id.name);
        editText2 = findViewById(R.id.phone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText2.getText().toString().isEmpty()){
                    Toast.makeText(Signinactivity.this,"Please enter your phone no.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String mobile = editText2.getText().toString().trim();
//                sendverficationcode(editText2.getText().toString());
                Log.i("hg",mobile);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+mobile,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        Signinactivity.this,               // Activity (for callback binding)
                        mCallbacks);
            }
        });
        // OnVerificationStateChangedCallbacks
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }
        };


    }
    //    public void sendverficationcode(String mobile){
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+91"+mobile,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
//    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(Signinactivity.this,"You have been successfully verified",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            // ...
                            startActivity(new Intent(Signinactivity.this, BranchActivity.class));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            }
                            else {
                                finish();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Signinactivity.this,"You have not been verified",Toast.LENGTH_SHORT).show();

//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}

