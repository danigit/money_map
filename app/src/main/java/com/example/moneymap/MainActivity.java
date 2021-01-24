package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.moneymap.fragments.AccountFragment;
import com.example.moneymap.fragments.OverviewFragment;
import com.example.moneymap.fragments.TransactionsFragment;
import com.example.moneymap.fragments.UserFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

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

//        Utils.database.setPersistenceEnabled(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);

        user_fragment = new UserFragment();
        account_fragment = new AccountFragment();
        transactions_fragment = new TransactionsFragment();

        overview_fragment = new OverviewFragment();

        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        changeFragment(transactions_fragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(handleFragments);

        // TODO this is temporary
        Button logout = (Button) findViewById(R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();

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
                case R.id.add_transaction:
                    addTransaction();
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

    public void addTransaction(){
        SlidingUpPanelLayout slidingLayout;
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        Utils.databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final List<String> categoriesList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: snapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.getValue(String.class);
                    if (propertyAddress!=null){
                        categoriesList.add(propertyAddress);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.categories_spinner);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, categoriesList);
                addressAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerProperty.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Utils.databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final List<String> accountsList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: snapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.child("title").getValue(String.class);
                    if (propertyAddress!=null){
                        accountsList.add(propertyAddress);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.account_spinner);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, accountsList);
                addressAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerProperty.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}