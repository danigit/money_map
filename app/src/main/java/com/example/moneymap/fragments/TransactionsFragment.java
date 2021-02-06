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

import com.example.moneymap.TransactionAdapter;
import com.example.moneymap.R;
import com.example.moneymap.Transaction;
import com.example.moneymap.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class TransactionsFragment extends Fragment {

    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
    }
}