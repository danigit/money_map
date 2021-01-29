package com.example.moneymap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        final int image = holder.categoryImage.getContext().getResources().getIdentifier(model, "drawable", holder.categoryImage.getContext().getPackageName());
        holder.categoryImage.setImageResource(image);
        holder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View categoryIconView = ((View)parentView.getParent()).findViewById(R.id.constraintLayout3);
                ImageView imageView = (ImageView) categoryIconView.findViewById(R.id.category_icon_image);;
                TextView imageName = (TextView) categoryIconView.findViewById(R.id.category_icon_name_text_view);

                imageName.setText(model);
                imageName.setVisibility(View.INVISIBLE);
                imageView.setImageResource(image);
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
        ImageView categoryImage;

        public AddCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
        }
    }
}
