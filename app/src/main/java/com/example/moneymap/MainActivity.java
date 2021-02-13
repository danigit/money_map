package com.example.moneymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.fragments.AccountFragment;
import com.example.moneymap.fragments.CategoriesFragment;
import com.example.moneymap.fragments.OverviewFragment;
import com.example.moneymap.fragments.TransactionsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PICTURE = 0;

    private AccountFragment accountFragment;
    private CategoriesFragment categoriesFragment;
    private TransactionsFragment transactionsFragment;
    private OverviewFragment overviewFragment;
    private String transactionAmountString;
    private String applicationPath;
    private TextView amountTextView;
    private Spinner categoriesSpinner;
    private EditText transactionNote;
    private SlidingUpPanelLayout slidingLayout;
    private RadioGroup incomeOutcomeRadioGroup;
    private ImageView userImage;
    private DrawerLayout sideMenuDrawer;
    private NavigationView sideMenuView;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountFragment = new AccountFragment();
        categoriesFragment = new CategoriesFragment();
        transactionsFragment = new TransactionsFragment();
        overviewFragment = new OverviewFragment();

        sideMenuDrawer = (DrawerLayout) findViewById(R.id.side_menu_drawer);
        amountTextView = (TextView) findViewById(R.id.transaction_amount_text_view);
        transactionNote = (EditText) findViewById(R.id.transaction_note_input2);
        incomeOutcomeRadioGroup = (RadioGroup) findViewById(R.id.income_outcome_radio_group);
        sideMenuView = (NavigationView) findViewById(R.id.side_menu);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);

        ImageView changeProfileImage = (ImageView) sideMenuView.getHeaderView(0).findViewById(R.id.take_profile_image_button);
        Button insertTransactionButton = (Button) findViewById(R.id.insert_transaction_button);
        Button zeroButton = (Button) findViewById(R.id.zero_button);
        Button oneButton = (Button) findViewById(R.id.one_button);
        Button twoButton = (Button) findViewById(R.id.two_button);
        Button threeButton = (Button) findViewById(R.id.three_button);
        Button fourButton = (Button) findViewById(R.id.four_button);
        Button fiveButton = (Button) findViewById(R.id.five_button);
        Button sixButton = (Button) findViewById(R.id.six_button);
        Button sevenButton = (Button) findViewById(R.id.seven_button);
        Button eightButton = (Button) findViewById(R.id.eight_button);
        Button nineButton = (Button) findViewById(R.id.nine_button);
        Button commaButton = (Button) findViewById(R.id.comma_button);
        Button deleteNumberButton = (Button) findViewById(R.id.cancel_number_button);
        ImageView closePanelButton = (ImageView) findViewById(R.id.cancel_transaction_button);

        // event listeners
        zeroButton.setOnClickListener(getTransactionInput);
        oneButton.setOnClickListener(getTransactionInput);
        twoButton.setOnClickListener(getTransactionInput);
        threeButton.setOnClickListener(getTransactionInput);
        fourButton.setOnClickListener(getTransactionInput);
        fiveButton.setOnClickListener(getTransactionInput);
        sixButton.setOnClickListener(getTransactionInput);
        sevenButton.setOnClickListener(getTransactionInput);
        eightButton.setOnClickListener(getTransactionInput);
        nineButton.setOnClickListener(getTransactionInput);
        commaButton.setOnClickListener(getTransactionInput);
        deleteNumberButton.setOnClickListener(getTransactionInput);
        changeProfileImage.setOnClickListener(handleTakePhoto);
        insertTransactionButton.setOnClickListener(getTransactionInput);
        closePanelButton.setOnClickListener(closePanel);
        sideMenuView.setNavigationItemSelectedListener(handleLeftMenuFragments);
        bottomNavigationView.setOnNavigationItemSelectedListener(handleFragments);
        incomeOutcomeRadioGroup.setOnCheckedChangeListener(incomeOutcomeHandler);

        applicationPath = getApplicationInfo().dataDir + "/app_" + Utils.applicationDirectory + "/";
        userImage = (ImageView) sideMenuView.getHeaderView(0).findViewById(R.id.profile_image);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        changeFragment(transactionsFragment);
        setUserImage();
        logout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (requestCode == CAMERA_PICTURE){
            if (resultCode == Activity.RESULT_OK){
                final Bundle cameraImage = result.getExtras();
                if (cameraImage != null) {
                    final Bitmap imageBitmap = (Bitmap) cameraImage.get("data");

                    if (imageBitmap != null) {
                        Bitmap newBitmap = preprocessImage(imageBitmap);
                        userImage.setImageBitmap(newBitmap);
                        boolean imageSaved = saveUserImage(newBitmap);

                        if (imageSaved){
                            Utils.showToast(getApplicationContext(), "Image saved to the device!", Toast.LENGTH_SHORT);
                        } else {
                            Utils.showToast(getApplicationContext(), "Could not save the image!", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Utils.showToast(getApplicationContext(), "Could not take the picture!", Toast.LENGTH_SHORT);
                    }
                } else{
                    Utils.showToast(getApplicationContext(), "Could not take the picture!", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        SlidingUpPanelLayout slidingLayoutAccount = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_account);
        SlidingUpPanelLayout slidingLayoutCategories = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout_categories);

        if(slidingLayout != null && slidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (this.accountFragment.isVisible() && isPanelOpen(slidingLayoutAccount)){
            ImageView addAccountButton = (ImageView) findViewById(R.id.add_account_image_button);
            Utils.closePanel(slidingLayoutAccount, addAccountButton);
        } else if (this.categoriesFragment.isVisible() && isPanelOpen(slidingLayoutCategories)){
            ImageView addAccountButton = (ImageView) findViewById(R.id.add_category_button);
            Utils.closePanel(slidingLayoutCategories, addAccountButton);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method that controls the state of the panel passed as parameter
     * @param panel - the panel that has to be controlled
     * @return true if the panel is opened, false otherwise
     */
    private boolean isPanelOpen(SlidingUpPanelLayout panel){
        return panel != null && panel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED;
    }

    /**
     * Method that scales the image based on the device density
     * @param image - image to be scaled
     * @return scaled image
     */
    public Bitmap preprocessImage(Bitmap image) {
        float density = getResources().getDisplayMetrics().density;

        if (density <= 2) {
            return Utils.scaleImage(image, 150, 150);
        } else {
            return Utils.scaleImage(image, 450, 450);
        }
    }

    /**
     * Method that set the user image
     */
    public void setUserImage() {
        if (getUserImage() != null) {
            userImage.setImageBitmap(getUserImage());
        } else{
            Utils.showToast(getApplicationContext(), "Unable to find the user image", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Logging out from the application
     */
    public void logout() {
        Button logout = (Button) sideMenuView.getHeaderView(0).findViewById(R.id.logout_button);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                googleSignInClient.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    /**
     * Method that handles the closing for the add transaction panel
     */
    public View.OnClickListener closePanel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.closeKeyboard(v);
            Utils.closePanel(slidingLayout, null);
        }
    };

    /**
     * Method that handles the selection of the type of transaction
     */
    public RadioGroup.OnCheckedChangeListener incomeOutcomeHandler = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int checkedButtonSelectedId = incomeOutcomeRadioGroup.getCheckedRadioButtonId();
            Button checkedButton = (Button) findViewById(checkedButtonSelectedId);
            String selectedType = checkedButton.getText().toString().toLowerCase();
            disableCategoriesForIncomeTransaction(selectedType);
        }
    };

    /**
     * Method that handles the capturing of a new photo
     */
    public View.OnClickListener handleTakePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                startActivityForResult(cameraIntent, CAMERA_PICTURE);
            } catch (SecurityException e) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    };

    /**
     * Method that handles the bottom bar click events
     */
    public BottomNavigationView.OnNavigationItemSelectedListener handleFragments = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.account_management:
                    changeFragment(accountFragment);
                    break;
                case R.id.categories:
                    changeFragment(categoriesFragment);
                    break;
                case R.id.add_transaction:
                    categoriesSpinner = (Spinner) findViewById(R.id.categories_spinner);
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

    /**
     * Method that handles the side menu event clicks
     */
    public NavigationView.OnNavigationItemSelectedListener handleLeftMenuFragments = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.account_management:
                    changeFragment(accountFragment);
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case R.id.categories:
                    changeFragment(categoriesFragment);
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    break;
                case R.id.add_transaction:
                    categoriesSpinner = (Spinner) findViewById(R.id.categories_spinner);
                    addTransaction();
                    break;
                case R.id.transactions:
                    changeFragment(transactionsFragment);
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                    break;
                case R.id.overview:
                    changeFragment(overviewFragment);
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    break;
            }

            sideMenuDrawer.closeDrawer(GravityCompat.START, false);
            return true;
        }
    };

    /**
     * Method that handles the transaction input
     */
    public View.OnClickListener getTransactionInput = new View.OnClickListener() {
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
                    transactionAmountString = transactionAmountString.substring(0, transactionAmountString.length() - 1);
                    amountTextView.setText(transactionAmountString);
                    break;
                case R.id.insert_transaction_button:
                    Spinner accountSpinner = (Spinner) findViewById(R.id.account_spinner);
                    transactionNote = (EditText) findViewById(R.id.transaction_note_input2);

                    int checkedButtonSelectedId = incomeOutcomeRadioGroup.getCheckedRadioButtonId();
                    Button checkedButton = (Button) findViewById(checkedButtonSelectedId);

                    String account = accountSpinner.getSelectedItem().toString();
                    String category = categoriesSpinner.getSelectedItem().toString();
                    String note = transactionNote.getText().toString();
                    final String type = checkedButton.getText().toString();

                    if (!transactionAmountString.equals("")) {
                        Date currentTime = Calendar.getInstance().getTime();
                        String day = (String) DateFormat.format("EEEE", currentTime);
                        String dayNumber = (String) DateFormat.format("d", currentTime);
                        String month = (String) DateFormat.format("MMMM", currentTime);
                        String year = (String) DateFormat.format("yyyy", currentTime);

                        String transactionKey = day + dayNumber + month + year + "-" + System.currentTimeMillis();
                        TransactionDate transactionDate = new TransactionDate(day, dayNumber, month, year);
                        Transaction transaction = new Transaction(account, category, note, transactionAmountString, type.toLowerCase(), transactionDate);
                        DatabaseReference accountsReference = Utils.databaseReference.child("accounts");
                        final DatabaseReference selectedAccount = accountsReference.child(account).child("amount");

                        selectedAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long accountAmount = (long) snapshot.getValue();
                                if (type.toLowerCase().equals("expense")) {
                                    selectedAccount.setValue(accountAmount - Long.parseLong(transactionAmountString));
                                } else{
                                    selectedAccount.setValue(accountAmount + Long.parseLong(transactionAmountString));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(Utils.TAG, error.getMessage());
                            }
                        });

                        DatabaseReference transactionsReference = Utils.databaseReference.child("transactions");
                        transactionsReference.child(transactionKey).setValue(transaction);

                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        changeFragment(transactionsFragment);
                    } else {
                        Utils.showToast(v.getContext(), "Insert an amount", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };

    /**
     * Method that disable the categories spinner if the transaction is of type income, and
     * activates it if the transaction if of type expense
     * @param selectedType the radio button that is selected
     */
    public void disableCategoriesForIncomeTransaction(String selectedType){
        if (selectedType.equals("income")){
            categoriesSpinner.setEnabled(false);
            categoriesSpinner.setClickable(false);
        } else if (selectedType.equals("expense")){
            categoriesSpinner.setEnabled(true);
            categoriesSpinner.setClickable(true);
        }
    }

    /**
     * Method that save the image passed as parameter to the device
     * @param image - the image to be saved
     * @return true if the image was saved, false otherwise
     */
    public boolean saveUserImage(Bitmap image){
        File fileDirectory = getApplicationContext().getDir(Utils.applicationDirectory, Context.MODE_PRIVATE);
        File imageFile = new File(fileDirectory, Utils.userImageName + ".jpeg");

        if(imageFile.exists()) {
            if(!imageFile.delete())
                return false;
        }

        FileOutputStream imageStream = null;
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imageStream = new FileOutputStream(imageFile);
                if (image != null) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                    imageStream.flush();
                    return true;
                }
            } else {
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (imageStream != null)
                    imageStream.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Method that gets the user image from the device if already saved
     * @return the user image if present, null otherwise
     */
    public Bitmap getUserImage(){
        Bitmap image = null;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            image = BitmapFactory.decodeFile(applicationPath + Utils.userImageName + ".jpeg");
        } else {
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        return image;
    }

    /**
     * Methods that handles the switch among the fragments
     * @param fragment - the fragment to be shown
     */
    public void changeFragment(Fragment fragment){
       getSupportFragmentManager().beginTransaction()
               .replace(R.id.fl_wrapper, fragment, null)
               .commit();
    }

    /**
     * Method that inserts a new transaction in the database
     */
    public void addTransaction(){

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.setTouchEnabled(false);
        transactionAmountString = "";
        amountTextView.setText("0");
        transactionNote.setText("");

        Utils.databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final List<String> categoriesList = new ArrayList<String>();

                for (DataSnapshot category: snapshot.getChildren()) {
                    String categoryName = category.child("name").getValue(String.class);
                    if (categoryName != null){
                        categoriesList.add(categoryName);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.categories_spinner);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, categoriesList);
                addressAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerProperty.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Utils.TAG, error.getMessage());
            }
        });

        Utils.databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final List<String> accountsList = new ArrayList<String>();

                for (DataSnapshot account: snapshot.getChildren()) {
                    String accountTitle = account.child("title").getValue(String.class);
                    if (accountTitle != null){
                        accountsList.add(accountTitle);
                    }
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.account_spinner);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, accountsList);
                addressAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerProperty.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Utils.TAG, error.getMessage());
            }
        });

        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    /**
     * Method that controls if the transaction amount is valid
     * @param amount - the amount to be controlled
     * @return true if the amount is a valid number, false otherwise
     */
    public boolean validAmount(String amount){
        boolean match = false;
        if (amount != null) {
            String decimalPattern = "(^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$)";
            match = Pattern.matches(decimalPattern, amount);
        }

        return match;
    }
}