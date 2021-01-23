package com.example.moneymap.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moneymap.Account;
import com.example.moneymap.R;
import com.example.moneymap.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://moneymap-70e0a-default-rtdb.europe-west1.firebasedatabase.app");
        database.setPersistenceEnabled(true);

        DatabaseReference databaseReference = database.getReference();
        Account account = new Account("finecobank", "This is a fineco account", 10000);
        DatabaseReference accountsReference = databaseReference.child("accounts");
        accountsReference.child("fineco").setValue(account);

        final LinearLayout accounts_layout = (LinearLayout) view.findViewById(R.id.accounts_layout);

        Utils.database.child("accounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    LinearLayout a = new LinearLayout(getContext());
                    a.setOrientation(LinearLayout.HORIZONTAL);
                    View layout = LayoutInflater.from(getContext()).inflate(R.layout.account_row, null);
                    TextView title =  layout.findViewById(R.id.account_title);
                    title.setText(child.child("title").getValue().toString());
                    TextView description =  layout.findViewById(R.id.account_description);
                    description.setText(child.child("description").getValue().toString());
                    TextView amount =  layout.findViewById(R.id.account_amount);
                    amount.setText(child.child("amount").getValue().toString());
                    a.addView(layout);
                    accounts_layout.addView(a);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}

