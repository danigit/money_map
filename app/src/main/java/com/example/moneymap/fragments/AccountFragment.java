package com.example.moneymap.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.Account;
import com.example.moneymap.R;
import com.example.moneymap.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;



/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private SlidingUpPanelLayout slidingLayoutAccount;
    private TextView accountName;
    private TextView accountDescription;
    private TextView accountAmount;
    private TextView addAccountButton;

    public AccountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveAccountButton = (Button) view.findViewById(R.id.save_account_button);

        slidingLayoutAccount = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout_account);
        slidingLayoutAccount.setTouchEnabled(false);

        addAccountButton = (TextView) view.findViewById(R.id.add_account_image_button);
        accountName = (TextView) view.findViewById(R.id.account_title_input);
        accountDescription = (TextView) view.findViewById(R.id.account_description_input);
        accountAmount = (TextView) view.findViewById(R.id.account_amount_input);

        addAccountButton.setOnClickListener(Utils.openCloseAccountPanel(slidingLayoutAccount, addAccountButton, "accounts"));
        saveAccountButton.setOnClickListener(addAccountListener());

        final LinearLayout accounts_layout = (LinearLayout) view.findViewById(R.id.accounts_layout);

        // getting the data from the database
        Utils.databaseReference.child("accounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Context context = getContext();
                TextView allAccountsTotal = (TextView) view.findViewById(R.id.all_accounts_total);

                double accountsTotal = 0;
                accounts_layout.removeAllViews();

                for (DataSnapshot accountElement: snapshot.getChildren()){
                    Account account = accountElement.getValue(Account.class);

                    if (account != null) {
                        accountsTotal += account.amount;
                        if (context != null) {
                            accounts_layout.addView(fillAccountRow(account, context));
                        } else {
                            Log.d(Utils.TAG, "The context is null");
                        }
                    } else {
                        Utils.showErrorToast(context, "Database error", Toast.LENGTH_SHORT);
                        Log.d(Utils.TAG, "The account is null!");
                    }
                }

                allAccountsTotal.setText(String.valueOf(accountsTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.showErrorToast(getContext(), error.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        slidingLayoutAccount.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onPause() {
        super.onPause();

        slidingLayoutAccount.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    /**
     * Method that handles the insertion of a new account into the database
     * @return onClickListener
     */
    private View.OnClickListener addAccountListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = accountName.getText().toString();
                String description = accountDescription.getText().toString();
                String amountString = accountAmount.getText().toString();
                double amount = 0;

                if (name.equals("")){
                    Utils.showWarnToast(getContext(), "Please insert a name", Toast.LENGTH_SHORT);
                } else {
                    if (!amountString.equals(""))
                        amount = Double.parseDouble(amountString);

                    Account account = new Account(name, description, amount);

                    DatabaseReference accountsReference = Utils.databaseReference.child("accounts");
                    accountsReference.child(name).setValue(account);

                    Utils.closeKeyboard(v);
                    Utils.closePanel(slidingLayoutAccount, addAccountButton);
                }
            }
        };
    }

    /**
     * method that creates and fill a view which represent an account retrieved from the database
     * @param account - contains account information
     * @param context - is the application context
     * @return layout - is the layout to be inserted
     */
    private View fillAccountRow(Account account, Context context){
        View layout = LayoutInflater.from(context).inflate(R.layout.account_row, null);

        if (layout != null) {

            TextView title = layout.findViewById(R.id.account_title);
            TextView description = layout.findViewById(R.id.account_description);
            TextView amount = layout.findViewById(R.id.account_amount);

            if (account.amount < 0) {
                title.setTextColor(getResources().getColor(R.color.red_color));
                amount.setTextColor(getResources().getColor(R.color.red_color));
            }

            title.setText(account.title);
            description.setText(account.description);
            amount.setText(String.valueOf(account.amount));
        }

        return layout;
    }
}

