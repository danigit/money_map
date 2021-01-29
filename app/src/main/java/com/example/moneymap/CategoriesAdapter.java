package com.example.moneymap;

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


public class CategoriesAdapter extends FirebaseRecyclerAdapter<Category, CategoriesAdapter.categoryViewHolder> {

    public CategoriesAdapter(FirebaseRecyclerOptions<Category> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull categoryViewHolder holder, int position, @NonNull Category model) {

        int categoryImageId = holder.categoryImage.getContext().getResources().getIdentifier(model.imageName, "drawable", holder.categoryImage.getContext().getPackageName());
        holder.name.setText(model.name);
        holder.categoryImage.setImageResource(categoryImageId);
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
            name = itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_image);
        }
    }
}
