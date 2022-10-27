package com.spiderindia.ironhorse.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.ItemsAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.OrderTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrackerDetailActivity extends AppCompatActivity  implements OnItemClick {

    OrderTracker order;
    TextView tvItemTotal, tvTaxPercent, tvTaxAmt, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal,
            tvDPercent, tvDAmount, txtdeliverytime;
    TextView txtcanceldetail, txtotherdetails, txtorderid, txtorderdate, btnpay, txtemail;
    NetworkImageView imgorder;
    SpannableString spannableString;
    Toolbar toolbar;
    public static ProgressBar pBar;
    RecyclerView recyclerView;
    public static Button btnCancel;
    public static LinearLayout lyttracker;
    View l4;
    LinearLayout returnLyt, lytPromo, lytWallet, lytPriceDetail, lytDiscount;
    double totalAfterTax = 0.0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_detail);

        order = (OrderTracker) getIntent().getSerializableExtra("model");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pBar = findViewById(R.id.pBar);
        lytPriceDetail = findViewById(R.id.lytPriceDetail);
        lytDiscount = findViewById(R.id.lytDiscount);
        lytPromo = findViewById(R.id.lytPromo);
        lytWallet = findViewById(R.id.lytWallet);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvDAmount = findViewById(R.id.tvDAmount);
        tvDPercent = findViewById(R.id.tvDPercent);
        tvTotal = findViewById(R.id.tvTotal);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvWallet = findViewById(R.id.tvWallet);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        txtorderid = findViewById(R.id.txtorderid);
        txtorderdate = findViewById(R.id.txtorderdate);
        btnpay = findViewById(R.id.btnpay);
        imgorder = findViewById(R.id.imgorder);
        txtotherdetails = findViewById(R.id.txtotherdetails);
        txtcanceldetail = findViewById(R.id.txtcanceldetail);
        lyttracker = findViewById(R.id.lyttracker);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        btnCancel = findViewById(R.id.btncancel);
        l4 = findViewById(R.id.l4);
        txtemail = findViewById(R.id.txtemail);
        returnLyt = findViewById(R.id.returnLyt);
        txtdeliverytime = findViewById(R.id.txtdeliverytime);

        ApiConfig.GetPaymentConfig(TrackerDetailActivity.this);

        String[] date = order.getDate_added().split("\\s+");
        txtorderid.setText(order.getOrder_id());
        txtorderdate.setText(date[0]);
        txtotherdetails.setText(getString(R.string.name_1) + order.getUsername() + getString(R.string.mobile_no_1) +
                order.getMobile() + getString(R.string.address_1) + order.getAddress() );//+"\n Email : "
        totalAfterTax = (Double.parseDouble(order.getTotal()) +
                Double.parseDouble(order.getDelivery_charge()) +
                Double.parseDouble(order.getTax_amt()));

        txtemail.setText("GMail : "+order.getGmail().toString().trim());
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getTotal());
        tvDeliveryCharge.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getDelivery_charge());
        tvTaxPercent.setText(getString(R.string.tax) + "(" + order.getTax_percent() + "%) :");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getTax_amt());
        tvDPercent.setText(getString(R.string.discount) + "(" + order.getdPercent() + "%) :");
        tvDAmount.setText(" " + Constant.SETTING_CURRENCY_SYMBOL + order.getdAmount());
        if(order.getdAmount().equals("0")){
            lytDiscount.setVisibility(View.GONE);
        }
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount.setText(" " + Constant.SETTING_CURRENCY_SYMBOL + order.getPromoDiscount());
        if(order.getPromoDiscount().equals("0")){
            lytPromo.setVisibility(View.GONE);
        }
        tvWallet.setText(" " + Constant.SETTING_CURRENCY_SYMBOL + order.getWalletBalance());
        if(order.getWalletBalance().equals("0")){
            lytWallet.setVisibility(View.GONE);
        }
        tvFinalTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getFinal_total());
        if (!order.getStatus().equalsIgnoreCase("delivered") && !order.getStatus().equalsIgnoreCase("cancelled")
                && !order.getStatus().equalsIgnoreCase("returned") &&
                !order.getStatus().equalsIgnoreCase("shipped")) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        if (order.getPayment_method().equals("pending")) {
            btnpay.setVisibility(View.VISIBLE);
        } else {
            btnpay.setVisibility(View.GONE);
        }

        if (order.getStatus().equalsIgnoreCase("cancelled")) {
            lyttracker.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            txtcanceldetail.setVisibility(View.VISIBLE);
            txtcanceldetail.setText(getString(R.string.canceled_on) + order.getStatusdate());
            lytPriceDetail.setVisibility(View.GONE);
        } else {
            lytPriceDetail.setVisibility(View.VISIBLE);
            if (order.getStatus().equals("returned")) {
                l4.setVisibility(View.VISIBLE);
                returnLyt.setVisibility(View.VISIBLE);
            }
            lyttracker.setVisibility(View.VISIBLE);
            for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
                int img = getResources().getIdentifier("img" + i, "id", getPackageName());
                int view = getResources().getIdentifier("l" + i, "id", getPackageName());
                int txt = getResources().getIdentifier("txt" + i, "id", getPackageName());
                int textview = getResources().getIdentifier("txt" + i + "" + i, "id", getPackageName());
                // System.out.println("===============" + img + " == " + view);
                if (img != 0 && findViewById(img) != null) {
                    ImageView imageView = findViewById(img);
                    imageView.setColorFilter(getResources().getColor(R.color.new_green));
                }

                if (view != 0 && findViewById(view) != null) {
                    View view1 = findViewById(view);
                    view1.setBackgroundColor(getResources().getColor(R.color.new_green));
                }

                if (txt != 0 && findViewById(txt) != null) {
                    TextView view1 = findViewById(txt);
                    view1.setTextColor(getResources().getColor(R.color.black));
                }

                if (textview != 0 && findViewById(textview) != null) {
                    TextView view1 = findViewById(textview);
                    String str = order.getOrderStatusArrayList().get(i).getStatusdate();
                    String[] splited = str.split("\\s+");
                    view1.setText(splited[0] + "\n" + splited[1]);
                }
            }
        }
        recyclerView.setAdapter(new ItemsAdapter(TrackerDetailActivity.this, order.itemsList, "detail",
                TrackerDetailActivity.this));


        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackCheckoutActivity.class);
                intent.putExtra("Order_id", order.getOrder_id());
                intent.putExtra("payamount", order.getFinal_total());
                startActivity(intent);
            }
        });

       txtdeliverytime.setText("Delivery Time  : "+order.getDeliverytime().toString().trim());

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btncancel) {

            new AlertDialog.Builder(TrackerDetailActivity.this)
                    .setTitle(getString(R.string.cancel_order))
                    .setMessage(getString(R.string.cancel_msg))
                    .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Constant.UPDATE_ORDER_STATUS, Constant.GetVal);
                            params.put(Constant.ID, order.getOrder_id());
                            params.put(Constant.STATUS, Constant.CANCELLED);
                            pBar.setVisibility(View.VISIBLE);
                            ApiConfig.RequestToVolley(new VolleyCallback() {
                                @Override
                                public void onSuccess(boolean result, String response) {
                                    // System.out.println("=================*cancelorder- " + response);
                                    if (result) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (!object.getBoolean(Constant.ERROR)) {
                                                Constant.isOrderCancelled = true;
                                                finish();
                                                ApiConfig.getWalletBalance(TrackerDetailActivity.this, new Session(TrackerDetailActivity.this));
                                            }
                                            Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                                            pBar.setVisibility(View.GONE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, TrackerDetailActivity.this, Constant.ORDERPROCESS_URL, params, false);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(String type, String amount, String id) {
        order.setTotal( String.valueOf(Double.parseDouble(order.getTotal()) - Double.parseDouble(amount)));
        order.setFinal_total( String.valueOf(Double.parseDouble(order.getFinal_total()) - Double.parseDouble(amount)));
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getTotal());
        totalAfterTax = (Double.parseDouble(order.getTotal()) +
                Double.parseDouble(order.getDelivery_charge()) +
                Double.parseDouble(order.getTax_amt()));
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvFinalTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getFinal_total());
        if(order.getTotal().equals("0.0") || order.getTotal().equals("0")  ){
            lyttracker.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            txtcanceldetail.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            txtcanceldetail.setText(getString(R.string.canceled_on) + order.getStatusdate());
            lytPriceDetail.setVisibility(View.GONE);
        }
    }
}
