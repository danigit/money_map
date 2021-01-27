package com.example.moneymap.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private TextView addCategoryButton;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        slidingLayoutAccount = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout_account);
        slidingLayoutAccount.setTouchEnabled(false);

        addCategoryButton = (TextView) view.findViewById(R.id.add_account_image_button);
        accountName = (TextView) view.findViewById(R.id.account_title_input);
        accountDescription = (TextView) view.findViewById(R.id.account_description_input);
        accountAmount = (TextView) view.findViewById(R.id.account_amount_input);

        addCategoryButton.setOnClickListener(openAccountPanel());

        Button saveAccountButton = (Button) view.findViewById(R.id.save_account_button);
        saveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = accountName.getText().toString();
                String description = accountDescription.getText().toString();
                double amount = Double.parseDouble(accountAmount.getText().toString());

                Account account = new Account(name, description, amount);

                DatabaseReference accountsReference = Utils.databaseReference.child("accounts");
                accountsReference.child(name).setValue(account);

                Context context = getContext();
                if (context != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                slidingLayoutAccount.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                addCategoryButton.setBackgroundResource(R.drawable.rounded_corners_red);
                addCategoryButton.setText(R.string.close);
            }
        });

        final LinearLayout accounts_layout = (LinearLayout) view.findViewById(R.id.accounts_layout);

        Utils.databaseReference.child("accounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double accountsTotal = 0;
                accounts_layout.removeAllViews();

                for (DataSnapshot child: snapshot.getChildren()){
                    Account account = child.getValue(Account.class);

                    if (account != null) {
                        accountsTotal += account.amount;
                        accounts_layout.addView(fillAccountRow(account));
                    }
                }

                TextView allAccountsTotal = (TextView) view.findViewById(R.id.all_accounts_total);
                allAccountsTotal.setText(String.valueOf(accountsTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.showErrorToast(getContext(), error.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    // method that handles the click on the add and close button in the account page
    public View.OnClickListener openAccountPanel(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCategoryButton.getText().toString().equals("CLOSE")) {
                    slidingLayoutAccount.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    addCategoryButton.setBackgroundResource(R.drawable.rounded_corners);
                    addCategoryButton.setText(R.string.add);
                } else {
                    slidingLayoutAccount.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    addCategoryButton.setBackgroundResource(R.drawable.rounded_corners_red);
                    addCategoryButton.setText(R.string.close);
                }
            }
        };
    }

    // method that creates and fill a view which represent an account retrieved from the database
    public View fillAccountRow(Account account){
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.account_row, null);

        TextView title =  layout.findViewById(R.id.account_title);
        title.setText(account.title);

        TextView description =  layout.findViewById(R.id.account_description);
        description.setText(account.description);

        TextView amount =  layout.findViewById(R.id.account_amount);
        amount.setText(String.valueOf(account.amount));

        return layout;
    }
}

