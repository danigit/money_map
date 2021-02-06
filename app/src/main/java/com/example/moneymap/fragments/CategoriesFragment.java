package com.example.moneymap.fragments;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymap.AddCategoryAdapter;
import com.example.moneymap.CategoriesAdapter;
import com.example.moneymap.Category;
import com.example.moneymap.R;
import com.example.moneymap.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    private SlidingUpPanelLayout slidingLayout;
    private TextView categoryName;
    private TextView categoryIconImageName;
    private TextView addCategoryButton;
    boolean isOpened = false;

    public void setListenerToRootView() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler);
        RecyclerView recyclerViewView = view.findViewById(R.id.categories_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(Utils.databaseReference.child("categories"), Category.class)
                .build();
        FirebaseRecyclerOptions<String> addAdapterOptions = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(Utils.databaseReference.child("categories_images"), String.class)
                .build();

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(options);
        AddCategoryAdapter addCategoriesAdapter = new AddCategoryAdapter(addAdapterOptions);

        recyclerView.setAdapter(categoriesAdapter);
        recyclerViewView.setAdapter(addCategoriesAdapter);

        categoriesAdapter.startListening();
        addCategoriesAdapter.startListening();

        setListenerToRootView();
        categoryName = (TextView) view.findViewById(R.id.category_name);
        categoryIconImageName = (TextView) view.findViewById(R.id.category_icon_name_text_view);
        addCategoryButton = (TextView) view.findViewById(R.id.add_category_button);

        addCategoryButton.setOnClickListener(openCategoryPanel());

        slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout_categories);
        slidingLayout.setTouchEnabled(false);

        Button saveCategoryButton = (Button) view.findViewById(R.id.save_category_button);
        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryNameString = categoryName.getText().toString();
                String categoryImageName = categoryIconImageName.getText().toString();

                if (categoryNameString.equals("")){
                    Utils.showToast(getContext(), "Please insert a name", Toast.LENGTH_SHORT);
                } else if (categoryImageName.equals("")) {
                    Utils.showToast(getContext(), "Please select an image", Toast.LENGTH_SHORT);
                }else{
                    Category category = new Category(categoryNameString, categoryImageName);
                    DatabaseReference accountsReference = Utils.databaseReference.child("categories");
                    accountsReference.child(categoryNameString).setValue(category);

                    Utils.closeKeyboard(v);
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                    addCategoryButton.setText(R.string.add);
                    addCategoryButton.setBackgroundResource(R.drawable.rounded_corners);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    // method that handles the click on the add and close button in the account page
    public View.OnClickListener openCategoryPanel(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCategoryButton.getText().toString().equals("CLOSE")) {
                    categoryName.setText("");
                    categoryIconImageName.setText("");
                    ((ImageView) getActivity().findViewById(R.id.category_icon_image)).setImageDrawable(null);

                    Utils.closeKeyboard(v);
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    addCategoryButton.setBackgroundResource(R.drawable.rounded_corners);
                    addCategoryButton.setText(R.string.add);
                } else {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    addCategoryButton.setBackgroundResource(R.drawable.rounded_corners_red);
                    addCategoryButton.setText(R.string.close);
                }
            }
        };
    }
}