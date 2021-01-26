package com.example.moneymap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TransactionAdapter extends FirebaseRecyclerAdapter<Transaction, TransactionAdapter.transactionViewHolder> {

    public TransactionAdapter(FirebaseRecyclerOptions<Transaction> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull transactionViewHolder holder, int position, @NonNull Transaction model) {

        Log.d(Utils.TAG, "setting the fields");
        holder.account.setText(model.amount);
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
