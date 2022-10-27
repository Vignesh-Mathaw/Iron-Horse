package com.spiderindia.ironhorse.fragment;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.ProductAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouriteFragment extends Fragment {

    private Activity activity;
    TextView txtnodata;
    RecyclerView favrecycleview;
    DatabaseHelper databaseHelper;
    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;
    public RelativeLayout layoutSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        txtnodata = view.findViewById(R.id.txtnodata);
        favrecycleview = view.findViewById(R.id.favrecycleview);
        GridLayoutManager layoutManager = new GridLayoutManager(activity,2);
        favrecycleview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(favrecycleview.getContext(),
                layoutManager.getOrientation());
        Drawable horizontalDivider = ContextCompat.getDrawable(activity, R.drawable.divider_line);
        dividerItemDecoration.setDrawable(horizontalDivider);
        favrecycleview.addItemDecoration(dividerItemDecoration);
        favrecycleview.setLayoutManager(new LinearLayoutManager(activity));
        databaseHelper = new DatabaseHelper(getActivity());
        getData();
    }

    private void getData() {
        productArrayList = new ArrayList<>();
        if (databaseHelper.getFavourite().isEmpty()) {
            txtnodata.setVisibility(View.VISIBLE);
            favrecycleview.setAdapter(new ProductAdapter(productArrayList, R.layout.lyt_item_list, activity));
        } else {
            for (final String id : databaseHelper.getFavourite()) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.PRODUCT_ID, id);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        //  System.out.println("=================*bookmark- " + response);
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    JSONObject object = new JSONObject(response);
                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                    productArrayList.add(ApiConfig.GetProductList(jsonArray).get(0));
                                    productAdapter = new ProductAdapter(productArrayList, R.layout.lyt_item_list, activity);
                                    favrecycleview.setAdapter(productAdapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, activity, Constant.GET_PRODUCT_DETAIL_URL, params, false);
            }
        }
    }


}
