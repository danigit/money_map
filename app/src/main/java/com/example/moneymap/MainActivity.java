package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.firebase.database.DatabaseReference;
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
    private String transactionAmountString = "";
    private Button zeroButton;
    private Button oneButton;
    private Button twoButton;
    private Button threeButton;
    private Button fourButton;
    private Button fiveButton;
    private Button sixButton;
    private Button sevenButton;
    private Button eightButton;
    private Button nineButton;
    private Button commaButton;
    private Button deleteNumberButton;
    private Button insertTransactionButton;
    private Spinner accountSpinner;
    private Spinner categoriesSpinner;
    private String transactionAccount;
    private String transactionCategory;
    private TextView amountTextView;
    private EditText transactionNote;


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

        zeroButton = (Button) findViewById(R.id.zero_button);
        oneButton = (Button) findViewById(R.id.one_button);
        twoButton = (Button) findViewById(R.id.two_button);
        threeButton = (Button) findViewById(R.id.three_button);
        fourButton = (Button) findViewById(R.id.four_button);
        fiveButton = (Button) findViewById(R.id.five_button);
        sixButton = (Button) findViewById(R.id.six_button);
        sevenButton = (Button) findViewById(R.id.seven_button);
        eightButton = (Button) findViewById(R.id.eight_button);
        nineButton = (Button) findViewById(R.id.nine_button);
        commaButton = (Button) findViewById(R.id.comma_button);
        deleteNumberButton = (Button) findViewById(R.id.cancel_number_button);
        insertTransactionButton = (Button) findViewById(R.id.insert_transaction_button);

        amountTextView = (TextView) findViewById(R.id.transaction_amount_text_view);

        zeroButton.setOnClickListener(getTransactionInput());
        oneButton.setOnClickListener(getTransactionInput());
        twoButton.setOnClickListener(getTransactionInput());
        threeButton.setOnClickListener(getTransactionInput());
        fourButton.setOnClickListener(getTransactionInput());
        fiveButton.setOnClickListener(getTransactionInput());
        sixButton.setOnClickListener(getTransactionInput());
        sevenButton.setOnClickListener(getTransactionInput());
        eightButton.setOnClickListener(getTransactionInput());
        nineButton.setOnClickListener(getTransactionInput());
        commaButton.setOnClickListener(getTransactionInput());
        deleteNumberButton.setOnClickListener(getTransactionInput());
        insertTransactionButton.setOnClickListener(getTransactionInput());
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

    public View.OnClickListener getTransactionInput(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int buttonId = v.getId();
                Log.d(Utils.TAG, "" + buttonId);
                switch (buttonId){
                    case R.id.zero_button:
                        if (transactionAmountString.charAt(0) != '0' && transactionAmountString.charAt(0) != ',') {
                            transactionAmountString += "0";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.one_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "1";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.two_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "2";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.three_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "3";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.four_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "4";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.five_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "5";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.six_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "6";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.seven_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "7";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.eight_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "8";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.nine_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += "9";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.comma_button:
                        if (validAmount(transactionAmountString)) {
                            transactionAmountString += ",";
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.cancel_number_button:
                        Log.d(Utils.TAG, "cancelling the number");
                        transactionAmountString = transactionAmountString.substring(0, transactionAmountString.length() - 1);
                        amountTextView.setText(transactionAmountString);
                        break;
                    case R.id.insert_transaction_button:
                        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
                        categoriesSpinner = (Spinner) findViewById(R.id.categories_spinner);
                        transactionNote = (EditText) findViewById(R.id.transaction_note_input2);


                        String account = accountSpinner.getSelectedItem().toString();
                        String category = categoriesSpinner.getSelectedItem().toString();
                        String note = transactionNote.getText().toString();

                        if (transactionAmountString.charAt(transactionAmountString.length() - 1) == ','){
                            transactionAmountString = transactionAccount.substring(0, transactionAccount.length() - 1);
                        }

                        Transaction transaction = new Transaction(account, category, note, transactionAmountString);
                        DatabaseReference transactionsReference = Utils.databaseReference.child("transactions");
                        String key = transactionsReference.push().getKey();
                        transactionsReference.child(key).setValue(transaction);
                        break;
                }
            }
        };
    }

    public boolean validAmount(String amount){
        int numberOfCommas = 0;
        if (amount != null) {
            numberOfCommas = amount.replaceAll("[^,]", "").length();
        }

        return numberOfCommas <= 1;
    }
}