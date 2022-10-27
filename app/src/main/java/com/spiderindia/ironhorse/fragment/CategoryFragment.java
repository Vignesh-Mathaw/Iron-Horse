package com.spiderindia.ironhorse.fragment;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.CategoryAdapter;

public class CategoryFragment extends Fragment {
    private TextView txtnodata;
    private RecyclerView categoryrecycleview;
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        setHasOptionsMenu(true);
        txtnodata = view.findViewById(R.id.txtnodata);
        categoryrecycleview = view.findViewById(R.id.categoryrecycleview);
        GridLayoutManager layoutManager = new GridLayoutManager(activity,2);
        categoryrecycleview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryrecycleview.getContext(),
                layoutManager.getOrientation());
        Drawable horizontalDivider = ContextCompat.getDrawable(activity, R.drawable.divider_line);
        dividerItemDecoration.setDrawable(horizontalDivider);
        categoryrecycleview.addItemDecoration(dividerItemDecoration);
        if (HomeFragment.categoryArrayList != null)
            if (HomeFragment.categoryArrayList.size() == 0)
                txtnodata.setVisibility(View.VISIBLE);
            else
                categoryrecycleview.setAdapter(new CategoryAdapter(activity, HomeFragment.categoryArrayList, R.layout.lyt_category, "cate"));

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(true);
    }
}
