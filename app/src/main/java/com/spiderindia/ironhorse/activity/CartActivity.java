package com.spiderindia.ironhorse.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.CartListAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    public static LinearLayout lytempty;
    static TextView txttotal, txtstotal, txtdeliverycharge, txtsubtotal,storeclosedLabel;
    RecyclerView cartrecycleview;
    static DatabaseHelper databaseHelper;
    ArrayList<Product> productArrayList;
    static CartListAdapter cartListAdapter;
    public static RelativeLayout lyttotal;
    double total;
    ProgressBar progressbar;
    static Activity activity;
    static Session session;
    Button btnShowNow;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart));
        Fresco.initialize(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(CartActivity.this);
        progressbar = findViewById(R.id.progressbar);
        lyttotal = findViewById(R.id.lyttotal);
        lytempty = findViewById(R.id.lytempty);
        btnShowNow = findViewById(R.id.btnShowNow);
        txttotal = findViewById(R.id.txttotal);
        txtsubtotal = findViewById(R.id.txtsubtotal);
        txtdeliverycharge = findViewById(R.id.txtdeliverycharge);
        txtstotal = findViewById(R.id.txtstotal);
        storeclosedLabel= findViewById(R.id.storeclosedLabel);
        cartrecycleview = findViewById(R.id.cartrecycleview);
        cartrecycleview.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        databaseHelper = new DatabaseHelper(CartActivity.this);
        activity = CartActivity.this;
ApiConfig.GetStoreOpenSetting(CartActivity.this);
        ApiConfig.GetPaymentConfig(CartActivity.this);
        try {
            getData();
        } catch (Exception e) {
            e.printStackTrace();

        }

        if(Constant.StoreOpenStatus.equals("0"))
        {
            lyttotal.setVisibility(View.GONE);
        }

        if(Constant.StoreOpenStatus.equals("0"))
        {
            storeclosedLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            storeclosedLabel.setVisibility(View.GONE);
        }
        lyttotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isUserLoggedIn()) {

                    startActivity(new Intent(CartActivity.this, AddressShowActivity.class));
                   // startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                } else {
                    startActivity(new Intent(CartActivity.this, LoginActivity.class).putExtra("fromto", "checkout"));
                }
            }
        });

        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    public static void SetDataTotal() {
        try {
            double total = databaseHelper.getTotalCartAmt(session);
            String displaytotal = DatabaseHelper.decimalformatData.format(total);
            if (cartListAdapter.getItemCount() == 1) {
                txttotal.setText(activity.getResources().getString(R.string.total_) + cartListAdapter.getItemCount() + activity.getResources().getString(R.string._item_) + Constant.SETTING_CURRENCY_SYMBOL + " " + displaytotal);
            } else
                txttotal.setText(activity.getResources().getString(R.string.total_) + cartListAdapter.getItemCount() + activity.getResources().getString(R.string._items_) + Constant.SETTING_CURRENCY_SYMBOL + " " + displaytotal);

            txtstotal.setText(Constant.SETTING_CURRENCY_SYMBOL + displaytotal);

            double subtotal = total;
            if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
                subtotal = subtotal + 0;
            }
            txtsubtotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private void getData() throws Exception {
        total = 0.00;
        productArrayList = new ArrayList<>();
        final ArrayList<String> idslist = databaseHelper.getCartList();
        if (idslist.isEmpty()) {
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);

            if(Constant.StoreOpenStatus.equals("0"))
            {
                lyttotal.setVisibility(View.GONE);
            }
            cartrecycleview.setAdapter(new CartListAdapter(productArrayList, CartActivity.this));
        } else {
            progressbar.setVisibility(View.VISIBLE);
            int i = 1;
            for (final String id : idslist) {
                final String[] ids = id.split("=");
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.PRODUCT_ID, ids[0]);

                final int finalI = i;
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        System.out.println("=================*cart- " + response + " == " + id);
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    JSONObject object = new JSONObject(response);
                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                    Product product = ApiConfig.GetCartList(jsonArray, ids[1], ids[2], databaseHelper);
                                    if (product != null) {
                                        productArrayList.add(product);
                                    }
                                    if (finalI == idslist.size()) {
                                        lyttotal.setVisibility(View.VISIBLE);

                                        if(Constant.StoreOpenStatus.equals("0"))
                                        {
                                            lyttotal.setVisibility(View.GONE);
                                        }

                                        cartListAdapter = new CartListAdapter(productArrayList, CartActivity.this);
                                        cartrecycleview.setAdapter(cartListAdapter);
                                        SetDataTotal();
                                        progressbar.setVisibility(View.GONE);
                                    }
                                } else {
                                    databaseHelper.DeleteOrderData(ids[1], ids[0]);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, CartActivity.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
                i++;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper.getTotalItemOfCart() == 0) {
            lytempty.setVisibility(View.VISIBLE);
            lyttotal.setVisibility(View.GONE);
            activity.invalidateOptionsMenu();
            if (cartrecycleview != null) {
                productArrayList = new ArrayList<>();
                cartrecycleview.setAdapter(new CartListAdapter(productArrayList, CartActivity.this));
            }





        }
        if(Constant.StoreOpenStatus.equals("0"))
        {
            lyttotal.setVisibility(View.GONE);
        }
        if(Constant.StoreOpenStatus.equals("0"))
        {
            storeclosedLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            storeclosedLabel.setVisibility(View.GONE);
        }


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
