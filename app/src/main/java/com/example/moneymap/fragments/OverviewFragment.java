package com.example.moneymap.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymap.Login;
import com.example.moneymap.R;
import com.example.moneymap.TransactionDate;
import com.example.moneymap.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    private BarChart transactionsPlot;
    private ImageView modifyUserInformation;
    private ImageView saveUserInformation;
    private EditText profileSavingsGoalInput;
    private EditText profileFixedCostsInput;
    private EditText profileMaxExpensesInput;
    private TextView profileSavingsGoalText;
    private TextView profileFixedCostsText;
    private TextView profileMaxExpensesText;
    private TextView balanceAmount;
    private TextView expensesTextView;
    private TextView incomesTextView;

    private float totalAmount;
    private float accountsTotal;
    private float totalExpenses;
    private float totalIncomes;

    Map<String, Float> dayExpense;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dayExpense = new LinkedHashMap<>();
        totalAmount = 0;
        accountsTotal = 0;
        totalExpenses = 0;
        totalIncomes = 0;
        for (int i = 1; i < 32; i ++){
            dayExpense.put(""+i, 0.0f);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionsPlot = view.findViewById(R.id.transactions_chart);
        transactionsPlot.setDrawGridBackground(false);
        transactionsPlot.getDescription().setEnabled(false);
        transactionsPlot.getLegend().setEnabled(false);
        transactionsPlot.setData(new BarData());

        profileSavingsGoalInput = (EditText) view.findViewById(R.id.user_savings_goal_input_field);
        profileFixedCostsInput = (EditText) view.findViewById(R.id.user_fixed_costs_input_field);
        profileMaxExpensesInput = (EditText) view.findViewById(R.id.user_max_expense_input_field);
        profileSavingsGoalText = (TextView) view.findViewById(R.id.profile_savings_goal_text_view);
        profileFixedCostsText = (TextView) view.findViewById(R.id.profile_fixed_expenses_text_view);
        profileMaxExpensesText = (TextView) view.findViewById(R.id.profile_max_expenses_text_view);
        modifyUserInformation = (ImageView) view.findViewById(R.id.modify_user_data_icon);
        saveUserInformation = (ImageView) view.findViewById(R.id.save_user_data_icon);

        balanceAmount = (TextView) view.findViewById(R.id.balance_amount);
        expensesTextView = (TextView) view.findViewById(R.id.expenses_overview);
        incomesTextView = (TextView) view.findViewById(R.id.income_overview);

        modifyUserInformation.setOnClickListener(handleModifyUserInformation);
        saveUserInformation.setOnClickListener(handleSaveUserInformation);

        XAxis xAxis = transactionsPlot.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayExpense.keySet())) ;

        transactionsPlot.getAxisRight().setEnabled(false);
        transactionsPlot.getAxisLeft().setStartAtZero(true);

        Utils.databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot account : snapshot.getChildren()){
                    accountsTotal += Float.parseFloat(account.child("amount").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // create a couple arrays of y-values to plot:
        Utils.databaseReference.child("transactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String currentDay = "";
                for (DataSnapshot transaction : snapshot.getChildren()){
                    Date currentTime = Calendar.getInstance().getTime();
                    String month = (String) DateFormat.format("MMMM", currentTime);
                    float amount = Float.parseFloat(transaction.child("amount").getValue().toString());
                    totalAmount += amount;

                    Log.d(Utils.TAG, transaction.child("type").getValue().toString());
                    if (transaction.child("type").getValue().toString().equals("expense")) {
                        TransactionDate transactionDate = transaction.child("transactionDate").getValue(TransactionDate.class);
                        if (transactionDate != null) {
                            String transactionMonth = transactionDate.month;
                            if (transactionMonth.equals(month)) {
                                if (!transactionDate.dayNumber.equals(currentDay)) {
                                    Float expense = dayExpense.get(transactionDate.dayNumber);
                                    if (expense != null) {
                                        dayExpense.put(transactionDate.dayNumber, amount + expense);
                                    } else {
                                        dayExpense.put(transactionDate.dayNumber, amount);
                                    }
                                } else {
                                    currentDay = transactionDate.dayNumber;
                                }
                            }
                        }

                        totalExpenses += amount;
                    } else if (transaction.child("type").getValue().toString().equals("income")){
                        totalIncomes += amount;
                    }
                }

                Log.d(Utils.TAG, Currency.getInstance(Locale.ITALY).getSymbol());
                String balance = String.valueOf(accountsTotal - totalExpenses) + " " + Currency.getInstance(Locale.ITALY).getSymbol();
                String expenses = String.valueOf(totalExpenses) + " " + Currency.getInstance(Locale.ITALY).getSymbol();
                String incomes = String.valueOf(totalIncomes) + " " + Currency.getInstance(Locale.ITALY).getSymbol();
                balanceAmount.setText(balance);
                expensesTextView.setText(expenses);
                incomesTextView.setText(incomes);

                for (Map.Entry dayEntry : dayExpense.entrySet()){
                    showReading((float)dayEntry.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        Utils.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(Utils.TAG, snapshot.child("fixed_costs").getValue().toString());
                profileSavingsGoalText.setText(snapshot.child("savings_goal").getValue().toString());
                profileFixedCostsText.setText(snapshot.child("fixed_costs").getValue().toString());
                profileMaxExpensesText.setText(snapshot.child("max_expense").getValue().toString());

                profileSavingsGoalInput.setText(snapshot.child("savings_goal").getValue().toString());
                profileFixedCostsInput.setText(snapshot.child("fixed_costs").getValue().toString());
                profileMaxExpensesInput.setText(snapshot.child("max_expense").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.showToast(getContext(), "Unable to retrieve the data", Toast.LENGTH_SHORT);
            }
        });
    }

    private void showReading(float readedValue){
        BarData data = transactionsPlot.getData();
        IBarDataSet set = data.getDataSetByIndex(0);

        if(set == null) {
            BarDataSet dataSet = new BarDataSet(new ArrayList<BarEntry>(), "");

            dataSet.setColor(getResources().getColor(R.color.red_color));
            dataSet.setDrawValues(false);
            dataSet.setHighLightColor(Color.RED);
            set = dataSet;
            data.addDataSet(dataSet);
        }

        BarEntry entry = new BarEntry(set.getEntryCount(), readedValue, 0);
        data.addEntry(entry, 0);
        data.notifyDataChanged();
        transactionsPlot.notifyDataSetChanged();
        transactionsPlot.invalidate();
        transactionsPlot.refreshDrawableState();
    }

    public View.OnClickListener handleModifyUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileSavingsGoalText.setVisibility(View.INVISIBLE);
            profileFixedCostsText.setVisibility(View.INVISIBLE);
            profileMaxExpensesText.setVisibility(View.INVISIBLE);
            modifyUserInformation.setVisibility(View.INVISIBLE);

            profileSavingsGoalInput.setVisibility(View.VISIBLE);
            profileFixedCostsInput.setVisibility(View.VISIBLE);
            profileMaxExpensesInput.setVisibility(View.VISIBLE);
            saveUserInformation.setVisibility(View.VISIBLE);
        }
    };

    public View.OnClickListener handleSaveUserInformation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            profileSavingsGoalInput.setVisibility(View.INVISIBLE);
            profileFixedCostsInput.setVisibility(View.INVISIBLE);
            profileMaxExpensesInput.setVisibility(View.INVISIBLE);
            saveUserInformation.setVisibility(View.INVISIBLE);

            Utils.databaseReference.child("savings_goal").setValue(profileSavingsGoalInput.getText().toString());
            Utils.databaseReference.child("fixed_costs").setValue(profileFixedCostsInput.getText().toString());
            Utils.databaseReference.child("max_expense").setValue(profileMaxExpensesInput.getText().toString());

            profileSavingsGoalText.setVisibility(View.VISIBLE);
            profileFixedCostsText.setVisibility(View.VISIBLE);
            profileMaxExpensesText.setVisibility(View.VISIBLE);
            modifyUserInformation.setVisibility(View.VISIBLE);

        }
    };
}