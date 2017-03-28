package com.example.dp863.crimespot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonback;
    private Button buttonReset;
    private EditText editTextEmail;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        buttonback = (Button) findViewById(R.id.buttonBack);
        buttonReset = (Button) findViewById(R.id.buttonResetPass);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonReset.setOnClickListener(this);
        buttonback.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

    }

    private void passReset() {
        String email = editTextEmail.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }


        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();


        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Start profile activity
                            Toast.makeText(ForgotPassActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();

                        } else {
                            //display some message here
                            Toast.makeText(ForgotPassActivity.this, "Failed to send reset email!", Toast.LENGTH_LONG).show();
                        }
                         progressDialog.dismiss();
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view == buttonReset) {
            passReset();
        }

        if (view == buttonback) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


    }

}