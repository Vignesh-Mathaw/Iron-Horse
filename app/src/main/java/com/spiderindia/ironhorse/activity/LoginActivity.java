package com.spiderindia.ironhorse.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.AppController;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.GPSTracker;
import com.spiderindia.ironhorse.helper.PinView;
import com.spiderindia.ironhorse.helper.ProgressDisplay;
import com.spiderindia.ironhorse.helper.SMSBroadcastReceiver;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.Utils;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.City;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements OnMapReadyCallback {
    ProgressDisplay progress;
    LinearLayout lytchangpsw, lytforgot, lytlogin, signUpLyt, lytotp, lytverify, lytResetPass, lytPrivacy;
    EditText edtCode, edtFCode, edtResetPass, edtResetCPass, edtnewpsw, edtRefer, edtoldpsw, edtforgotmobile,
            edtloginpassword, edtLoginMobile, edtname, edtemail, edtmobile, edtcity, edtPinCode, edtaddress, edtpsw, edtcpsw, edtMobVerify;//edtpincode
    Button btnotpverify, btnEmailVerify, btnsubmit, btnResetPass;
    String from, mobile, fromto, pincode, city, area, cityId = "0", areaId = "0";
    PinView edtotp;
    TextView txtmobileno, tvResend, tvSignUp, tvForgotPass, tvPrivacyPolicy, tvResendPass, tvTime,
            tvCurrent;
    ScrollView scrollView;
    SmsRetrieverClient client;
    Task<Void> retriever;
    SMSBroadcastReceiver smsBroadcastReceiver;
    AppCompatSpinner cityspinner, areaSpinner;
    ArrayList<City> cityArrayList, areaList;

    SupportMapFragment mapFragment;
    GPSTracker gps;
    int selectedCityId = 0;
    Session session;
    Toolbar toolbar;
    CheckBox chPrivacy;
    ////Firebase
    String phoneNumber, firebase_otp, otpFor = "",  strmap = "0";
    boolean isResendCall;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    public long leftTime;
    Timer timer;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver_my;

    TextView txtdob;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        from = getIntent().getStringExtra("from");
        fromto = getIntent().getStringExtra("fromto");
        mobile = getIntent().getStringExtra("txtmobile");
        firebase_otp = getIntent().getStringExtra("OTP");

        gps = new GPSTracker(LoginActivity.this);
        session = new Session(getApplicationContext());
        cityArrayList = new ArrayList<>();
        areaList = new ArrayList<>();
        chPrivacy = findViewById(R.id.chPrivacy);
        txtmobileno = findViewById(R.id.txtmobileno);
        cityspinner = findViewById(R.id.cityspinner);
        areaSpinner = findViewById(R.id.areaSpinner);
        edtnewpsw = findViewById(R.id.edtnewpsw);
        edtoldpsw = findViewById(R.id.edtoldpsw);
        edtCode = findViewById(R.id.edtCode);
        edtFCode = findViewById(R.id.edtFCode);
        edtResetPass = findViewById(R.id.edtResetPass);
        edtResetCPass = findViewById(R.id.edtResetCPass);
        edtforgotmobile = findViewById(R.id.edtforgotmobile);
        edtloginpassword = findViewById(R.id.edtloginpassword);
        edtLoginMobile = findViewById(R.id.edtLoginMobile);
        tvCurrent = findViewById(R.id.tvCurrent);
        lytchangpsw = findViewById(R.id.lytchangpsw);
        lytforgot = findViewById(R.id.lytforgot);
        lytlogin = findViewById(R.id.lytlogin);
        lytResetPass = findViewById(R.id.lytResetPass);
        lytPrivacy = findViewById(R.id.lytPrivacy);
        scrollView = findViewById(R.id.scrollView);
        edtotp = findViewById(R.id.edtotp);
        btnResetPass = findViewById(R.id.btnResetPass);
        btnsubmit = findViewById(R.id.btnsubmit);
        btnEmailVerify = findViewById(R.id.btnEmailVerify);
        btnotpverify = findViewById(R.id.btnotpverify);
        edtMobVerify = findViewById(R.id.edtMobVerify);
        lytverify = findViewById(R.id.lytverify);
        signUpLyt = findViewById(R.id.signUpLyt);
        lytotp = findViewById(R.id.lytotp);
        edtotp = findViewById(R.id.edtotp);
        edtname = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtemail);
        edtmobile = findViewById(R.id.edtmobile);
        edtaddress = findViewById(R.id.edtaddress);
        edtPinCode = findViewById(R.id.edtPinCode);
        edtpsw = findViewById(R.id.edtpsw);
        edtcpsw = findViewById(R.id.edtcpsw);
        edtRefer = findViewById(R.id.edtRefer);
        tvResend = findViewById(R.id.tvResend);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacy);
        tvResendPass = findViewById(R.id.tvResendPass);
        txtdob = findViewById(R.id.txtdob);
        tvTime = findViewById(R.id.tvTime);
        tvSignUp.setText(underlineSpannable(getString(R.string.sign_up_)));
        tvForgotPass.setText(underlineSpannable(getString(R.string.forgottext)));
        tvResendPass.setText(underlineSpannable(getString(R.string.resend_cap)));
        edtCode.setText("91");

        final Calendar c = Calendar.getInstance();
        txtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LoginActivity.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT, pickerListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtdob.setText(session.getData(Session.KEY_DOB));

        tvResend.setVisibility(View.GONE);

        starTimer();


        //  edtLoginMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);

        edtoldpsw.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtnewpsw.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtloginpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtpsw.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtcpsw.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtResetPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        edtResetCPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0);
        Utils.setHideShowPassword(edtpsw);
        Utils.setHideShowPassword(edtcpsw);
        Utils.setHideShowPassword(edtloginpassword);
        Utils.setHideShowPassword(edtoldpsw);
        Utils.setHideShowPassword(edtnewpsw);
        Utils.setHideShowPassword(edtResetPass);
        Utils.setHideShowPassword(edtResetCPass);
        progress = new ProgressDisplay(this);
        // Constant.country_code ="91"; // your country code
        //edtCode.setText(Constant.country_code);
        // edtFCode.setText(Constant.country_code);
        if (from != null) {
            switch (from) {
                case "forgot":
                    lytforgot.setVisibility(View.VISIBLE);
                    break;
                case "changepsw":
                    lytchangpsw.setVisibility(View.VISIBLE);
                    break;
                case "reset_pass":
                    lytResetPass.setVisibility(View.VISIBLE);
                    break;
                case "register":
                    lytverify.setVisibility(View.VISIBLE);
                    break;
                case "otp_verify":
                case "otp_forgot":
                    lytotp.setVisibility(View.VISIBLE);
                    txtmobileno.setText(getResources().getString(R.string.please_type_verification_code_sent_to) + "  " + Constant.country_code + " " + mobile);
                    break;
                default:
                    signUpLyt.setVisibility(View.VISIBLE);
                    edtmobile.setText(mobile);
                    edtRefer.setText(Constant.FRND_CODE);
                    SetCitySpinnerData();
                    break;
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            lytlogin.setVisibility(View.VISIBLE);

        }

        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = cityspinner.getSelectedItem().toString();
                cityId = cityArrayList.get(position).getCity_id();
                SetAreaSpinnerData(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaSpinner.getSelectedItem().toString();
                areaId = areaList.get(position).getCity_id();
                // edtPinCode.setText(areaList.get(position).getPincode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        client = SmsRetriever.getClient(this);
        retriever = client.startSmsRetriever();
        smsBroadcastReceiver = new SMSBroadcastReceiver();

        retriever.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                smsBroadcastReceiver.injectOTPListener(new SMSBroadcastReceiver.OTPListener() {
                    @Override
                    public void onOTPReceived(@NotNull String otp) {
                        System.out.println("====otp");
                    }

                    @Override
                    public void onOTPTimeOut() {

                    }
                });

                registerReceiver(smsBroadcastReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));

            }
        });

        retriever.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ApiConfig.displayLocationSettingsRequest(LoginActivity.this);
        ApiConfig.getLocation(LoginActivity.this);
        ApiConfig.GetStoreOpenSetting(LoginActivity.this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        StartFirebaseLogin();
        PrivacyPolicy();

        startSmsUserConsent();
    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        ((SmsRetrieverClient) client).startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                //otp_one.setText(String.format("%s - %s", getString(R.string.received_message), message));
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            try {
                String otp1 = matcher.group(0).toString();
                edtotp.setText(otp1);
                OTP_Varification();
               /* otp_one.setText(String.valueOf(otp1.charAt(0)));
                otp_two.setText(String.valueOf(otp1.charAt(1)));
                otp_three.setText(String.valueOf(otp1.charAt(2)));
                otp_four.setText(String.valueOf(otp1.charAt(3)));

                String otpPin = otp_one.getText().toString().trim() + otp_two.getText().toString().trim() + otp_three.getText().toString().trim() +
                        otp_four.getText().toString().trim();
                otpPinStr = sharedpreferences.getString("OTP", "");
                if (otpPin.equals(otpPinStr)) {
                    Intent Change_password_Activity = new Intent(getApplicationContext(), Change_password_Activity.class);
                    startActivity(Change_password_Activity);
                }else{
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Mobile_otp_Activity.this);
                    builder.setMessage("Invalid OTP")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    otp_four.requestFocus();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }*/
                // Toast.makeText(getApplicationContext(),otp1.toString(),Toast.LENGTH_LONG).show();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }

        }
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver_my = new SmsBroadcastReceiver();
        smsBroadcastReceiver_my.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver_my, intentFilter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver_my);
    }

    public void generateOTP() {
        final String mobile = edtMobVerify.getText().toString().trim();
        final String code = edtCode.getText().toString().trim();
        if (ApiConfig.CheckValidattion(code, false, false)) {
            //edtCode.setError(getString(R.string.enter_country_code));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_country_code))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtCode.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(mobile, false, false)) {
            //edtMobVerify.setError(getString(R.string.enter_mobile_no));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtMobVerify.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(mobile, false, true)) {
            //edtMobVerify.setError(getString(R.string.enter_valid_mobile_no));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_valid_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtMobVerify.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (AppController.isConnected(LoginActivity.this)) {
            Constant.country_code = code;
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.VERIFY_USER);
            params.put(Constant.MOBILE, mobile);
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            System.out.println("=================*verify  " + response);
                            JSONObject object = new JSONObject(response);
                            otpFor = "otp_verify";
                            phoneNumber = "+" + (Constant.country_code + mobile);
                            if (!object.getBoolean(Constant.ERROR)) {
                                sentRequest(phoneNumber);
                            } else {
                                setSnackBar(getString(R.string.verify_alert_1) + getString(R.string.app_name) + getString(R.string.verify_alert_2), getString(R.string.btn_ok), from);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, LoginActivity.this, Constant.RegisterUrl, params, true);
        }
    }


    public void sentRequest(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                LoginActivity.this,        // Activity (for callback binding)
                mCallback);
    }

    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                System.out.println("====verification complete call  " + phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                setSnackBar(e.getLocalizedMessage(), getString(R.string.btn_ok), "failed");
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Constant.verificationCode = s;
                String mobileno = "";
                starTimer();
                if (!isResendCall) {
                    if (otpFor.equals("otp_forgot"))
                        mobileno = edtforgotmobile.getText().toString();
                    else
                        mobileno = edtMobVerify.getText().toString();


                    startActivity(new Intent(LoginActivity.this, LoginActivity.class)
                            .putExtra("from", otpFor)
                            .putExtra("txtmobile", mobileno)
                            .putExtra("OTP", s));

                }

            }
        };
    }

    public void ChangePassword() {
        final Session sessionpsw = new Session(LoginActivity.this);
        String oldpsw = edtoldpsw.getText().toString();
        String password = edtnewpsw.getText().toString();

        if (ApiConfig.CheckValidattion(oldpsw, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_old_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtoldpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(password, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_new_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtnewpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } /*else if(password.length() < 8 || password.length() > 16) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if (!isAcceptablePassword(password)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16 having at least 1 " +
                    "uppercase alphabet , 1 lowercase alphabet , 1 number and 1 special charecter")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }*/else if (oldpsw.equals(password)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Old Password and New Password should not be same")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtoldpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }  else if (!oldpsw.equals(sessionpsw.getData(Session.KEY_Password))) {
            //edtoldpsw.setError(getString(R.string.no_match_old_pass));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.no_match_old_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtoldpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (AppController.isConnected(LoginActivity.this)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.CHANGE_PASSWORD);
            params.put(Constant.PASSWORD, password);
            params.put(Constant.ID, sessionpsw.getData(Session.KEY_ID));

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            // Setting Dialog Message
            alertDialog.setTitle(getString(R.string.change_pass));
            alertDialog.setMessage(getString(R.string.reset_alert_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();

            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            //  System.out.println("=================*changepsw " + response);
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        sessionpsw.logoutUser_reset(LoginActivity.this);
                                      /*  finish();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));*/
                                    }
                                    Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, LoginActivity.this, Constant.RegisterUrl, params, true);

                }
            });
            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void ResetPassword() {

        String reset_psw = edtResetPass.getText().toString();
        String reset_c_psw = edtResetCPass.getText().toString();

        if (ApiConfig.CheckValidattion(reset_psw, false, false)) {
            // edtResetPass.setError(getString(R.string.enter_new_pass));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_new_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtResetPass.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if(reset_psw.length() < 8 || reset_psw.length() > 16) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if (!isAcceptablePassword(reset_psw)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16 having at least 1 " +
                    "uppercase alphabet , 1 lowercase alphabet , 1 number and 1 special charecter")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if (ApiConfig.CheckValidattion(reset_c_psw, false, false)) {
            //edtResetCPass.setError(getString(R.string.enter_confirm_pass));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_confirm_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtResetCPass.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (!reset_psw.equals(reset_c_psw)) {
            // edtResetPass.setError(getString(R.string.pass_not_match));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.pass_not_match))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtResetPass.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }  else if (AppController.isConnected(LoginActivity.this)) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.CHANGE_PASSWORD);
            params.put(Constant.PASSWORD, reset_c_psw);
            //params.put(Constant.ID, session.getData(Session.KEY_ID));
            params.put(Constant.ID, Constant.U_ID);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            // Setting Dialog Message
            alertDialog.setTitle(getString(R.string.reset_pass));
            alertDialog.setMessage(getString(R.string.reset_alert_msg));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {

//                            if (result) {
//                                try {
//                                    JSONObject object = new JSONObject(response);
//                                    if (!object.getBoolean(Constant.ERROR)) {
//                                        setSnackBar(getString(R.string.msg_reset_pass_success), getString(R.string.btn_ok), from);
//                                    }
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            Toast.makeText(LoginActivity.this, R.string.msg_reset_pass_success, Toast.LENGTH_SHORT).show();
                        }
                    }, LoginActivity.this, Constant.RegisterUrl, params, true);

                }
            });
            alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog1.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    public void RecoverPassword() {
        final String mobile = edtforgotmobile.getText().toString().trim();
        String code = edtFCode.getText().toString().trim();
        if (ApiConfig.CheckValidattion(code, false, false)) {
            // edtFCode.setError(getString(R.string.enter_country_code));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_country_code))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtFCode.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(mobile, false, false)) {
            //edtforgotmobile.setError(getString(R.string.enter_mobile_no));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtforgotmobile.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (mobile.length() != 0 && ApiConfig.CheckValidattion(mobile, false, true)) {
            //  edtforgotmobile.setError(getString(R.string.enter_valid_mobile_no));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_valid_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtforgotmobile.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }  else {
            Constant.country_code = code;
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.TYPE, Constant.VERIFY_USER);
            params.put(Constant.MOBILE, mobile);
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            //System.out.println("=================*verify  " + response);
                            JSONObject object = new JSONObject(response);
                            otpFor = "otp_forgot";
                            phoneNumber = ("+" + Constant.country_code + mobile);
                            if (object.getBoolean(Constant.ERROR)) {
                                Constant.U_ID = object.getString(Constant.ID);
                                sentRequest(phoneNumber);
                            } else {
                                setSnackBar(getString(R.string.alert_register_num1) + getString(R.string.app_name) + getString(R.string.alert_register_num2), getString(R.string.btn_ok), from);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, LoginActivity.this, Constant.RegisterUrl, params, true);
        }
    }

    public void UserLogin() {
        String email = edtLoginMobile.getText().toString();
        final String password = edtloginpassword.getText().toString();

        if (ApiConfig.CheckValidattion(email, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtLoginMobile.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(email, false, true)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_valid_mobile_no))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtLoginMobile.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(password, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtloginpassword.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (AppController.isConnected(LoginActivity.this)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(Constant.MOBILE, email);
            params.put(Constant.PASSWORD, password);
            params.put(Constant.FCM_ID, "" + AppController.getInstance().getDeviceToken());
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    System.out.println("============login res " + response);
                    if (result) {
                        try {
                            JSONObject objectbject = new JSONObject(response);
                            if (!objectbject.getBoolean(Constant.ERROR)) {
                                StartMainActivity(objectbject, password, "","");
                            }
                            Toast.makeText(LoginActivity.this, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, LoginActivity.this, Constant.LoginUrl, params, true);
        }
    }


    public void setSnackBar(String message, String action, final String type) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("reset_pass") || type.equals("forgot") || type.equals("register")) {
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void OTP_Varification() {
        String otptext = edtotp.getText().toString().trim();

        if (ApiConfig.CheckValidattion(otptext, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_otp))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtotp.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(firebase_otp, otptext);
            signInWithPhoneAuthCredential(credential, otptext);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String otptext) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            if (from.equalsIgnoreCase("otp_verify")) {
                                startActivity(new Intent(LoginActivity.this, LoginActivity.class).putExtra("from", "info").putExtra("txtmobile", mobile).putExtra("OTP", otptext));
                            } else {
                                startActivity(new Intent(LoginActivity.this, LoginActivity.class).putExtra("from", "reset_pass"));
                            }
                            edtotp.setError(null);
                        } else {

                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            edtotp.setError(message);

                        }
                    }
                });
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String monthString = String.valueOf(selectedMonth + 1);
            String dateString = String.valueOf(selectedDay);
            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }
            if (dateString.length() == 1) {
                dateString = "0" + dateString;
            }
            txtdob.setText(dateString + "-" + monthString + "-" + selectedYear);
        }
    };


    public void UserSignUpSubmit(String latitude, String longitude) {

        String name = edtname.getText().toString().trim();
        String email = "" + edtemail.getText().toString().trim();
        String mobile = edtmobile.getText().toString().trim();
        String address = edtaddress.getText().toString().trim();
        String pincode = edtPinCode.getText().toString().trim();
        final String password = edtpsw.getText().toString().trim();
        String cpassword = edtcpsw.getText().toString().trim();
        final String dob = txtdob.getText().toString();

        if (ApiConfig.CheckValidattion(name, false, false)) {
            //edtname.setError(getString(R.string.enter_name));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_name))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtname.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
            //scrollView.scrollTo(0, edtname.getBottom());
        }  else if (!isAcceptabletext(name)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Enter valid Name")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtname.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }/*else if (ApiConfig.CheckValidattion(email, false, false)) {
            //edtemail.setError(getString(R.string.enter_email));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_email))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtemail.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }*/
        else if (!email.equals("") && ApiConfig.CheckValidattion(email, true, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_valid_email))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtemail.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (cityId.equals("0")) {
            //Toast.makeText(LoginActivity.this, getResources().getString(R.string.selectcity), Toast.LENGTH_LONG).show();
            //scrollView.scrollTo(0, cityspinner.getBottom());
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.selectcity))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            //edtemail.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (areaId.equals("0")) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.selectarea))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            //edtemail.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }   else if (ApiConfig.CheckValidattion(pincode, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_pincode))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtPinCode.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (CheckValidattion(pincode, false, true) )
//        else if (CheckValidattion(pincode, false, true) ||
//                pincode.equals("000000"))

        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Enter a Valid Pincode")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (ApiConfig.CheckValidattion(address, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_address))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtaddress.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (!isAcceptableaddress(edtaddress.getText().toString().trim())) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Enter valid Address")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtaddress.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if (ApiConfig.CheckValidattion(password, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
      /*  else if(password.length() < 8 || password.length() > 16) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }else if (!isAcceptablePassword(password)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Password length should between 8 to 16 having at least 1 " +
                    "uppercase alphabet , 1 lowercase alphabet , 1 number and 1 special charecter")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }*/
        else if (ApiConfig.CheckValidattion(cpassword, false, false)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.enter_confirm_pass))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            edtcpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (!password.equals(cpassword)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.pass_not_match))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            edtcpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (latitude.equals("0") || longitude.equals("0")) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.alert_select_location))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (!chPrivacy.isChecked()) {
            // Toast.makeText(LoginActivity.this, getString(R.string.alert_privacy_msg), Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(getString(R.string.alert_privacy_msg))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            // edtcpsw.requestFocus();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        } else if (AppController.isConnected(LoginActivity.this)) {
            if (strmap.equals("0")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Please Choose the delivery location on the map?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                strmap = "1";
//                                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
//                                intent.putExtra("PAGE", "LOGIN");
//                                intent.putExtra("LAT","0");
//                                intent.putExtra("LONG", "0");
//                                startActivity(intent);

                              startActivity(new Intent(LoginActivity.this, MapActivity.class));
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            } else {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.TYPE, Constant.REGISTER);
                params.put(Constant.NAME, name);
                params.put(Constant.EMAIL, email);
                params.put(Constant.MOBILE, mobile);
                params.put(Constant.PASSWORD, password);
                params.put(Constant.PINCODE, pincode);


                params.put(Constant.CITY_ID, cityId);
                params.put(Constant.AREA_ID, areaId);
                params.put(Constant.STREET, address);
                params.put(Constant.LONGITUDE, longitude);
                params.put(Constant.LATITUDE, latitude);
                params.put(Constant.DOB, dob);
                params.put(Constant.COUNTRY_CODE, Constant.country_code);
                params.put(Constant.REFERRAL_CODE, Constant.randomAlphaNumeric(8));
                params.put(Constant.FRIEND_CODE, edtRefer.getText().toString().trim());
                params.put(Constant.FCM_ID, "" + AppController.getInstance().getDeviceToken());
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    StartMainActivity(objectbject, password, area, city);
                                }
                                Toast.makeText(LoginActivity.this, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, LoginActivity.this, Constant.RegisterUrl, params, true);
            }
        }
    }

    public static boolean CheckValidattion(String item, boolean isemailvalidation, boolean ismobvalidation) {
        if (item.length() == 0)
            return true;
        else if (isemailvalidation && (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()))
            return true;
        else return ismobvalidation && (item.length() < 6 || item.length() > 6);
    }

    public void OnBtnClick(View view) {
        int id = view.getId();

        if (id == R.id.tvSignUp) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class).putExtra("from", "register").putExtra("from", "register"));
        } else if (id == R.id.tvForgotPass) {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class).putExtra("from", "forgot"));
        } else if (id == R.id.btnchangepsw) {

            ChangePassword();

        } else if (id == R.id.btnResetPass) {
            hideKeyboard(view);
            ResetPassword();

        } else if (id == R.id.btnrecover) {
            hideKeyboard(view);
            RecoverPassword();

        } else if (id == R.id.tvResendPass) {
            hideKeyboard(view);
            RecoverPassword();

        } else if (id == R.id.btnlogin) {
            hideKeyboard(view);
            UserLogin();

        } else if (id == R.id.btnEmailVerify) {
            //    MobileVerification();
            hideKeyboard(view);
            generateOTP();

        } else if (id == R.id.tvResend) {
            isResendCall = true;
            // generateOTP();
            starTimer();
            tvResend.setVisibility(View.GONE);
            phoneNumber = "+91"+mobile;
            sentRequest(phoneNumber);

        } else if (id == R.id.btnotpverify) {
            hideKeyboard(view);
            OTP_Varification();

        } else if (id == R.id.btnsubmit) {
            double saveLatitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LATITUDE));
            double saveLongitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LONGITUDE));

            if (saveLatitude == 0 || saveLongitude == 0) {
                UserSignUpSubmit(String.valueOf(gps.latitude), String.valueOf(gps.longitude));
            } else {
                UserSignUpSubmit(new Session(getApplicationContext()).getCoordinates(Session.KEY_LATITUDE), new Session(getApplicationContext()).getCoordinates(Session.KEY_LONGITUDE));
            }


        } else if (id == R.id.tvUpdate) {

            if (ApiConfig.isGPSEnable(LoginActivity.this)) {
                startActivity(new Intent(LoginActivity.this, MapActivity.class));
                strmap = "1";
            }
            else
                ApiConfig.displayLocationSettingsRequest(LoginActivity.this);
        }

    }

    public void StartMainActivity(JSONObject objectbject, String password, String area, String city) {
        try {

            if(area.equals("")) {
                new Session(LoginActivity.this).createUserLoginSession(AppController.getInstance().getDeviceToken(),
                        objectbject.getString(Constant.USER_ID),
                        objectbject.getString(Constant.NAME), objectbject.getString(Constant.EMAIL),
                        objectbject.getString(Constant.MOBILE), objectbject.getString(Constant.DOB),
                        objectbject.getString(Constant.CITY_NAME), objectbject.getString(Constant.AREA_NAME),
                        objectbject.getString(Constant.CITY_ID), objectbject.getString(Constant.AREA_ID),
                        objectbject.getString(Constant.STREET), objectbject.getString(Constant.PINCODE),
                        objectbject.getString(Constant.STATUS), objectbject.getString(Constant.CREATEDATE),
                        objectbject.getString(Constant.APIKEY), password, objectbject.getString(Constant.REFERRAL_CODE),
                        objectbject.getString(Constant.LATITUDE), objectbject.getString(Constant.LONGITUDE));
            }else{
                new Session(LoginActivity.this).createUserLoginSession(AppController.getInstance().getDeviceToken(),
                        objectbject.getString(Constant.USER_ID),
                        objectbject.getString(Constant.NAME), objectbject.getString(Constant.EMAIL),
                        objectbject.getString(Constant.MOBILE), objectbject.getString(Constant.DOB),
                        city, area,
                        objectbject.getString(Constant.CITY_ID), objectbject.getString(Constant.AREA_ID),
                        objectbject.getString(Constant.STREET), objectbject.getString(Constant.PINCODE),
                        objectbject.getString(Constant.STATUS), objectbject.getString(Constant.CREATEDATE),
                        objectbject.getString(Constant.APIKEY), password, objectbject.getString(Constant.REFERRAL_CODE),
                        objectbject.getString(Constant.LATITUDE), objectbject.getString(Constant.LONGITUDE));
            }
            session.setData(Session.KEY_Address1,objectbject.getString(Constant.STREET));
            if (fromto != null && fromto.equals("checkout"))
                startActivity(new Intent(LoginActivity.this, AddressShowActivity.class));
            else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            //finishAffinity();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        mMap.clear();
        LatLng latLng;

        double saveLatitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LATITUDE));
        double saveLongitude = Double.parseDouble(new Session(getApplicationContext()).getCoordinates(Session.KEY_LONGITUDE));

        if (saveLatitude == 0 || saveLongitude == 0) {
            latLng = new LatLng(gps.latitude, gps.longitude);
        } else {
            latLng = new LatLng(saveLatitude, saveLongitude);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Timer extends CountDownTimer {

        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;

            long totalSecs = (long) (millisUntilFinished / 1000.0);
            long minutes = (totalSecs / 120);
            long seconds = totalSecs % 120;
            tvTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFinish() {
            stopTimer();
            tvResend.setVisibility(View.VISIBLE);
            tvTime.setText("");
        }
    }

    public void starTimer() {
        timer = new Timer(120 * 1000, 1000);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    public SpannableString underlineSpannable(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        return spannableString;
    }

    public void PrivacyPolicy() {
        tvPrivacyPolicy.setClickable(true);
        tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        String message = getString(R.string.msg_privacy_terms);
        String s2 = getString(R.string.terms_conditions);
        String s1 = getString(R.string.privacy_policy);
        final Spannable wordtoSpan = new SpannableString(message);

        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent privacy = new Intent(LoginActivity.this, WebViewActivity.class);
                privacy.putExtra("type", "privacy");
                startActivity(privacy);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent terms = new Intent(LoginActivity.this, WebViewActivity.class);
                terms.putExtra("type", "terms");
                startActivity(terms);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacyPolicy.setText(wordtoSpan);
    }

    private void SetCitySpinnerData() {
        Map<String, String> params = new HashMap<String, String>();
        cityArrayList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        // System.out.println("====res city " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            cityArrayList.add(0, new City("0", getString(R.string.select_city)));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (session.getData(Session.KEY_CITY_ID).equals(jsonObject.getString(Constant.ID))) {
                                    selectedCityId = i;
                                }
                                cityArrayList.add(new City(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME)));
                            }
                            cityspinner.setAdapter(new ArrayAdapter<City>(LoginActivity.this, R.layout.spinner_item, cityArrayList));
                            cityspinner.setSelection(selectedCityId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, LoginActivity.this, Constant.CITY_URL, params, false);
    }

    private void SetAreaSpinnerData(String cityId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.CITY_ID, cityId);
        areaList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        // System.out.println("====res area " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            areaList.add(0, new City("0", getString(R.string.select_area)));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                areaList.add(new City(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME)));
                            }
                            areaSpinner.setAdapter(new ArrayAdapter<City>(LoginActivity.this, R.layout.spinner_item, areaList));
                        }





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, LoginActivity.this, Constant.GET_AREA_BY_CITY, params, false);
    }




    double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        super.onResume();
        latitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE));
        longitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE));
        tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, LoginActivity.this));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(LoginActivity.this);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static final String SPECIAL_CHARACTERS = "!@#$%^&*()~`-=_+[]{}|:\";',./<>?";
    public static boolean isAcceptabletext(String password) {
        if (TextUtils.isEmpty(password)) {
            System.out.println("empty string.");
            return false;
        }
        char[] aC = password.toCharArray();
        boolean status=false;

        String str = "0";


        for(char c : aC) {
            if (Character.isUpperCase(c)) {
                str = "1";
            } else if (Character.isLowerCase(c)) {
                str = "1";
            } else if (Character.isDigit(c)) {
                return false;
            } else
            if (SPECIAL_CHARACTERS.indexOf(String.valueOf(c)) >= 0) {
                return false;
            }
        }
        if(str == "1"){
            status = true;
        }else { status = false; }
        return status;
    }

    public static final String SPECIAL_CHARACTERS_Address = "!@#%^&*()~`=_+[]{}|:\";'<>?";
    public static boolean isAcceptableaddress(String password) {
        if (TextUtils.isEmpty(password)) {
            System.out.println("empty string.");
            return false;
        }
        char[] aC = password.toCharArray();
        boolean status=false;

        String str = "0";


        for(char c : aC) {
            if (Character.isUpperCase(c)) {
                str = "1";
            } else if (Character.isLowerCase(c)) {
                str = "1";
            } else if (Character.isDigit(c)) {
                System.out.println(c + " is digit.");
            } else
            if (SPECIAL_CHARACTERS_Address.indexOf(String.valueOf(c)) >= 0) {
                return false;
            }
        }
        if(str == "1"){
            status = true;
        }else { status = false; }
        return status;
    }


    public static boolean isAcceptablePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            System.out.println("empty string.");
            return false;
        }
        password = password.trim();
        int len = password.length();
        if(len < 6 || len > 100) {
            System.out.println("wrong size, it must have at least 8 characters and less than 20.");
            return false;
        }

        char[] aC = password.toCharArray();
        boolean status=false, lower=false,upper=false,number=false,spical=false;
        for(char c : aC) {
            if (Character.isUpperCase(c)) {
                System.out.println(c + " is uppercase.");
                upper = true;
            } else if (Character.isLowerCase(c)) {
                System.out.println(c + " is lowercase.");
                lower = true;
            } else if (Character.isDigit(c)) {
                number = true;
            } else if (SPECIAL_CHARACTERS.indexOf(String.valueOf(c)) >= 0) {
                spical = true;
            }
        }
        if(lower == true && upper == true && number == true && spical == true){
            status = true;
        }else{
            status = false;
        }
        return status;
    }
}




