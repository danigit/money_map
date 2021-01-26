package com.example.moneymap.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymap.Account;
import com.example.moneymap.AddCategoryAdapter;
import com.example.moneymap.CategoriesAdapter;
import com.example.moneymap.Category;
import com.example.moneymap.R;
import com.example.moneymap.Transaction;
import com.example.moneymap.TransactionAdapter;
import com.example.moneymap.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    private SlidingUpPanelLayout slidingLayout;
    private TextView categoryName;
    private TextView categoryIconImageName;
    private Button saveCategoryButton;


    RecyclerView recyclerView;
    RecyclerView recyclerViewView;
    CategoriesAdapter categoriesAdapter;
    AddCategoryAdapter addCategoriesAdapter;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.categories_recycler);
        recyclerViewView = view.findViewById(R.id.categories_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(Utils.databaseReference.child("categories"), Category.class)
                .build();
        FirebaseRecyclerOptions<String> addAdapterOptions = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(Utils.databaseReference.child("categories_images"), String.class)
                .build();

        categoriesAdapter = new CategoriesAdapter(options);
        addCategoriesAdapter = new AddCategoryAdapter(addAdapterOptions);

        recyclerView.setAdapter(categoriesAdapter);
        recyclerViewView.setAdapter(addCategoriesAdapter);

        categoriesAdapter.startListening();
        addCategoriesAdapter.startListening();

        categoryName = (TextView) view.findViewById(R.id.category_name);
        categoryIconImageName = (TextView) view.findViewById(R.id.category_icon_name_text_view);

        saveCategoryButton = (Button) view.findViewById(R.id.save_category_button);
        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category category = new Category(categoryName.getText().toString(), categoryIconImageName.getText().toString());
                DatabaseReference accountsReference = Utils.databaseReference.child("categories");
                accountsReference.child(categoryName.getText().toString()).setValue(category);
                InputMethodManager imm = (InputMethodManager) ((Activity)getContext()).getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
    }
}