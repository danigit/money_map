package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private TextView loginLabel;
    private ProgressBar progressBar;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.username_input_field_register);
        password = (EditText) findViewById(R.id.password_input_field_register);
        confirmPassword = (EditText) findViewById(R.id.password_reinsert_input_field);
        loginLabel = (TextView) findViewById(R.id.go_to_login_label);
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
        registerButton = (Button) findViewById(R.id.register_button);

        registerButton.setOnClickListener(handleRegisterUser);
        loginLabel.setOnClickListener(goToLogin);
    }

    private View.OnClickListener handleRegisterUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailString = email.getText().toString().trim();
            String passwordString = password.getText().toString().trim();
            String confirmPasswordString = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(emailString)){
                email.setError("Insert an email address");
                return;
            }

            if (TextUtils.isEmpty(passwordString)){
                password.setError("Insert a password");
                return;
            }

            if (!Utils.validatePassword(passwordString)){
                password.setError("At least 6 character (upper/lower letters and numbers)");
                return;
            }

            if (TextUtils.isEmpty(confirmPasswordString)){
                password.setError("Reinsert a password");
                return;
            }

            if (!passwordString.equals(confirmPasswordString)){
                password.setError("Passwords have to match");
                return;
            }

            registerButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            // finishing the activity so that I cannot go back to register if the user is
                            // already logged
                            finish();
                        } else {
                            if (task.getException() != null) {
                                Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }

                            registerButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        }
    };

    private View.OnClickListener goToLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}