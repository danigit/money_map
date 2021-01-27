package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.Objects;


/**
 * Class that handles the login capability of the application
 */
public class Login extends AppCompatActivity implements OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private EditText email;
    private EditText password;
    private ProgressBar progressBar;
    private Button loginButton;

    @Override
    protected void onStart() {
        super.onStart();

        // here I sync the whole database, usually is not a good practice
        // I have to sync only the necessary things to work offline
        Utils.databaseReference.keepSynced(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // controlling if the use has an active internet connection
        if (!Utils.isConnectionActive(Login.this)){
            Utils.showWarnToast(Login.this, "Warning!!! The device is not connected, " +
                    "you have to have an internet connection to make the login.", Toast.LENGTH_LONG);
        }

        // connecting to firebase
        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.username_input_field_login);
        password = (EditText) findViewById(R.id.password_input_field_login);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        loginButton = (Button) findViewById(R.id.login_button);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        if ((firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) || GoogleSignIn.getLastSignedInAccount(this) != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            // finishing the activity so that I cannot go back to login if the user is
            // already logged
            finish();
        }

        // recover UI elements
        TextView googleLoginText = (TextView) findViewById(R.id.google_signin_label);
        ImageView googleLoginIcon = (ImageView) findViewById(R.id.google_login_image);
        TextView registerText = (TextView) findViewById(R.id.register_here_label);

        // adding event listeners
        loginButton.setOnClickListener(handleLoginUser);
        googleLoginText.setOnClickListener(handleLoginWithGoogle);
        googleLoginIcon.setOnClickListener(handleLoginWithGoogle);
        registerText.setOnClickListener(handleRegister);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.showErrorToast(Login.this, "Error: Failed to connect to google!", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> account = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(account);
        }
    }

    private final View.OnClickListener handleLoginUser = new View.OnClickListener() {
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
                            if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                // finishing the activity so that I cannot go back to login if the user is
                                // already logged
                                finish();
                            }else{
                                Utils.showWarnToast(Login.this, "Please verify your email first!", Toast.LENGTH_SHORT);

                                loginButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (task.getException() != null) {
                                Log.d(Utils.TAG, task.getException().getLocalizedMessage());
                                if (Objects.equals(task.getException().getLocalizedMessage(), "The email address is badly formatted."))
                                    email.setError("Email malformed or not registered");
                                else
                                    Utils.showErrorToast(Login.this, "Unable to reach the server! Control internet connection.", Toast.LENGTH_LONG);
                            }

                            loginButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        }
    };

    private final View.OnClickListener handleLoginWithGoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }
    };

    private final View.OnClickListener handleRegister = new View.OnClickListener() {
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
            Utils.showErrorToast(Login.this, "Error: Unable to sign in!", Toast.LENGTH_SHORT);
        }
    }
}