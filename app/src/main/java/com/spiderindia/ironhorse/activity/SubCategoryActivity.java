package com.spiderindia.ironhorse.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.CategoryAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Category;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SubCategoryActivity extends AppCompatActivity {

    public Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<Category> categoryArrayList;
    TextView tvtitle, tvAlert;
    String cateId, cateName;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cateId = getIntent().getStringExtra("id");
        cateName = getIntent().getStringExtra("name");
        progressbar = findViewById(R.id.progressBar);
        tvtitle = findViewById(R.id.title);
        tvAlert = findViewById(R.id.tvAlert);
        tvtitle.setText(cateName);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        try {
            GetCategory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetCategory() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CATEGORY_ID, cateId);
        System.out.println("======params" + params.toString());
        categoryArrayList = new ArrayList<>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("======sub cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                categoryArrayList.add(new Category(jsonObject.getString(Constant.ID),
                                        jsonObject.getString(Constant.NAME),
                                        jsonObject.getString(Constant.SUBTITLE),
                                        jsonObject.getString(Constant.IMAGE),""));
                            }
                            recyclerView.setAdapter(new CategoryAdapter(SubCategoryActivity.this, categoryArrayList, R.layout.lyt_category_main, "sub_cate"));

                        } else {
                            tvAlert.setVisibility(View.VISIBLE);
                            tvAlert.setText(object.getString(Constant.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, SubCategoryActivity.this, Constant.SubcategoryUrl, params, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
