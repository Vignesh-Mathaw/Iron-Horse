package com.spiderindia.ironhorse.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.AppController;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {
    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = getIntent().getStringExtra("type");
        prgLoading = findViewById(R.id.prgLoading);
        mWebView = findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
       // mWebView.setWebViewClient(new WebViewClient());


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                prgLoading.setVisibility(View.GONE);

                Uri uri = Uri.parse(url);

                if (uri.toString().startsWith("mailto:")) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, uri));
                } else if (uri.toString().startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, uri));
                }else if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
            }

        });



        try {
            if (AppController.isConnected(this)) {
                switch (type) {
                    case "privacy":
                        getSupportActionBar().setTitle(getString(R.string.privacy_policy));
                        GetContent(Constant.GET_PRIVACY, type);
                        break;
                    case "terms":
                        getSupportActionBar().setTitle(getString(R.string.terms_conditions));
                        GetContent(Constant.GET_TERMS, type);
                        break;
                    case "contact":
                        getSupportActionBar().setTitle(getString(R.string.contact));
                        GetContent(Constant.GET_CONTACT, type);
                        break;
                    case "about":
                        getSupportActionBar().setTitle(getString(R.string.about));
                        GetContent(Constant.GET_ABOUT_US, type);
                        break;
                    case "faq":
                        getSupportActionBar().setTitle(getString(R.string.faq));
                        mWebView.loadUrl(Constant.FAQ_URL);
                        prgLoading.setVisibility(View.GONE);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetContent(final String type, final String key) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(type, Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //   System.out.println("================="+type +"===================="+ response);
                if (result) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {

                            String privacyStr = obj.getString(key);
                            mWebView.setVerticalScrollBarEnabled(true);
                            mWebView.loadDataWithBaseURL("", privacyStr, "text/html", "UTF-8", "");
                            mWebView.setBackgroundColor(getResources().getColor(R.color.bg_color));

                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        prgLoading.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, WebViewActivity.this, Constant.SETTING_URL, params, false);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
