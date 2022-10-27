package com.spiderindia.ironhorse.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.ProductLoadMoreAdapter;
import com.spiderindia.ironhorse.fragment.HomeFragment;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.AppController;
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

public class ProductListActivity extends AppCompatActivity {

    private String name, id, filterBy, from;
    private RecyclerView recyclerView;
    private ArrayList<Product> productArrayList;
    private ProductLoadMoreAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int offset, filterIndex;
    private ImageView fab;
    private RelativeLayout lay_menu;
    private LinearLayout lay_filter_container;

    private TextView tvAlert;
    private DatabaseHelper databaseHelper;
    private Menu menu;
    private String GET_PRODUCT_URL;
    int total;
    Toolbar toolbar;
    int position;
    NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private boolean isLoadMore = false;

    private RadioGroup rad_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        from = getIntent().getStringExtra("from");
        name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar);
        lay_menu = findViewById(R.id.lay_menu);
        lay_filter_container = findViewById(R.id.lay_filter_container);
        rad_group = findViewById(R.id.rad_group);
        lay_menu.setVisibility(View.GONE);
        fab = findViewById(R.id.fab);
        databaseHelper = new DatabaseHelper(ProductListActivity.this);
        tvAlert = findViewById(R.id.txtnodata);
        nestedScrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));
        productArrayList = new ArrayList<>();
        filterIndex = -1;
        if (from.equals("regular")) {

            id = getIntent().getStringExtra("id");
            GET_PRODUCT_URL = Constant.GET_PRODUCT_BY_SUB_CATE;
            try {
                ReLoadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (from.equals("item")) {
            id = getIntent().getStringExtra("id");
            GET_PRODUCT_URL = Constant.GET_PRODUCT_BY_SUB_CATE_ITEM;
            try {
                ReLoadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            position = getIntent().getIntExtra("position", 0);
            productArrayList = HomeFragment.sectionList.get(position).getProductList();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mAdapter = new ProductLoadMoreAdapter(ProductListActivity.this, productArrayList, recyclerView, R.layout.lyt_item_list);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            }, 1500);

        }
        mSwipeRefreshLayout = findViewById(R.id.swipeLayout);
        // mSwipeRefreshLayout.setColorSchemeColors(Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ReLoadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lay_menu.getVisibility() == View.VISIBLE) {
                    lay_menu.setVisibility(View.GONE);
                } else {
                    lay_menu.setVisibility(View.VISIBLE);
                }
            }
        });
        lay_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lay_menu.setVisibility(View.GONE);
            }
        });

        lay_filter_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lay_menu.setVisibility(View.VISIBLE);
            }
        });
        rad_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                filterIndex = i;
                switch (i) {
                    case R.id.new_to_old:
                        filterBy = Constant.NEW;
                        break;
                    case R.id.old_to_new:
                        filterBy = Constant.OLD;
                        break;
                    case R.id.high_to_low:
                        filterBy = Constant.HIGH;
                        break;
                    case R.id.low_to_high:
                        filterBy = Constant.LOW;
                        break;
                }
                try {
                    ReLoadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lay_menu.setVisibility(View.GONE);
            }
        });
    }

    private void GetData(final int startoffset) {
        Map<String, String> params = new HashMap<>();
        if (from.equals("item")) {
            params.put("category_id", id);
        } else {
            params.put(Constant.SUB_CATEGORY_ID, id);
        }
        params.put(Constant.LIMIT, Constant.LOAD_ITEM_LIMIT);
        params.put(Constant.OFFSET, startoffset + "");
        if (filterIndex != -1)
            params.put(Constant.SORT, filterBy);
        System.out.println("====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        //    System.out.println("====product  " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            total = Integer.parseInt(objectbject.getString(Constant.TOTAL));
                            if (startoffset == 0) {
                                productArrayList = new ArrayList<>();
                                tvAlert.setVisibility(View.GONE);
                            }
                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            if (startoffset == 0) {
                                mAdapter = new ProductLoadMoreAdapter(ProductListActivity.this, productArrayList, recyclerView, R.layout.lyt_item_list);
                                mAdapter.setHasStableIds(true);
                                recyclerView.setAdapter(mAdapter);
                                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        // if (diff == 0) {
                                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (productArrayList.size() < total) {
                                                if (!isLoadMore) {
                                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productArrayList.size() - 1) {
                                                        //bottom of list!
                                                        productArrayList.add(null);
                                                        mAdapter.notifyItemInserted(productArrayList.size() - 1);
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                offset = offset + Integer.parseInt(Constant.LOAD_ITEM_LIMIT);
                                                                Map<String, String> params = new HashMap<>();
                                                                if(from.equals("item")){
                                                                    params.put("category_id", id);
                                                                }else {
                                                                    params.put(Constant.SUB_CATEGORY_ID, id);
                                                                }                                                                params.put(Constant.LIMIT, Constant.LOAD_ITEM_LIMIT);
                                                                params.put(Constant.OFFSET, offset + "");
                                                                if (filterIndex != -1)
                                                                    params.put(Constant.SORT, filterBy);

                                                                ApiConfig.RequestToVolley(new VolleyCallback() {
                                                                    @Override
                                                                    public void onSuccess(boolean result, String response) {

                                                                        if (result) {
                                                                            try {
                                                                                // System.out.println("====product  " + response);
                                                                                JSONObject objectbject = new JSONObject(response);
                                                                                if (!objectbject.getBoolean(Constant.ERROR)) {

                                                                                    JSONObject object = new JSONObject(response);
                                                                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                                                                    productArrayList.remove(productArrayList.size() - 1);
                                                                                    mAdapter.notifyItemRemoved(productArrayList.size());

                                                                                    if (productArrayList.contains(null)) {
                                                                                        for (int i = 0; i < productArrayList.size(); i++) {
                                                                                            if (productArrayList.get(i) == null) {
                                                                                                productArrayList.remove(i);
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                                                                                    mAdapter.notifyDataSetChanged();
                                                                                    mAdapter.setLoaded();
                                                                                    isLoadMore = false;
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                }, ProductListActivity.this, GET_PRODUCT_URL, params, false);

                                                            }
                                                        }, 2000);
                                                        isLoadMore = true;
                                                    }

                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            if (startoffset == 0) {
                                tvAlert.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProductListActivity.this, GET_PRODUCT_URL, params, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (!Constant.SELECTEDPRODUCT_POS.equals(""))
                if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                    mAdapter.notifyItemChanged(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                    Constant.SELECTEDPRODUCT_POS = "";
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (from.equals("section"))
            menu.findItem(R.id.menu_sort).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart_new, ProductListActivity.this));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_search:
                startActivity(new Intent(ProductListActivity.this, SearchActivity.class).putExtra("from", Constant.FROMSEARCH));
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
                return true;
            case R.id.menu_sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductListActivity.this);
                builder.setTitle(ProductListActivity.this.getResources().getString(R.string.filterby));
                builder.setSingleChoiceItems(Constant.filtervalues, filterIndex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        filterIndex = item;
                        switch (item) {
                            case 0:
                                filterBy = Constant.NEW;
                                break;
                            case 1:
                                filterBy = Constant.OLD;
                                break;
                            case 2:
                                filterBy = Constant.HIGH;
                                break;
                            case 3:
                                filterBy = Constant.LOW;
                                break;
                        }
                        try {
                            ReLoadData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }
        return onOptionsItemSelected(item);
    }

    private void ReLoadData() throws Exception {
        if (AppController.isConnected(ProductListActivity.this)) {
            GetData(0);
        }
    }
}

