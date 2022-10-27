package com.spiderindia.ironhorse.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.instamojo.android.Instamojo;
import com.instamojo.android.helpers.Constants;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.helper.PaymentModelClass;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Slot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback, PaymentResultListener ,
        Instamojo.InstamojoPaymentCallback {
    private String TAG = CheckoutActivity.class.getSimpleName();
    public Toolbar toolbar;
    public TextView tvDeliveryText,tvTaxPercent, tvTaxAmt, tvDelivery, tvPayment, tvLocation, tvAlert, tvWltBalance, tvLoyaltyBalance,tvCity, tvName,tvMobile, tvTotal, tvDeliveryCharge,
            tvSubTotal, tvCurrent, tvWallet,tvLoyalty, tvPromoCode, tvPCAmount, tvPlaceOrder, tvConfirmOrder, tvPreTotal, tvEmail;
    LinearLayout lytPayOption, lytTax, lytOrderList, lytWallet,lytLoyalty, lytCLocation, paymentLyt, deliveryLyt, lytPayU, lytPayPal,
            lytRazorPay, lytUpi,dayLyt, lytCod;
    Button btnApply;
    EditText edtPromoCode;
    public ProgressBar prgLoading;
    Session session;
    JSONArray qtyList, variantIdList, nameList;
    DatabaseHelper databaseHelper;
    double total, subtotal;
    String deliveryCharge = "0";
    PaymentModelClass paymentModelClass;
    SupportMapFragment mapFragment;
    CheckBox chWallet,chLoyalty, chHome, chWork;
    public RadioButton rToday, rTomorrow;
    String deliveryTime = "", deliveryDay = "", pCode = "", paymentMethod = "", label = "", appliedCode = "";
    RadioButton rbCod, rbPayU, rbPayPal, rbRazorPay,rbUpi, rbUpi2, rbInsta;
    ProgressDialog mProgressDialog;
    RelativeLayout walletLyt, mainLayout,LoyaltyLyt;
    Map<String, String> razorParams,upiParams;
    public String razorPayId;
    double usedBalance = 0;
    double usedPoints = 0;

    RecyclerView recyclerView;
    ArrayList<Slot> slotList;
    SlotAdapter adapter;
    ProgressBar pBar;
    public boolean isApplied;
    double taxAmt = 0.0;
    double dCharge = 0.0, pCodeDiscount = 0.0;
    final int UPI_PAYMENT = 0;
    AppCompatSpinner deliveryTypespinner;
    String selectedDeliveryType = "EMPTY";
    CardView deliveryCardView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Checkout");
        toolbar.setNavigationIcon(R.drawable.ic_lefy_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        deliveryTypespinner = findViewById(R.id.deliveryTypeSpinner);
        deliveryCardView = findViewById(R.id.layout_delivery_type);
        paymentModelClass = new PaymentModelClass(CheckoutActivity.this);
        databaseHelper = new DatabaseHelper(CheckoutActivity.this);
        session = new Session(CheckoutActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pBar = findViewById(R.id.pBar);
        lytTax = findViewById(R.id.lytTax);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        dayLyt = findViewById(R.id.dayLyt);
        rbCod = findViewById(R.id.rbcod);
        rbPayU = findViewById(R.id.rbPayU);
        rbUpi= findViewById(R.id.rbUpi);
        rbUpi2= findViewById(R.id.rbUpi2);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbRazorPay = findViewById(R.id.rbRazorPay);
        rbInsta = findViewById(R.id.rbInsta);
        tvLocation = findViewById(R.id.tvLocation);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvPayment = findViewById(R.id.tvPayment);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvAlert = findViewById(R.id.tvAlert);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        lytPayPal = findViewById(R.id.lytPayPal);
        lytRazorPay = findViewById(R.id.lytRazorPay);
        lytCod = findViewById(R.id.lytCod);
        lytUpi = findViewById(R.id.lytUpi);
        lytPayU = findViewById(R.id.lytPayU);
        chWallet = findViewById(R.id.chWallet);
        chLoyalty = findViewById(R.id.chLoyalty);

        tvEmail = findViewById(R.id.tvEmail);
        chHome = findViewById(R.id.chHome);
        chWork = findViewById(R.id.chWork);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotal = findViewById(R.id.tvTotal);
        tvName = findViewById(R.id.tvName);
        tvMobile = findViewById(R.id.tvMobile);
        tvDeliveryText = findViewById(R.id.tvDeliveryText);
        tvCity = findViewById(R.id.tvCity);
        tvCurrent = findViewById(R.id.tvCurrent);
        lytPayOption = findViewById(R.id.lytPayOption);
        lytOrderList = findViewById(R.id.lytOrderList);
        lytCLocation = findViewById(R.id.lytCLocation);
        lytWallet = findViewById(R.id.lytWallet);
        walletLyt = findViewById(R.id.walletLyt);
        LoyaltyLyt = findViewById(R.id.LoyaltyLyt);
        paymentLyt = findViewById(R.id.paymentLyt);
        deliveryLyt = findViewById(R.id.deliveryLyt);
        tvWallet = findViewById(R.id.tvWallet);
        prgLoading = findViewById(R.id.prgLoading);
        tvPlaceOrder = findViewById(R.id.tvPlaceOrder);
        tvConfirmOrder = findViewById(R.id.tvConfirmOrder);
        lytWallet.setVisibility(View.GONE);
        rToday = findViewById(R.id.rToday);
        rTomorrow = findViewById(R.id.rTomorrow);
        tvWltBalance = findViewById(R.id.tvWltBalance);
        tvLoyaltyBalance = findViewById(R.id.tvLoyaltyBalance);
        tvPreTotal = findViewById(R.id.tvPreTotal);
        btnApply = findViewById(R.id.btnApply);
        tvLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_my_location, 0, 0, 0);
        tvCurrent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_address, 0, 0, 0);
        tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
        tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
        tvPreTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info, 0, 0, 0);
        ApiConfig.getWalletBalance(CheckoutActivity.this, session);
        GetTimeSlots();
       // SetDeliveryTypeSpinnerData();
        try {
            qtyList = new JSONArray(session.getData(Session.KEY_Orderqty));
            variantIdList = new JSONArray(session.getData(Session.KEY_Ordervid));
            nameList = new JSONArray(session.getData(Session.KEY_Ordername));
            for (int i = 0; i < nameList.length(); i++) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setWeightSum(4f);
                String[] name = nameList.getString(i).split("==");
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/montserrat_regular.ttf");
                TextView tv1 = new TextView(this);
                //tv1.setText(name[1] + " (" + name[0] + ")");
                tv1.setText(name[1]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1.5f;
                tv1.setPadding(0, 10, 0, 15);
                tv1.setTextSize(13);
                tv1.setTypeface(face);
                tv1.setLayoutParams(lp);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                linearLayout.addView(tv1);

                TextView tv2 = new TextView(this);
                tv2.setText(qtyList.getString(i));
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 0.7f;
                tv2.setTextSize(13);
                tv2.setTypeface(face);
                tv2.setPadding(0, 10, 0, 15);
                tv2.setLayoutParams(lp1);
                tv2.setGravity(Gravity.CENTER);
                linearLayout.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(Constant.SETTING_CURRENCY_SYMBOL + name[2]);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 0.8f;
                tv3.setTextSize(13);
                tv3.setPadding(0, 10, 0, 15);
                tv3.setTypeface(face);
                tv3.setLayoutParams(lp2);
                tv3.setGravity(Gravity.CENTER);
                linearLayout.addView(tv3);

                TextView tv4 = new TextView(this);
                tv4.setText(Constant.SETTING_CURRENCY_SYMBOL + name[3]);
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp3.weight = 1f;
                tv4.setTextSize(13);
                tv4.setTypeface(face);
                tv4.setPadding(0, 10, 0, 15);
                tv4.setLayoutParams(lp3);
                tv4.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                linearLayout.addView(tv4);
                lytOrderList.addView(linearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SetDataTotal();
        chWallet.setTag("false");
        chLoyalty.setTag("false");

        getWalletBalance();
        GetLoyaltyPoints();


        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        tvLoyaltyBalance.setText(getString(R.string.total_points)  + Constant.Points_Loyalty);




        chWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);
                    chLoyalty.setChecked(false);
                    LoyaltyUncheck();

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + (Constant.WALLET_BALANCE - usedBalance));
                        paymentMethod = "wallet";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + "0.0");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    tvWallet.setText("-" + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            }
        });

        chLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chLoyalty.getTag().equals("false")) {
                    chLoyalty.setChecked(true);
                    chWallet.setChecked(false);
                    walletUncheck();

                    double loyalty = Double.parseDouble(Constant.Points_Loyalty);

                    if (loyalty >= subtotal) {
                        usedPoints = subtotal;
                        tvLoyaltyBalance.setText(getString(R.string.remaining_loyalty_balance) + (loyalty - usedPoints));

                        paymentMethod = "LoyaltyPoints";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedPoints =loyalty;
                        tvWltBalance.setText(getString(R.string.remaining_loyalty_balance)  + "0");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedPoints);
                   // tvLoyalty.setText("-" + usedPoints);
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
                    chLoyalty.setTag("true");

                } else {
                    LoyaltyUncheck();
                }

            }
        });





        PromoCodeCheck();
        setPaymentMethod();

       // GetDeliveryMethod();
      //  GetDeliveryCharge();
       // ApiConfig.GetLoyaltyPoints(CheckoutActivity.this,session.getData(Session.KEY_ID));

        ApiConfig.GetPaymentConfig(CheckoutActivity.this);
    }
    private void SetDeliveryTypeStorePickupSpinnerData() {
        String[] items = new String[]{"Select Delivery Method ","Store Pickup", "In Person Delivery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        deliveryTypespinner.setAdapter(adapter);
        deliveryTypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeliveryType = deliveryTypespinner.getSelectedItem().toString();

                 if(position == 2)
                {
                    selectedDeliveryType = "delivery";
                    GetDeliveryCharge();

                }
                else if(position == 1)
                {
                    selectedDeliveryType = "pickup";
                    GetDeliveryCharge();

                }
                else
                {
                    selectedDeliveryType = "EMPTY";
                    Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble("0");
                    SetDataTotal();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void SetDeliveryTypeCourierSpinnerData() {
        String[] items = new String[]{"Select Delivery Method ","By Courier", "In Person Delivery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        deliveryTypespinner.setAdapter(adapter);
        deliveryTypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeliveryType = deliveryTypespinner.getSelectedItem().toString();

                 if(position == 2)
                {
                    selectedDeliveryType = "delivery";
                    GetDeliveryCharge();

                }
                else if(position == 1)
                {
                    selectedDeliveryType = "courier";
                    GetDeliveryCharge();

                }
                else
                {
                    selectedDeliveryType = "EMPTY";
                    Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble("0");
                    SetDataTotal();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    private void SetDeliveryTypeSpinnerData() {
        String[] items = new String[]{"Select Delivery Method ","By Courier", "In Person Delivery", "Store Pickup"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        deliveryTypespinner.setAdapter(adapter);
        deliveryTypespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeliveryType = deliveryTypespinner.getSelectedItem().toString();

                if(position == 3)
                {
                    selectedDeliveryType = "pickup";
                    GetDeliveryCharge();

                }
                else if(position == 2)
                {
                    selectedDeliveryType = "delivery";
                    GetDeliveryCharge();

                }
                else if(position == 1)
                {
                    selectedDeliveryType = "courier";
                    GetDeliveryCharge();

                }
                else
                {
                    selectedDeliveryType = "EMPTY";
                    Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble("0");
                    SetDataTotal();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void GetDeliveryMethod() {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("in_persion_delivery").equals("1")) {
                           // SetDeliveryTypeSpinnerData();
                            deliveryCardView.setVisibility(View.VISIBLE);

                            if(object.getString("Delivery_by_courier").equals("1") && object.getString("storepickup").equals("1"))
                            {
                                tvDeliveryText.setVisibility(View.GONE);
                                deliveryTypespinner.setVisibility(View.VISIBLE);
                                SetDeliveryTypeSpinnerData();
                            }
                            else  if(object.getString("Delivery_by_courier").equals("0") && object.getString("storepickup").equals("1"))
                            {
                                tvDeliveryText.setVisibility(View.GONE);
                                deliveryTypespinner.setVisibility(View.VISIBLE);
                                SetDeliveryTypeStorePickupSpinnerData();
                            }
                            else  if(object.getString("Delivery_by_courier").equals("1") && object.getString("storepickup").equals("0"))
                            {
                                tvDeliveryText.setVisibility(View.GONE);
                                deliveryTypespinner.setVisibility(View.VISIBLE);
                                SetDeliveryTypeCourierSpinnerData();
                            }
                            else
                            {
                                tvDeliveryText.setVisibility(View.VISIBLE);
                                selectedDeliveryType = "delivery";
                                GetDeliveryCharge();
                            }

                        }
                        else
                        {
                            deliveryCardView.setVisibility(View.GONE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.GETDELIVERYMETHOD_URL, params, false);
    }




    public void GetDeliveryCharge() {

        double totalAmount = databaseHelper.getTotalCartAmt(session);
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", session.getData(Session.KEY_ID));
        params.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        params.put(Constant.QUANTITY, String.valueOf(qtyList));
        params.put(Constant.ADDRESS_ID_DELIVERY, session.getData(Session.KEY_DELIVERY_ID));
        params.put("delivery_method", selectedDeliveryType);
        params.put("total_order_amount", DatabaseHelper.decimalformatData.format(totalAmount));
        params.put("get_order_delivery_charge", Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {

                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(object.getString("delivery_charge"));
                            SetDataTotal();
                           // selectedDeliveryType = "EMPTY";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, params, false);
    }

    public void walletUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        lytWallet.setVisibility(View.GONE);
        chWallet.setChecked(false);
        chWallet.setTag("false");
        SetDataTotal();
    }


    public void LoyaltyUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvLoyaltyBalance.setText(getString(R.string.total_points) + Constant.Points_Loyalty);
       // lytLoyalty.setVisibility(View.GONE);
        chLoyalty.setChecked(false);
        chLoyalty.setTag("false");
        SetDataTotal();
    }

    public void setPaymentMethod() {
        if (Constant.PAYPAL.equals("1"))
            lytPayPal.setVisibility(View.VISIBLE);
        else
            lytPayPal.setVisibility(View.GONE);

        if (Constant.PAYUMONEY.equals("1"))
            lytPayU.setVisibility(View.VISIBLE);
        else
            lytPayU.setVisibility(View.GONE);

        if (Constant.RAZORPAY.equals("1"))
            lytRazorPay.setVisibility(View.VISIBLE);
        else
            lytRazorPay.setVisibility(View.GONE);

        if (Constant.COD.equals("1"))
            lytCod.setVisibility(View.VISIBLE);
        else
            lytCod.setVisibility(View.GONE);

        if (Constant.UPI.equals("1"))
            lytUpi.setVisibility(View.VISIBLE);
        else
            lytUpi.setVisibility(View.GONE);


        chHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chHome.setChecked(true);
                chWork.setChecked(false);
                label = chHome.getTag().toString();
            }
        });
        chWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chWork.setChecked(true);
                chHome.setChecked(false);
                label = chWork.getTag().toString();
            }
        });
        rbCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCod.setChecked(true);
                rbPayU.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                rbInsta.setChecked(false);
                rbUpi.setChecked(false);
                rbUpi2.setChecked(false);
                paymentMethod = rbCod.getTag().toString();

            }
        });
        rbPayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayU.setChecked(true);
                rbCod.setChecked(false);
                rbPayPal.setChecked(false);
                rbInsta.setChecked(false);
                rbRazorPay.setChecked(false);
                rbUpi.setChecked(false);
                rbUpi2.setChecked(false);
                paymentMethod = rbPayU.getTag().toString();

            }
        });

        rbPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayPal.setChecked(true);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbInsta.setChecked(false);
                rbRazorPay.setChecked(false);
                rbUpi.setChecked(false);
                rbUpi2.setChecked(false);
                paymentMethod = rbPayPal.getTag().toString();
            }
        });

        rbRazorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(true);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbInsta.setChecked(false);
                rbUpi.setChecked(false);
                rbUpi2.setChecked(false);
                paymentMethod = rbRazorPay.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });

        rbUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(false);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbInsta.setChecked(false);
                rbUpi.setChecked(true);
                rbUpi2.setChecked(false);
                paymentMethod = rbUpi.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });

        rbUpi2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(false);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbUpi2.setChecked(true);
                rbInsta.setChecked(false);
                rbUpi.setChecked(false);
                paymentMethod = rbUpi2.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });

        rbInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(false);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbUpi2.setChecked(false);
                rbInsta.setChecked(true);
                rbUpi.setChecked(false);
                paymentMethod = rbInsta.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });
    }

    private String getTime() {
        String delegate = "HH:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }


    public void SetDataTotal() {
        total = databaseHelper.getTotalCartAmt(session);
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(total));
        subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
            deliveryCharge = Constant.SETTING_DELIVERY_CHARGE + "";
        } else {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
//            tvDeliveryCharge.setText(getResources().getString(R.string.free));
//            deliveryCharge = "0";
        }
        taxAmt = ((Constant.SETTING_TAX * total) / 100);
        if (pCode.isEmpty()) {
            subtotal = (subtotal + taxAmt);
        } else
            subtotal = (subtotal + taxAmt - pCodeDiscount);
        tvTaxPercent.setText("Tax(" + Constant.SETTING_TAX + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(taxAmt));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
    }


    public void OnBtnClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirmOrder:

//                    if(selectedDeliveryType.equals("EMPTY"))
//                    {
//                        Toast.makeText(CheckoutActivity.this, "please select Delivery Method!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }


                tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
                tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                tvConfirmOrder.setVisibility(View.GONE);
                tvPlaceOrder.setVisibility(View.VISIBLE);
                paymentLyt.setVisibility(View.VISIBLE);
                deliveryLyt.setVisibility(View.GONE);
                break;
            case R.id.tvLocation:
                if (tvLocation.getTag().equals("hide")) {
                    tvLocation.setTag("show");
                    lytCLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setTag("hide");
                    lytCLocation.setVisibility(View.GONE);
                }
                break;
            case R.id.tvPlaceOrder:
                PlaceOrderProcess();
                break;
            case R.id.imgedit:
                startActivity(new Intent(CheckoutActivity.this, AddressShowActivity.class));
                break;
            case R.id.tvUpdate:
                if (ApiConfig.isGPSEnable(CheckoutActivity.this)) {
//                    Intent intent = new Intent(CheckoutActivity.this, MapActivity.class);
//                    intent.putExtra("PAGE", "CHECKOUT");
//                    intent.putExtra("LAT", "0");
//                    intent.putExtra("LONG", "0");
//                    startActivity(intent);
                  startActivity(new Intent(CheckoutActivity.this, MapActivity.class));}
                else
                    ApiConfig.displayLocationSettingsRequest(CheckoutActivity.this);
                break;
            default:
                break;
        }
    }


    public void PlaceOrderProcess() {
        if (deliveryDay.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else

        if (paymentMethod.isEmpty()) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }


        deliveryCharge = tvDeliveryCharge.getText().toString();
        deliveryCharge = deliveryCharge.replace(Constant.SETTING_CURRENCY_SYMBOL,"");

        final Map<String, String> sendparams = new HashMap<String, String>();
        sendparams.put(Constant.PLACE_ORDER, Constant.GetVal);
        sendparams.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        sendparams.put(Constant.TAX_PERCENT, String.valueOf(Constant.SETTING_TAX));
        sendparams.put(Constant.TAX_AMOUNT, DatabaseHelper.decimalformatData.format(taxAmt));

        sendparams.put(Constant.TOTAL, DatabaseHelper.decimalformatData.format(total));
        sendparams.put(Constant.FINAL_TOTAL, DatabaseHelper.decimalformatData.format(subtotal));


        sendparams.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        sendparams.put(Constant.QUANTITY, String.valueOf(qtyList));
        sendparams.put(Constant.MOBILE, session.getData(Session.KEY_MOBILE));
        sendparams.put(Constant.DELIVERY_CHARGE, deliveryCharge);
     //  sendparams.put(Constant.DELIVERY_TIME,  "");

        sendparams.put(Constant.DELIVERY_TIME, (deliveryDay + "/" + deliveryTime));
        sendparams.put("total_items",String.valueOf(variantIdList.length()));
        String Deliverydate = "";
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(deliveryDay.equals(getString(R.string.today))){
            Deliverydate = dateFormat.format(today);
        }else if(deliveryDay.equals(getString(R.string.tomorrow))){
            Deliverydate = dateFormat.format(tomorrow);
        }
        sendparams.put("delivery_date", Deliverydate);
        sendparams.put(Constant.KEY_WALLET_USED, chWallet.getTag().toString());
        sendparams.put(Constant.KEY_WALLET_BALANCE, String.valueOf(usedBalance));

        sendparams.put("loyalty_Points_used", chLoyalty.getTag().toString());
        sendparams.put("loyalty_Points", String.valueOf(usedPoints));

        sendparams.put(Constant.PAYMENT_METHOD, paymentMethod);
        String address;
        if(label.equals("")){
            address = session.getData(Session.KEY_Address1) + "," +
                    " " + session.getData(Session.KEY_AREA) + ", " + session.getData(Session.KEY_CITY) + ", " +
                    "" + session.getData(Session.KEY_PINCODE) ;

             address = session.getData(Session.KEY_DELIVERY_ADDRESS) + ","
                    + session.getData(Session.KEY_DELIVERY_AREA)
                    + "," + session.getData(Session.KEY_DELIVERY_CITY)
                    + "" + session.getData(Session.KEY_DELIVERY_PINCODE);




        }else{
            address = session.getData(Session.KEY_Address1) + "," +
                    " " + session.getData(Session.KEY_AREA) + ", " + session.getData(Session.KEY_CITY) + ", " +
                    "" + session.getData(Session.KEY_PINCODE) + ", Deliver to " + label;


            address = session.getData(Session.KEY_DELIVERY_ADDRESS) + ","
                    + session.getData(Session.KEY_DELIVERY_AREA)
                    + "," + session.getData(Session.KEY_DELIVERY_CITY)
                    + "" + session.getData(Session.KEY_DELIVERY_PINCODE) + ", Deliver to " + label;


        }


        if (!pCode.isEmpty()) {
            sendparams.put(Constant.PROMO_CODE, pCode);
            sendparams.put(Constant.PROMO_DISCOUNT, String.valueOf(pCodeDiscount));
        }
        sendparams.put(Constant.ADDRESS, address);
        sendparams.put(Constant.LONGITUDE, session.getCoordinates(Session.KEY_LONGITUDE));
        sendparams.put(Constant.LATITUDE, session.getCoordinates(Session.KEY_LATITUDE));
        sendparams.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));


        sendparams.put("delivery_method", selectedDeliveryType);


        System.out.println("=====params " + sendparams.toString());

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        dialog.setContentView(dialogView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        // Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvCancel, tvConfirm, tvItemTotal, tvTaxPercent1, tvTaxAmt1, tvDeliveryCharge1, tvTotal1, tvPromoCode1, tvPCAmount1, tvWallet1, tvFinalTotal1;
        LinearLayout lytPromo, lytWallet;

        lytPromo = dialogView.findViewById(R.id.lytPromo);
        lytWallet = dialogView.findViewById(R.id.lytWallet);
        tvItemTotal = dialogView.findViewById(R.id.tvItemTotal);
        tvTaxPercent1 = dialogView.findViewById(R.id.tvTaxPercent);
        tvTaxAmt1 = dialogView.findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge1 = dialogView.findViewById(R.id.tvDeliveryCharge);
        tvTotal1 = dialogView.findViewById(R.id.tvTotal);
        tvPromoCode1 = dialogView.findViewById(R.id.tvPromoCode);
        tvPCAmount1 = dialogView.findViewById(R.id.tvPCAmount);
        tvWallet1 = dialogView.findViewById(R.id.tvWallet);
        tvFinalTotal1 = dialogView.findViewById(R.id.tvFinalTotal);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        tvConfirm = dialogView.findViewById(R.id.tvConfirm);
        String orderMessage = "";
        if (!pCode.isEmpty())
            lytPromo.setVisibility(View.VISIBLE);
        else
            lytPromo.setVisibility(View.GONE);

        if (chWallet.getTag().toString().equals("true"))
            lytWallet.setVisibility(View.VISIBLE);
        else
            lytWallet.setVisibility(View.GONE);

        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;

        double totalAfterTax = (total + dCharge + taxAmt);
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + total);
        tvDeliveryCharge1.setText(tvDeliveryCharge.getText().toString());
        tvTaxPercent1.setText(getString(R.string.tax) + "(" + Constant.SETTING_TAX + "%) :");
        tvTaxAmt1.setText(tvTaxAmt.getText().toString());
        tvTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount1.setText(tvPCAmount.getText().toString());
        tvWallet1.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
        tvFinalTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentMethod.equals(getResources().getString(R.string.codpaytype)) || paymentMethod.equals("wallet") || paymentMethod.equals("LoyaltyPoints")) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    System.out.println("====place order res " + response);
                                    JSONObject object = new JSONObject(response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        if (chWallet.getTag().toString().equals("true"))
                                            ApiConfig.getWalletBalance(CheckoutActivity.this, session);
                                        dialog.dismiss();

                                        selectedDeliveryType = "EMPTY";
                                        startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, true);
                    dialog.dismiss();
                } else if (paymentMethod.equals(getString(R.string.pay_u))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    paymentModelClass.OnPayClick(CheckoutActivity.this, sendparams);
                } else if (paymentMethod.equals(getString(R.string.paypal))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    StartPayPalPayment(sendparams);
                } else if (paymentMethod.equals(getString(R.string.razor_pay))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    sendparams.put("online_txn_status", "1");
                    razorParams = sendparams;
                    CreateOrderId();
                } else if (paymentMethod.equals("UPI")) {
                    dialog.dismiss();
                    upiParams = sendparams;
                    String txnId = System.currentTimeMillis() + "";
                    String stramount = upiParams.get(Constant.FINAL_TOTAL);
                    String strupivirtualid = Constant.UPI_ID.toString().trim();
                    String strname = Constant.UPI_NAME.toString().trim();
                    String strnote = Constant.UPI_NOTE.toString().trim();
                    payUsingUpi(strname, strupivirtualid,
                            strnote, stramount, txnId);
                } else if (paymentMethod.equals(getString(R.string.insta_pay))) {
                    dialog.dismiss();
                    upiParams = sendparams;
                    tvPlaceOrder.setVisibility(View.GONE);
                    String txnId = System.currentTimeMillis() + "";
                    String stramount = upiParams.get(Constant.FINAL_TOTAL);
                    String strupivirtualid = Constant.UPI_ID.toString().trim();
                    String strname = Constant.UPI_NAME.toString().trim();
                    String strnote = Constant.UPI_NOTE.toString().trim();

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", session.getData(Session.KEY_NAME));
                    params.put("email",  session.getData(Session.KEY_EMAIL));
                    params.put("mobile",  session.getData(Session.KEY_MOBILE));
                    params.put("amount", stramount);
                    params.put("user_id",  session.getData(Session.KEY_ID));
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject objectbject = new JSONObject(response);
                                    String jsonArray = objectbject.getString("order_id");
                                    mCurrentEnv = Instamojo.Environment.TEST;
                                    Instamojo.getInstance().initialize(CheckoutActivity.this, mCurrentEnv);
                                    initiateSDKPayment(jsonArray);
                                    tvPlaceOrder.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    tvPlaceOrder.setVisibility(View.VISIBLE);
                                    Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }, CheckoutActivity.this, Constant.INSTAMOJO, params, true);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.TEST;


    private void initiateSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(this, orderID, this);
    }


    void payUsingUpi(  String name,String upiId, String note, String amount,String transaction) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        String strpayment = String.valueOf("upi://pay?appid="+getPackageName().toString().trim()+"&tr=TRNID_"+transaction+"&mc=&pa="+upiId+"&pn="+getString(R.string.app_name)+"&tn="+getString(R.string.app_name)+"&am="+amount+"&cu=INR");
        strpayment = strpayment.replace("%40", "@");
        strpayment = strpayment.replace("%20", " ");
        Uri uri_new = Uri.parse(strpayment);
        upiPayIntent.setData(uri_new);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        try {
            startActivityForResult(chooser, 121);
        }catch (Exception ex) {
            Toast.makeText(CheckoutActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateOrderId() {
        showProgressDialog(getString(R.string.loading));
        Map<String, String> params = new HashMap<>();
        String[] amount = String.valueOf(subtotal * 100).split("\\.");
        params.put("amount", "" + amount[0]);
        System.out.println("====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            startPayment(object.getString("id"), object.getString("amount"));
                            hideProgressDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.Get_RazorPay_OrderId, params, false);

    }

    public void startPayment(String orderId, String payAmount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.ic_launcher);
        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Session.KEY_NAME));
            options.put(Constant.ORDER_ID, orderId);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));
            preFill.put(Constant.CONTACT, session.getData(Session.KEY_MOBILE));
            options.put("prefill", preFill);
            checkout.open(CheckoutActivity.this, options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorPayId = razorpayPaymentID;
            PlaceOrder(paymentMethod, razorPayId, true, razorParams, "Success");
        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    public void PlaceOrder_UPI(final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        showProgressDialog(getString(R.string.processing));
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                AddTransaction(object.getString(Constant.ORDER_ID), paymentType, txnid, status, getString(R.string.order_success), sendparams);
                                startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                            hideProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {
            AddTransaction("", "UPI", txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void PlaceOrder(final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        showProgressDialog(getString(R.string.processing));
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                AddTransaction(object.getString(Constant.ORDER_ID), paymentType, txnid, status, getString(R.string.order_success), sendparams);
                                startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                            hideProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {
            AddTransaction("", getString(R.string.razor_pay), txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.Add_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendparams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put(Constant.TXTN_DATE, df.format(c));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (status.equals("Failed"))
                                finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, transparams, false);
    }

    public void StartPayPalPayment(final Map<String, String> sendParams) {
        showProgressDialog(getString(R.string.processing));
        Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.LAST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.PAYER_EMAIL, sendParams.get(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, "Cart Order");
        params.put(Constant.ITEM_NUMBER, "1");
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                hideProgressDialog();
                Intent intent = new Intent(getApplicationContext(), PayPalWebActivity.class);
                intent.putExtra("url", response);
                intent.putExtra("params", (Serializable) sendParams);
                startActivity(intent);


            }
        }, CheckoutActivity.this, Constant.PAPAL_URL, params, true);
    }

    public void RefreshPromoCode(View view) {
        if (isApplied) {
            btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            btnApply.setText(getString(R.string.apply));
            edtPromoCode.setText("");
            tvPromoCode.setVisibility(View.GONE);
            tvPCAmount.setVisibility(View.GONE);
            isApplied = false;
            appliedCode = "";
            pCode = "";
            SetDataTotal();
        }
    }

    public void PromoCodeCheck() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String promoCode = edtPromoCode.getText().toString().trim();
                if (promoCode.isEmpty()) {
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(getString(R.string.enter_promo_code));
                } else if (isApplied && promoCode.equals(appliedCode)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.promo_code_already_applied), Toast.LENGTH_SHORT).show();
                } else {
                    if (isApplied && !promoCode.equals(appliedCode)) {
                        SetDataTotal();
                    }
                    tvAlert.setVisibility(View.GONE);
                    btnApply.setVisibility(View.INVISIBLE);
                    pBar.setVisibility(View.VISIBLE);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.VALIDATE_PROMO_CODE, Constant.GetVal);
                    params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                    params.put(Constant.PROMO_CODE, promoCode);
                    params.put(Constant.TOTAL, String.valueOf(total));

                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    //   System.out.println("===res " + response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        pCode = object.getString(Constant.PROMO_CODE);
                                        tvPromoCode.setText(getString(R.string.promo_code) + "(" + pCode + ")");
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                                        btnApply.setText(getString(R.string.applied));
                                        isApplied = true;
                                        appliedCode = edtPromoCode.getText().toString();
                                        tvPCAmount.setVisibility(View.VISIBLE);
                                        tvPromoCode.setVisibility(View.VISIBLE);
                                        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;
                                        subtotal = (Double.parseDouble(object.getString(Constant.DISCOUNTED_AMOUNT)) + taxAmt + dCharge);
                                        pCodeDiscount = (Double.parseDouble(object.getString(Constant.DISCOUNT)));
                                        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + pCodeDiscount);
                                        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);
                                    } else {
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                        btnApply.setText(getString(R.string.apply));
                                        tvAlert.setVisibility(View.VISIBLE);
                                        tvAlert.setText(object.getString("message"));
                                    }
                                    pBar.setVisibility(View.GONE);
                                    btnApply.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CheckoutActivity.this, Constant.PROMO_CODE_CHECK_URL, params, false);
                }
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void initiateCustomPayment(String orderID) {
        Intent intent = new Intent(getBaseContext(), CheckoutActivity.class);
        intent.putExtra(Constants.ORDER_ID, orderID);
        startActivityForResult(intent, 12321);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 121) {
            if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    Log.e("UPI", "onActivityResult: " + trxt);
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                } else {
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            } else {
                //when user simply back without payment
                Log.e("UPI", "onActivityResult: " + "Return data is null");
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        }else if((RESULT_OK == resultCode) || (resultCode == 12321)){
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);
            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                //checkPaymentStatus(transactionID, orderID);
                showToast(transactionID+ orderID);
            } else {
                showToast("Oops!! Payment was cancelled");
            }
        }
        else {
            if (data != null)
                paymentModelClass.TrasactionMethod(data, CheckoutActivity.this);
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(CheckoutActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    // else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success") || status.equals("00") ) {
                //  Status("Success",approvalRefNo);
                // PlaceOrder(a, getApplicationContext().getResources().getString(R.string.onlinepaytype), approvalRefNo, true, upiParams, status);
                PlaceOrder_UPI(paymentMethod, approvalRefNo, true, upiParams, "Success");
                //Code to handle successful transaction here.
                Toast.makeText(CheckoutActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
//                int addid = Integer.parseInt(strTransactionid);
//                addid += 1;
//                strTransactionid = String.valueOf(addid);
                // Status("Cancelled",approvalRefNo);
                // PlaceOrder(paymentMethod, approvalRefNo, false, upiParams, "Cancelled");
                Toast.makeText(CheckoutActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
//                int addid = Integer.parseInt(strTransactionid);
//                addid += 1;
//                strTransactionid = String.valueOf(addid);
                //Status("Failed",approvalRefNo);
                //  PlaceOrder(paymentMethod, approvalRefNo, false, upiParams, "Failed");
                Toast.makeText(CheckoutActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(CheckoutActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        mMap.clear();
        LatLng latLng = new LatLng(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)));
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);

//        tvName.setText(session.getData(Session.KEY_NAME));
//        tvCurrent.setText(getString(R.string.current_location) + " : " + ApiConfig.getAddress(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)), CheckoutActivity.this));
//        String address = session.getData(Session.KEY_ADDRESS) + ","
//                + session.getData(Session.KEY_AREA)
//                + ", <br>" + session.getData(Session.KEY_CITY)
//                + "- " + session.getData(Session.KEY_PINCODE);
//        tvMobile.setText(getString(R.string.mobile_) + session.getData(Session.KEY_MOBILE));
//        tvCity.setText(Html.fromHtml(address));

        tvName.setText(session.getData(Session.KEY_DELIVERY_NAME));
        tvCurrent.setText(getString(R.string.current_location) + " : " + ApiConfig.getAddress(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)), CheckoutActivity.this));
        String address = session.getData(Session.KEY_DELIVERY_ADDRESS) + ","
                + session.getData(Session.KEY_DELIVERY_AREA)
                + ", <br>" + session.getData(Session.KEY_DELIVERY_CITY)
                + "- " + session.getData(Session.KEY_DELIVERY_PINCODE);
        tvMobile.setText(getString(R.string.mobile_) + session.getData(Session.KEY_DELIVERY_MOBILE));
        tvCity.setText(Html.fromHtml(address));
        tvEmail.setText("Email :" + session.getData(Session.KEY_EMAIL));
        GetDeliveryMethod();


        if (paymentLyt.getVisibility() == View.GONE) {
//            if(!session.getData(Session.KEY_DELIVERY_AREA).equalsIgnoreCase("chennai")) {
//                GetDeliveryCharge();
//            }
        }
    }

    @Override
    public void onBackPressed() {

        if (paymentLyt.getVisibility() == View.VISIBLE) {
            walletUncheck();
            LoyaltyUncheck();
            tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
            tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
            tvConfirmOrder.setVisibility(View.VISIBLE);
            tvPlaceOrder.setVisibility(View.GONE);
            paymentLyt.setVisibility(View.GONE);
            deliveryLyt.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }
    public void  GetLoyaltyPoints () {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            Constant.Points_Loyalty = objectbject.getString("data").toString();
                            tvLoyaltyBalance.setText(getString(R.string.total_points) +Constant.Points_Loyalty);

                            if (Constant.Points_Loyalty.equals("0")) {
                                chLoyalty.setEnabled(false);
                                LoyaltyLyt.setEnabled(false);
                            }
                        }

                    } catch (JSONException e) {
                    }
                }
            }
        }, CheckoutActivity.this, Constant.LOYALTY_POINTS_URL,params, false);
    }

    public void getWalletBalance() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("=================*setting " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));

                            if (Constant.WALLET_BALANCE == 0) {
                                chWallet.setEnabled(false);
                                walletLyt.setEnabled(false);
                            }

                            DrawerActivity.tvWallet.setText(getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                            tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.USER_DATA_URL, params, false);

    }

    public void GetTimeSlots() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_TIME_SLOT, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        slotList = new ArrayList<>();
                        if (!object.getBoolean(Constant.ERROR)) {
                            dayLyt.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = object.getJSONArray(Constant.TIME_SLOTS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object1 = jsonArray.getJSONObject(i);
                                slotList.add(new Slot(object1.getString(Constant.ID), object1.getString(Constant.TITLE), object1.getString(Constant.LAST_ORDER_TIME)));

                            }
                            adapter = new SlotAdapter(slotList);
                            recyclerView.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.SETTING_URL, params, false);

    }

    public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {
        public ArrayList<Slot> categorylist;
        int selectedPosition = 0;

        public SlotAdapter(ArrayList<Slot> categorylist) {
            this.categorylist = categorylist;

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_time_slot, parent, false);
            return new ViewHolder(view);
        }

        @NonNull
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //holder.setIsRecyclable(false);
            final Slot model = categorylist.get(position);
            holder.rdBtn.setText(model.getTitle());
            holder.rdBtn.setTag(position);
            holder.rdBtn.setChecked(position == selectedPosition);
            if (deliveryDay.equals(getString(R.string.tomorrow))) {
                model.setSlotAvailable(true);
                //deliveryTime = model.getTitle();
            }
            if (model.isSlotAvailable()) {
                holder.rdBtn.setClickable(true);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            } else {
                holder.rdBtn.setChecked(false);
                holder.rdBtn.setClickable(false);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            }
            if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                rToday.setClickable(false);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            } else {
                rToday.setClickable(true);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
            System.out.println("======time slote valdation " + getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()));
            rToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                        rToday.setClickable(false);
                        rToday.setChecked(false);
                        rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                        selectedPosition = -1;
                    } else {
                        rToday.setChecked(true);
                        rTomorrow.setChecked(false);
                        deliveryDay = getString(R.string.today);
                        for (Slot s : slotList) {
                            if (getTime().compareTo(s.getLastOrderTime()) > 0) {
                                s.setSlotAvailable(false);
                            } else
                                s.setSlotAvailable(true);
                        }
                        notifyDataSetChanged();
                        selectedPosition = -1;
                    }
                }
            });

            rTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryDay = getString(R.string.tomorrow);
                    rToday.setChecked(false);
                    rTomorrow.setChecked(true);
                    notifyDataSetChanged();
                    selectedPosition = -1;

                }

            });
            holder.rdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryTime = model.getTitle();
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categorylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton rdBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                rdBtn = itemView.findViewById(R.id.rdBtn);

            }

        }
    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d(TAG, "Payment complete");
        PlaceOrder(paymentMethod, transactionID, true, upiParams, "Success");

    }

    @Override
    public void onPaymentCancelled() {
        Log.d(TAG, "Payment cancelled");
        showToast("Payment cancelled by user");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d(TAG, "Initiate payment failed");
        showToast("Initiating payment failed. Error: " + errorMessage);
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
