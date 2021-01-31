package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.fragments.AccountFragment;
import com.example.moneymap.fragments.CategoriesFragment;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private UserFragment user_fragment;
    private AccountFragment accountFragment;
    private CategoriesFragment categoriesFragment;
    private TransactionsFragment transactionsFragment;
    private OverviewFragment overviewFragment;
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
    private TextView closePanelButton;
    private Button insertTransactionButton;
    private Spinner accountSpinner;
    private Spinner categoriesSpinner;
    private String transactionAccount;
    private String transactionCategory;
    private TextView amountTextView;
    private EditText transactionNote;
    private SlidingUpPanelLayout slidingLayout;
    private RadioGroup incomeOutcomeRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);

//        user_fragment = new UserFragment();
        accountFragment = new AccountFragment();
        categoriesFragment = new CategoriesFragment();
        transactionsFragment = new TransactionsFragment();

        overviewFragment = new OverviewFragment();

        bottomNavigationView.getMenu().getItem(4).setChecked(true);
        changeFragment(transactionsFragment);

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
        closePanelButton = (TextView) findViewById(R.id.cancel_transaction_button);
        deleteNumberButton = (Button) findViewById(R.id.cancel_number_button);
        insertTransactionButton = (Button) findViewById(R.id.insert_transaction_button);
        incomeOutcomeRadioGroup = (RadioGroup) findViewById(R.id.income_outcome_radio_group);

        amountTextView = (TextView) findViewById(R.id.transaction_amount_text_view);
        transactionNote = (EditText) findViewById(R.id.transaction_note_input2);

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
        closePanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
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
//                case R.id.user_settings:
//                    changeFragment(user_fragment);
//                    break;
                case R.id.account_management:
                    changeFragment(accountFragment);
                    break;
                case R.id.categories:
                    changeFragment(categoriesFragment);
                    break;
                case R.id.add_transaction:
                    addTransaction();
                    break;
                case R.id.transactions:
                    changeFragment(transactionsFragment);
                    break;
                case R.id.overview:
                    changeFragment(overviewFragment);
                    break;
            }
            return true;
        }
    };

    public void addTransaction(){

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.getChildAt(1).setOnClickListener(null);
        transactionAmountString = "";
        transactionNote.setText("");

        Utils.databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final List<String> categoriesList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: snapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.child("name").getValue(String.class);
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
                String tempAmount = transactionAmountString;
                switch (buttonId){
                    case R.id.zero_button:
                        tempAmount += "0";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.one_button:
                        tempAmount += "1";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.two_button:
                        tempAmount += "2";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.three_button:
                        tempAmount += "3";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.four_button:
                        tempAmount += "4";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.five_button:
                        tempAmount += "5";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.six_button:
                        tempAmount += "6";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.seven_button:
                        tempAmount += "7";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.eight_button:
                        tempAmount += "8";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.nine_button:
                        tempAmount += "9";
                        if (validAmount(tempAmount)) {
                            transactionAmountString = tempAmount;
                            amountTextView.setText(transactionAmountString);
                        }
                        break;
                    case R.id.comma_button:
                        tempAmount += ".0";
                        if (validAmount(tempAmount)) {
                            transactionAmountString += ".";
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
                        int checkedButtonSelectedId = incomeOutcomeRadioGroup.getCheckedRadioButtonId();
                        Button checkedButton = (Button) findViewById(checkedButtonSelectedId);

                        String account = accountSpinner.getSelectedItem().toString();
                        String category = categoriesSpinner.getSelectedItem().toString();
                        String note = transactionNote.getText().toString();
                        String type = checkedButton.getText().toString();

                        if (!transactionAmountString.equals("")) {
                            Date currentTime = Calendar.getInstance().getTime();
                            String day = (String) DateFormat.format("EEEE", currentTime);
                            String dayNumber = (String) DateFormat.format("dd", currentTime);
                            String month = (String) DateFormat.format("MMMM", currentTime);
                            String year = (String) DateFormat.format("yyyy", currentTime);
                            String hours = (String) DateFormat.format("HH", currentTime);
                            String minutes = (String) DateFormat.format("mm", currentTime);
                            String seconds = (String) DateFormat.format("ss", currentTime);

                            String transactionKey = day + dayNumber + month + year + "-" + hours + minutes + seconds;
                            TransactionDate transactionDate = new TransactionDate(day, dayNumber, month, year);
                            Transaction transaction = new Transaction(account, category, note, transactionAmountString, type.toLowerCase(), transactionDate);
                            DatabaseReference transactionsReference = Utils.databaseReference.child("transactions");
                            transactionsReference.child(transactionKey).setValue(transaction);

                            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        } else {
                            Utils.showToast(v.getContext(), "Insert an amount", Toast.LENGTH_SHORT);
                        }
                        break;
                }
            }
        };
    }

    public boolean validAmount(String amount){
        boolean match = false;
        if (amount != null) {
            String decimalPattern = "(^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$)";
            match = Pattern.matches(decimalPattern, amount);
        }

        return match;
    }
}