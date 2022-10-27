package com.spiderindia.ironhorse.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.instamojo.android.Instamojo;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.PaymentModelClass;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;

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
public class TrackCheckoutActivity extends AppCompatActivity implements PaymentResultListener, Instamojo.InstamojoPaymentCallback {

    private String TAG = TrackCheckoutActivity.class.getSimpleName();
    public Toolbar toolbar;
    LinearLayout paymentLyt, lytPayU, lytPayPal, lytRazorPay, lytUpi;
    Session session;
    PaymentModelClass paymentModelClass;
    String  paymentMethod = "";
    RadioButton rbCod, rbPayU, rbPayPal, rbRazorPay,rbUpi;
    ProgressDialog mProgressDialog;
    public String razorPayId;
    String subtotal, OrdID;

    TextView tvSubTotal, tvConfirmOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_checkout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Payment");
        toolbar.setNavigationIcon(R.drawable.ic_lefy_arrow_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        session = new Session(TrackCheckoutActivity.this);

        paymentModelClass = new PaymentModelClass(TrackCheckoutActivity.this);

        rbCod = findViewById(R.id.rbcod);
        rbPayU = findViewById(R.id.rbPayU);
        rbUpi= findViewById(R.id.rbUpi);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbRazorPay = findViewById(R.id.rbRazorPay);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvConfirmOrder = findViewById(R.id.tvConfirmOrder);

        lytPayPal = findViewById(R.id.lytPayPal);
        lytRazorPay = findViewById(R.id.lytRazorPay);
        lytUpi = findViewById(R.id.lytUpi);
        lytPayU = findViewById(R.id.lytPayU);
        paymentLyt = findViewById(R.id.paymentLyt);

        setPaymentMethod();

        subtotal = getIntent().getStringExtra("payamount").toString().trim();
        OrdID = getIntent().getStringExtra("Order_id").toString().trim();
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);

        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceOrderProcess();
            }
        });
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

       // if (Constant.RAZORPAY.equals("1"))
        lytRazorPay.setVisibility(View.VISIBLE);
       /*  else
            lytRazorPay.setVisibility(View.GONE);*/

       // if (Constant.UPI.equals("1"))
        lytUpi.setVisibility(View.VISIBLE);
       /*  else
            lytUpi.setVisibility(View.GONE);*/

        rbPayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayU.setChecked(true);
                rbCod.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                rbUpi.setChecked(false);
                paymentMethod = rbPayU.getTag().toString();

            }
        });

        rbPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayPal.setChecked(true);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbRazorPay.setChecked(false);
                rbUpi.setChecked(false);
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
                rbUpi.setChecked(false);
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
                rbUpi.setChecked(true);
                paymentMethod = rbUpi.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });
    }


    public void PlaceOrderProcess() {
        if (paymentMethod.equals(getString(R.string.razor_pay))) {
            //startPayment(OrdID, subtotal+"00");
            CreateOrderId();
        } else if (paymentMethod.equals("UPI")) {
            String txnId = System.currentTimeMillis() + "";
            String stramount = subtotal+".00";
            String strupivirtualid = Constant.UPI_ID.toString().trim();
            String strname = Constant.UPI_NAME.toString().trim();
            String strnote = Constant.UPI_NOTE.toString().trim();
            payUsingUpi(strname, strupivirtualid,
                    strnote, stramount, txnId);
        }
    }

    void payUsingUpi(  String name,String upiId, String note, String amount,String transaction) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tid", transaction)
                .appendQueryParameter("tr", "TRNID_"+transaction )
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, 121);
        } else {
            Toast.makeText(TrackCheckoutActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateOrderId() {
        showProgressDialog(getString(R.string.loading));
        Map<String, String> params = new HashMap<>();
        params.put("amount", subtotal+"00");
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
                        hideProgressDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                }
            }
        }, TrackCheckoutActivity.this, Constant.Get_RazorPay_OrderId, params, false);
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
            checkout.open(TrackCheckoutActivity.this, options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorPayId = razorpayPaymentID;
            AddTransaction(OrdID, paymentMethod, razorPayId, "Success", getString(R.string.order_success));
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

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put("update_payment", Constant.GetVal);
        transparams.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, subtotal);
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
                            startActivity(new Intent(TrackCheckoutActivity.this, OrderListActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, TrackCheckoutActivity.this, Constant.ORDERPROCESS_URL, transparams, false);
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
        }, TrackCheckoutActivity.this, Constant.PAPAL_URL, params, true);
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
        }else {
            if (data != null)
                paymentModelClass.TrasactionMethod(data, TrackCheckoutActivity.this);
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(TrackCheckoutActivity.this)) {
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

            if (status.equals("success")) {
                AddTransaction(OrdID, paymentMethod, approvalRefNo, "Success", getString(R.string.order_success));
                Toast.makeText(TrackCheckoutActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(TrackCheckoutActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(TrackCheckoutActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(TrackCheckoutActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d(TAG, "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
        Toast.makeText(getApplicationContext(),"Initiating payment failed. Error: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentCancelled() {
        Log.d(TAG, "Payment cancelled");
        Toast.makeText(getApplicationContext(),"Initiating payment failed. Error: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d(TAG, "Initiate payment failed");
        Toast.makeText(getApplicationContext(),"Initiating payment failed. Error: ",Toast.LENGTH_SHORT).show();
       // showToast("Initiating payment failed. Error: " + errorMessage);
    }





}
