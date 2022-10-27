package com.spiderindia.ironhorse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.fragment.OrderTrackerListFragment;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.OrderTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class OrderListActivity extends AppCompatActivity {
    LinearLayout lytempty, lytdata;
    public static ArrayList<OrderTracker> orderTrackerslist, cancelledlist, deliveredlist, processedlist, shippedlist, returnedList;
    Session session;
    String[] tabs;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.order_track));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(getApplicationContext());
        tabs = new String[]{getString(R.string.all), getString(R.string.in_process1), getString(R.string.shipped1), getString(R.string.delivered1), getString(R.string.cancelled1), getString(R.string.returned1)};
        lytempty = findViewById(R.id.lytempty);
        lytdata = findViewById(R.id.lytdata);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);


        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = findViewById(R.id.tablayout);
        GetOrderDetails();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void GetOrderDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GETORDERS, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));

        orderTrackerslist = new ArrayList<>();
        cancelledlist = new ArrayList<>();
        deliveredlist = new ArrayList<>();
        processedlist = new ArrayList<>();
        shippedlist = new ArrayList<>();
        returnedList = new ArrayList<>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            lytdata.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String laststatusname = null, laststatusdate = null;
                                JSONArray statusarray = jsonObject.getJSONArray("status");
                                ArrayList<OrderTracker> statusarraylist = new ArrayList<>();

                                int cancel = 0, delivered = 0, process = 0, shipped = 0, returned = 0;
                                for (int k = 0; k < statusarray.length(); k++) {
                                    JSONArray sarray = statusarray.getJSONArray(k);
                                    String sname = sarray.getString(0);
                                    String sdate = sarray.getString(1);

                                    statusarraylist.add(new OrderTracker(sname, sdate));
                                    laststatusname = sname;
                                    laststatusdate = sdate;

                                    if (sname.equalsIgnoreCase("cancelled")) {
                                        cancel = 1;
                                        delivered = 0;
                                        process = 0;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("delivered")) {
                                        delivered = 1;
                                        process = 0;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("processed")) {
                                        process = 1;
                                        shipped = 0;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("shipped")) {
                                        shipped = 1;
                                        returned = 0;
                                    } else if (sname.equalsIgnoreCase("returned")) {

                                        returned = 1;
                                    }
                                }

                                ArrayList<OrderTracker> itemList = new ArrayList<>();
                                JSONArray itemsarray = jsonObject.getJSONArray("items");

                                for (int j = 0; j < itemsarray.length(); j++) {

                                    JSONObject itemobj = itemsarray.getJSONObject(j);
                                    double productPrice = 0.0;
                                    if (itemobj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                                        productPrice = (Double.parseDouble(itemobj.getString(Constant.PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                                    else {
                                        productPrice = (Double.parseDouble(itemobj.getString(Constant.DISCOUNTED_PRICE)) * Integer.parseInt(itemobj.getString(Constant.QUANTITY)));
                                    }
                                    JSONArray statusarray1 = itemobj.getJSONArray("status");
                                    ArrayList<OrderTracker> statusList = new ArrayList<>();

                                    for (int k = 0; k < statusarray1.length(); k++) {
                                        JSONArray sarray = statusarray1.getJSONArray(k);
                                        String sname = sarray.getString(0);
                                        String sdate = sarray.getString(1);
                                        statusList.add(new OrderTracker(sname, sdate));
                                    }
                                    itemList.add(new OrderTracker(itemobj.getString(Constant.ID),
                                            itemobj.getString(Constant.ORDER_ID),
                                            itemobj.getString(Constant.PRODUCT_VARIANT_ID),
                                            itemobj.getString(Constant.QUANTITY),
                                            String.valueOf(productPrice),
                                            itemobj.getString(Constant.DISCOUNT),
                                            itemobj.getString(Constant.SUB_TOTAL),
                                            itemobj.getString(Constant.DELIVER_BY),
                                            itemobj.getString(Constant.NAME),
                                            itemobj.getString(Constant.IMAGE),
                                            itemobj.getString(Constant.MEASUREMENT),
                                            itemobj.getString(Constant.UNIT),
                                            jsonObject.getString(Constant.PAYMENT_METHOD),
                                            itemobj.getString(Constant.ACTIVE_STATUS),
                                            itemobj.getString(Constant.DATE_ADDED),
                                            statusList,itemobj.getString("rating"),
                                            itemobj.getString("rating_desc"),itemobj.getString("product_id")));
                                }

                                String email = "", deliverytime= "";
                                try{
                                    email =  jsonObject.getString("user_email");
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                try{
                                    deliverytime =  jsonObject.getString("delivery_time");
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                OrderTracker orderTracker = new OrderTracker(
                                        jsonObject.getString(Constant.USER_ID),
                                        jsonObject.getString(Constant.ID),
                                        jsonObject.getString(Constant.DATE_ADDED),
                                        laststatusname, laststatusdate,
                                        statusarraylist,
                                        jsonObject.getString(Constant.MOBILE),
                                        jsonObject.getString(Constant.DELIVERY_CHARGE),
                                        jsonObject.getString(Constant.PAYMENT_METHOD),
                                        jsonObject.getString(Constant.ADDRESS),
                                        jsonObject.getString(Constant.TOTAL),
                                        jsonObject.getString(Constant.FINAL_TOTAL),
                                        jsonObject.getString(Constant.TAX_AMOUNT),
                                        jsonObject.getString(Constant.TAX_PERCENT),
                                        jsonObject.getString(Constant.KEY_WALLET_BALANCE),
                                        jsonObject.getString(Constant.PROMO_CODE),
                                        jsonObject.getString(Constant.PROMO_DISCOUNT),
                                        jsonObject.getString(Constant.DISCOUNT),
                                        jsonObject.getString(Constant.DISCOUNT_AMT),
                                        jsonObject.getString(Constant.USER_NAME), itemList,
                                        email,deliverytime);
                                orderTrackerslist.add(orderTracker);
                                if (cancel == 1)
                                    cancelledlist.add(orderTracker);
                                if (delivered == 1)
                                    deliveredlist.add(orderTracker);
                                if (process == 1)
                                    processedlist.add(orderTracker);
                                if (shipped == 1)
                                    shippedlist.add(orderTracker);
                                if (returned == 1)
                                    returnedList.add(orderTracker);
                            }
                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);
                        } else {
                            lytempty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, OrderListActivity.this, Constant.ORDERPROCESS_URL, params, true);

       /* try {
            progressbar.setVisibility(View.VISIBLE);
            Call<myorders> call = apiInterface.Myorders(Constant.AccessKeyVal, Constant.GetVal,session.getData(Session.KEY_ID));

            call.enqueue(new Callback<myorders>() {
                @Override
                public void onResponse(Call<myorders> call, @NotNull retrofit2.Response<myorders> response) {
                    progressbar.setVisibility(View.GONE);
                    myorders reqTutorDTO = response.body();
                    if (reqTutorDTO != null) {
                        if (!reqTutorDTO.toString().equals(Constant.ERROR)) {
                            //  Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                lytdata.setVisibility(View.VISIBLE);
                                ArrayList<Orders> orders = new ArrayList<Orders>();
                                orders = (ArrayList<Orders>) reqTutorDTO.getData();
                                for (int i = 0; i < orders.size(); i++) {
                                    //JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    String laststatusname = null, laststatusdate = null;
                                    List<List<String>> statusarray = orders.get(i).getStatus();
                                    ArrayList<OrderTracker> statusarraylist = new ArrayList<>();

                                    int cancel = 0, delivered = 0, process = 0, shipped = 0, returned = 0;
                                    for (int k = 0; k < statusarray.size(); k++) {
                                        //JSONArray sarray = statusarray.getJSONArray(k);
                                        List<String> sarray = statusarray.get(k);
                                        String sname = sarray.get(0).toString();
                                        String sdate = sarray.get(1).toString();

                                        statusarraylist.add(new OrderTracker(sname, sdate));
                                        laststatusname = sname;
                                        laststatusdate = sdate;

                                        if (sname.equalsIgnoreCase("cancelled")) {
                                            cancel = 1;
                                            delivered = 0;
                                            process = 0;
                                            shipped = 0;
                                            returned = 0;
                                        } else if (sname.equalsIgnoreCase("delivered")) {
                                            delivered = 1;
                                            process = 0;
                                            shipped = 0;
                                            returned = 0;
                                        } else if (sname.equalsIgnoreCase("processed")) {
                                            process = 1;
                                            shipped = 0;
                                            returned = 0;
                                        } else if (sname.equalsIgnoreCase("shipped")) {
                                            shipped = 1;
                                            returned = 0;
                                        } else if (sname.equalsIgnoreCase("returned")) {

                                            returned = 1;
                                        }
                                    }

                                    ArrayList<OrderTracker> itemList = new ArrayList<>();
                                    //JSONArray itemsarray = jsonObject.getJSONArray("items");

                                    List<Item> items = orders.get(i).getItems();

                                    for (int j = 0; j < items.size(); j++) {

                                       // JSONObject itemobj = itemsarray.getJSONObject(j);
                                        double productPrice = 0.0;
                                        if (items.get(j).getDiscountedPrice().equals("0"))
                                            productPrice = (Double.parseDouble(items.get(j).getPrice()) * Integer.parseInt(items.get(j).getQuantity()));
                                        else {
                                            productPrice = (Double.parseDouble(items.get(j).getDiscountedPrice()) *
                                                    Integer.parseInt(items.get(j).getQuantity()));
                                        }
                                        //JSONArray statusarray1 = itemobj.getJSONArray("status");

                                        List<List<String>> statusarray1 = orders.get(i).getStatus();
                                        ArrayList<OrderTracker> statusList = new ArrayList<>();

                                        for (int k = 0; k < statusarray1.size(); k++) {
                                           // JSONArray sarray = statusarray1.getJSONArray(k);

                                            List<String> sarray = statusarray.get(k);
                                            String sname = sarray.get(0);
                                            String sdate = sarray.get(1);
                                            statusList.add(new OrderTracker(sname, sdate));
                                        }
                                        String Delivery_by = "";
                                        try {
                                            Delivery_by =  items.get(j).getDeliverBy().toString().toString();
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                            Delivery_by = "";

                                        }
                                        itemList.add(new OrderTracker(items.get(j).getId(),
                                                items.get(j).getOrderId(),
                                                items.get(j).getProductVariantId(),
                                                items.get(j).getQuantity(),
                                                String.valueOf(productPrice),
                                                items.get(j).getDiscount(),
                                                items.get(j).getSubTotal(),
                                                Delivery_by,
                                                items.get(j).getName(),
                                                items.get(j).getImage(),
                                                items.get(j).getMeasurement(),
                                                items.get(j).getUnit(),
                                              //  jsonObject.getString(Constant.PAYMENT_METHOD),
                                                orders.get(i).getPaymentMethod(),
                                                items.get(j).getActiveStatus(),
                                                items.get(j).getDateAdded(),
                                                statusList,items.get(j).getRating(),
                                                items.get(j).getRatingDesc(),items.get(j).getProductId()));
                                    }
                                    OrderTracker orderTracker = new OrderTracker(
                                            orders.get(i).getUserId(),
                                            orders.get(i).getId(),
                                            orders.get(i).getDateAdded(),
                                            laststatusname, laststatusdate,
                                            statusarraylist,
                                            orders.get(i).getMobile(),
                                            orders.get(i).getDeliveryCharge(),
                                            orders.get(i).getPaymentMethod(),
                                            orders.get(i).getAddress(),
                                            orders.get(i).getTotal(),
                                            orders.get(i).getFinalTotal(),
                                            orders.get(i).getTaxAmount(),
                                            orders.get(i).getTaxPercentage(),
                                            orders.get(i).getWalletBalance(),
                                            orders.get(i).getPromoCode(),
                                            orders.get(i).getPromoDiscount(),
                                            orders.get(i).getDiscount(),
                                            orders.get(i).getDiscountRupees(),
                                            orders.get(i).getUserName(), itemList);
                                    orderTrackerslist.add(orderTracker);
                                    if (cancel == 1)
                                        cancelledlist.add(orderTracker);
                                    if (delivered == 1)
                                        deliveredlist.add(orderTracker);
                                    if (process == 1)
                                        processedlist.add(orderTracker);
                                    if (shipped == 1)
                                        shippedlist.add(orderTracker);
                                    if (returned == 1)
                                        returnedList.add(orderTracker);
                                }
                                setupViewPager(viewPager);
                                tabLayout.setupWithViewPager(viewPager);
                            } else {
                                lytempty.setVisibility(View.VISIBLE);
                            }
                        }
                     else {
                        Toast.makeText(getApplicationContext(), "Invalid Response from Server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<myorders> call, Throwable t) {
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Invalid Response from Server", Toast.LENGTH_SHORT).show();
                    call.cancel();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OrderTrackerListFragment(), tabs[0]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[1]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[2]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[3]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[4]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[5]);
        viewPager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            OrderTrackerListFragment fragment = new OrderTrackerListFragment();
            data.putInt("pos", position);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.isOrderCancelled || Constant.isOrderRating) {
            GetOrderDetails();
            Constant.isOrderCancelled = false;
            Constant.isOrderRating = false;
        }
    }


    public void OnBtnClick(View view) {
        finish();
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