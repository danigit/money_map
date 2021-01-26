package com.example.moneymap;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AddCategoryAdapter extends FirebaseRecyclerAdapter<String, AddCategoryAdapter.AddCategoryViewHolder> {

    public View parentView;

    public AddCategoryAdapter(FirebaseRecyclerOptions<String> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final AddCategoryAdapter.AddCategoryViewHolder holder, int position, @NonNull final String model) {

        holder.setCategoryData(position);
        holder.categoryImage.setImageResource(holder.categoryImage.getContext().getResources().getIdentifier(model, "drawable", holder.categoryImage.getContext().getPackageName()));
        holder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) ((View)parentView.getParent()).findViewById(R.id.constraintLayout3).findViewById(R.id.category_icon_image);;
                TextView imageName = (TextView) (((View) parentView.getParent()).findViewById(R.id.constraintLayout3).findViewById(R.id.category_icon_name_text_view));
                imageName.setText(model);
                imageName.setVisibility(View.INVISIBLE);
                imageView.setImageResource(holder.categoryImage.getContext().getResources().getIdentifier(model, "drawable", holder.categoryImage.getContext().getPackageName()));
            }
        });
    }

    @NonNull
    @Override
    public AddCategoryAdapter.AddCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        parentView = parent;
        View view = inflater.inflate(R.layout.category_layout, parent, false);
        return new AddCategoryAdapter.AddCategoryViewHolder(view);
    }

    static class AddCategoryViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView categoryImage;

        public AddCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setCategoryData(int index) {
            Log.d(Utils.TAG, "" + index % 3);
            if(index % 3 == 0){
                categoryImage = itemView.findViewById(R.id.category_image);
            } else if (index % 3 == 1){
                categoryImage = itemView.findViewById(R.id.category_image1);
            } else if (index % 3 == 2){
                categoryImage = itemView.findViewById(R.id.category_image2);
            }
        }
    }
}
