package com.example.moneymap;

import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TransactionAdapter extends FirebaseRecyclerAdapter<Transaction, TransactionAdapter.transactionViewHolder> {

    double transactionAmount;
    Map hasInserted = new HashMap<String, Integer>();


    public TransactionAdapter(FirebaseRecyclerOptions<Transaction> options){
        super(options);
        transactionAmount = 0;
    }

    @Override
    protected void onBindViewHolder(@NonNull transactionViewHolder holder, int position, @NonNull Transaction model) {

        Resources resource = holder.note.getResources();
        final String date = model.transactionDate.day + model.transactionDate.dayNumber + model.transactionDate.month + model.transactionDate.year;

        Object value = hasInserted.get(date);
        if (value == null){
            Log.d(Utils.TAG, "object is not null");
            ConstraintLayout transactionDateLayout = ((View) holder.category.getParent().getParent()).findViewById(R.id.transaction_data_container);
            transactionDateLayout.setVisibility(View.VISIBLE);
            TextView day = transactionDateLayout.findViewById(R.id.day_text_view);
            TextView dayNumber = transactionDateLayout.findViewById(R.id.day_number_text_view);
            TextView monthYear = transactionDateLayout.findViewById(R.id.month_year_text_view);
            final TextView dateAmount = transactionDateLayout.findViewById(R.id.total_amount_date_text_view);

            day.setText(model.transactionDate.day);
            dayNumber.setText(model.transactionDate.dayNumber);
            String monthYearString = model.transactionDate.month + " " + model.transactionDate.year;
            monthYear.setText(monthYearString);
            Utils.databaseReference.child("transactions").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    transactionAmount = 0;
                    // 4. get all the transactions with the above date and make the sum
                    for (DataSnapshot transaction: snapshot.getChildren()) {

                        TransactionDate transactionDateObject = transaction.child("transactionDate").getValue(TransactionDate.class);
                        if (transactionDateObject  != null){
                            // 4.1 get the date of the current transaction
                            String dateObject = transactionDateObject.day + transactionDateObject.dayNumber + transactionDateObject.month + transactionDateObject.year;
                            // 4.2 compare it with the current one, if equal add the amount
                            String amount = transaction.child("amount").getValue().toString();
                            if (dateObject.equals(date)){
                                transactionAmount += Double.parseDouble(amount);
                            }
                        }
                    }
                    dateAmount.setText(String.valueOf(transactionAmount));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        hasInserted.putIfAbsent(date, position);

        if (model.type.equals("income")){
            holder.category.setTextColor(resource.getColor(R.color.colorAccent));
        } else{
            holder.category.setTextColor(resource.getColor(R.color.red_dark_color));
        }

        holder.account.setText(model.account);
        holder.category.setText(model.category);
        holder.note.setText(model.note);
        holder.amount.setText(model.amount);
    }

    @NonNull
    @Override
    public transactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_layout, parent, false);
        return new TransactionAdapter.transactionViewHolder(view);
    }

    static class transactionViewHolder extends RecyclerView.ViewHolder{

        TextView account, category, note, amount;

        public transactionViewHolder(@NonNull View itemView) {
            super(itemView);

            account = itemView.findViewById(R.id.transaction_account);
            category = itemView.findViewById(R.id.transaction_category);
            note = itemView.findViewById(R.id.transaction_note);
            amount = itemView.findViewById(R.id.transaction_amount);
        }
    }



































}
//    String[] categories;
//    String[] descriptions;
//    String[] amount;
//    int[] images;
//    Context context;
//
//    public TransactionAdapter(Context cx, String[] categories, String[] descriptions, String[] amount, int[] images){
//        context = cx;
//        this.categories = categories;
//        this.descriptions = descriptions;
//        this.amount = amount;
//        this.images = images;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.row_layout, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        if (position == 0){
//            Log.d("dani", "The positioin is " + position);
//        }
//        holder.title.setText(categories[position]);
//        holder.content.setText(descriptions[position]);
//        holder.amount.setText(amount[position]);
//        holder.image.setImageResource(images[position]);
//    }
//
//    @Override
//    public int getItemCount() {
//        return images.length;
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//
//        TextView title, content, amount;
//        ImageView image;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            title = itemView.findViewById(R.id.title);
//            content = itemView.findViewById(R.id.content);
//            amount = itemView.findViewById(R.id.amount);
//            image = itemView.findViewById(R.id.image);
//        }
//    }
//}
