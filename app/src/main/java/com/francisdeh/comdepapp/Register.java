package com.francisdeh.comdepapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tapadoo.alerter.Alerter;

public class Register extends AppCompatActivity {
    TextView pageTitleText, bottomTextA, bottomTextB;
    Button loginBtn;
    EditText emailEditText, passwordEditText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        pageTitleText = (TextView)findViewById(R.id.textView);
        bottomTextA = (TextView)findViewById(R.id.comdep_text);
        bottomTextB = (TextView)findViewById(R.id.app_text);

        bottomTextA.setTypeface(Typeface.createFromAsset(getAssets(), "greecian.ttf"));
        bottomTextB.setTypeface(Typeface.createFromAsset(getAssets(), "greecian.ttf"));

        loginBtn = (Button)findViewById(R.id.loginBtn);

        emailEditText = (EditText)findViewById(R.id.email_edit_text);
        passwordEditText = (EditText)findViewById(R.id.password_edit_text);

        emailEditText.setText(getString(R.string.comdep_email));
        passwordEditText.setText(getString(R.string.comdep_password));

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateInputTextsAndAuthenticate();


            }
        });
    }

    public void setAdminEmailAndPassword(View view){
        emailEditText.setText(getString(R.string.comdep_admin_email));
        passwordEditText.setText(getString(R.string.comdep_admin_password));
    }

    public void validateInputTextsAndAuthenticate(){
        if(TextUtils.isEmpty(emailEditText.getText().toString()) || TextUtils.isEmpty(passwordEditText.getText().toString())){
            Alerter.create(Register.this)
                    .setTitle("Error:")
                    .setText("Fill all the form Fields.")
                    .setBackgroundColorInt(Color.RED)
                    .enableSwipeToDismiss()
                    .show();

            return;
        }

        //sign in with firebase
        //begin progress display
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    progressDialog.dismiss();

                    Intent intent = new Intent(Register.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                } else {
                    //check if the credentials match our set credentials, both admin and regular
                    //this is to ensure we don't do multiple user registrations
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    if((TextUtils.equals(email, getString(R.string.comdep_email)) && TextUtils.equals(password, getString(R.string.comdep_password))) || (TextUtils.equals(email, getString(R.string.comdep_admin_email)) && TextUtils.equals(password, getString(R.string.comdep_admin_password)))){
                        //register user
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();

                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressDialog.dismiss();
                                //alert user
                                Alerter.create(Register.this)
                                        .setTitle("Error:")
                                        .setText("Please re-try login attempt. "+ e.getMessage())
                                        .setBackgroundColorInt(Color.RED)
                                        .enableSwipeToDismiss()
                                        .show();
                            }
                        });

                    } else {
                        progressDialog.dismiss();

                        Alerter.create(Register.this)
                                .setTitle("Error:")
                                .setText("Credentials do not match. Re-enter credentials or re-start app.")
                                .setBackgroundColorInt(Color.RED)
                                .enableSwipeToDismiss()
                                .show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();

                Alerter.create(Register.this)
                        .setTitle("Error:")
                        .setText("Please re-try login attempt. " + e.getMessage())
                        .setBackgroundColorInt(Color.RED)
                        .enableSwipeToDismiss()
                        .show();
            }
        });


    }



}
