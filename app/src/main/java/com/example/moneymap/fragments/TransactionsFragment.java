package com.example.moneymap.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.moneymap.MainActivity;
import com.example.moneymap.TransactionAdapter;
import com.example.moneymap.R;
import com.example.moneymap.Transaction;
import com.example.moneymap.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Transaction> options = new FirebaseRecyclerOptions.Builder<Transaction>()
                .setQuery(Utils.databaseReference.child("transactions"), Transaction.class)
                .build();

        transactionAdapter = new TransactionAdapter(options);
        recyclerView.setAdapter(transactionAdapter);

        transactionAdapter.startListening();
//        Utils.databaseReference.child("transactions").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                final List<Transaction> transactionsList = new ArrayList<Transaction>();
//
//                for (DataSnapshot addressSnapshot: snapshot.getChildren()) {
//                    String transactionCategory = addressSnapshot.child("category").getValue(String.class);
//                    String transactionAccount = addressSnapshot.child("account").getValue(String.class);
//                    String transactionNote = addressSnapshot.child("note").getValue(String.class);
//                    String transactionAmount = addressSnapshot.child("note").getValue(String.class);
//                    Transaction transaction = new Transaction(transactionAccount, transactionCategory, transactionNote, transactionAmount);
//                    transactionsList.add(transaction);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        TransactionAdapter myAdapter = new TransactionAdapter(this, classes, descriptions, amount, images);
//        recyclerView.setAdapter(myAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}