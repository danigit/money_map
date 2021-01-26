package com.example.moneymap;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends FirebaseRecyclerAdapter<Category, CategoriesAdapter.categoryViewHolder> {

    public CategoriesAdapter(FirebaseRecyclerOptions<Category> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull categoryViewHolder holder, int position, @NonNull Category model) {

        holder.setCategoryData(position);
        holder.name.setText(model.name);
        holder.categoryImage.setImageResource(holder.categoryImage.getContext().getResources().getIdentifier(model.imageName, "drawable", holder.categoryImage.getContext().getPackageName()));
    }

    @NonNull
    @Override
    public CategoriesAdapter.categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_layout, parent, false);
        return new CategoriesAdapter.categoryViewHolder(view);
    }

    static class categoryViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView categoryImage;

        public categoryViewHolder(@NonNull View itemView) {
            super(itemView);

//            name = itemView.findViewById(R.id.category_name);
//            categoryImage = itemView.findViewById(R.id.category_image);
        }

        public void setCategoryData(int index) {
            Log.d(Utils.TAG, "" + index % 3);
            if(index % 3 == 0){
                name = itemView.findViewById(R.id.category_name);
                categoryImage = itemView.findViewById(R.id.category_image);
            } else if (index % 3 == 1){
                name = itemView.findViewById(R.id.category_name1);
                categoryImage = itemView.findViewById(R.id.category_image1);
            } else if (index % 3 == 2){
                name = itemView.findViewById(R.id.category_name2);
                categoryImage = itemView.findViewById(R.id.category_image2);
            }
        }
    }
}
