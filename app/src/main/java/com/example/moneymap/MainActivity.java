package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.moneymap.fragments.AccountFragment;
import com.example.moneymap.fragments.OverviewFragment;
import com.example.moneymap.fragments.TransactionsFragment;
import com.example.moneymap.fragments.UserFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private UserFragment user_fragment;
    private AccountFragment account_fragment;
    private TransactionsFragment transactions_fragment;
    private OverviewFragment overview_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_fragment = new UserFragment();
        account_fragment = new AccountFragment();
        transactions_fragment = new TransactionsFragment();
        overview_fragment = new OverviewFragment();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);

        changeFragment(user_fragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(handleFragments);

        Button logout = (Button) findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();

        // this is temporary
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // here I'm not doing any control, I just want to logout
                firebaseAuth.signOut();
                googleSignInClient.signOut();
                finish();
            }
        });
    }

    public void changeFragment(Fragment fragment){
       getSupportFragmentManager().beginTransaction()
               .replace(R.id.fl_wrapper, fragment, null)
               .commit();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener handleFragments = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.user_settings:
                    changeFragment(user_fragment);
                    break;
                case R.id.account_management:
                    changeFragment(account_fragment);
                    break;
                case R.id.transactions:
                    changeFragment(transactions_fragment);
                    break;
                case R.id.overview:
                    changeFragment(overview_fragment);
                    break;
            }
            return true;
        }
    };
}