package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity implements OnConnectionFailedListener {

    private static final String TAG = "money_map";
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth firebaseAuth;
    private EditText email;
    private EditText password;
    private ProgressBar progressBar;
    private Button loginButton;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView googleLoginText = (TextView) findViewById(R.id.google_signin_label);
        TextView registerText = (TextView) findViewById(R.id.register_here_label);

        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.username_input_field_login);
        password = (EditText) findViewById(R.id.password_input_field_login);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        loginButton = (Button) findViewById(R.id.login_button);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        if (firebaseAuth.getCurrentUser() != null || GoogleSignIn.getLastSignedInAccount(this) != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            // finishing the activity so that I cannot go back to login if the user is
            // already logged
            finish();
        }

        loginButton.setOnClickListener(handleLoginUser);
        googleLoginText.setOnClickListener(handleLoginWithGoogle);
        registerText.setOnClickListener(handleRegister);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Login.this, "Error: Failed to connect to google!", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> account = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(account);
        }
    }

    private View.OnClickListener handleLoginUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String emailString = email.getText().toString().trim();
            String passwordString = password.getText().toString().trim();

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

            loginButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            // finishing the activity so that I cannot go back to login if the user is
                            // already logged
                            finish();
                        } else {
                            if (task.getException() != null) {
                                Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }

                            loginButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        }
    };

    private View.OnClickListener handleLoginWithGoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
    };

    private View.OnClickListener handleRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), Register.class));
        }
    };

    public void handleSignInResult(Task<GoogleSignInAccount> result){
        if (result != null && result.isSuccessful()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(Login.this, "Error: Unable to sign in!", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}