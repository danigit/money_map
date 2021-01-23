package com.example.moneymap.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private SlidingUpPanelLayout slidingLayout;
    private Button btnShow;
    private Button btnHide;
    private TextView textView;

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

        final int[] i = {0};
//        addAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(Utils.TAG, "Button clicked");
//                Account account = new Account("finecobank" +i[0]++, "This is a fineco account", 10000);
//                DatabaseReference accountsReference = Utils.databaseReference.child("accounts");
//                accountsReference.child("fineco" + i[0]).setValue(account);
//                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//            }
//        });

        final LinearLayout accounts_layout = (LinearLayout) view.findViewById(R.id.accounts_layout);

        Utils.databaseReference.child("accounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double accountsTotal = 0;
                accounts_layout.removeAllViews();
                for (DataSnapshot child: snapshot.getChildren()){
                    View layout = LayoutInflater.from(getContext()).inflate(R.layout.account_row, null);
                    TextView title =  layout.findViewById(R.id.account_title);
                    title.setText(child.child("title").getValue().toString());
                    TextView description =  layout.findViewById(R.id.account_description);
                    description.setText(child.child("description").getValue().toString());
                    TextView amount =  layout.findViewById(R.id.account_amount);
                    amount.setText(child.child("amount").getValue().toString());
                    accountsTotal += child.child("amount").getValue(Double.class);
                    accounts_layout.addView(layout);
                }
                TextView allAccountsTotal = (TextView) view.findViewById(R.id.all_accounts_total);
                allAccountsTotal.setText(String.valueOf(accountsTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        slidingLayout.setPanelSlideListener(onSlideListener());
    }

    /**
     * Request show sliding layout when clicked
     * @return
     */
    private View.OnClickListener onShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show sliding layout in bottom of screen (not expand it)
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };
    }

    /**
     * Hide sliding layout when click button
     * @return
     */
    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide sliding layout
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        };
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
            }

            @Override
            public void onPanelExpanded(View view) {
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}

