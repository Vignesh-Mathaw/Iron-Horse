package com.spiderindia.ironhorse.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    Session session;
    String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiConfig.GetStoreOpenSetting(SplashActivity.this);

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {

            switch (data.getPath().split("/")[1]) {
                case "itemdetail": // Handle the item detail deep link
                    Intent intent = new Intent(this, ProductDetailActivity.class);
                    intent.putExtra("id", data.getPath().split("/")[2]);
                    intent.putExtra("from", "share");
                    intent.putExtra("vpos", 0);
                    startActivity(intent);
                    finish();
                    break;

                case "refer": // Handle the item detail deep link
                    Constant.FRND_CODE = data.getPath().split("/")[2];
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", Constant.FRND_CODE);
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(SplashActivity.this, R.string.refer_code_copied, Toast.LENGTH_LONG).show();

                    Intent referIntent = new Intent(this, LoginActivity.class);
                    referIntent.putExtra("from", "register");
                    startActivity(referIntent);
                    finish();
                    break;
            }
        } else {
            setContentView(R.layout.activity_splash);
            session = new Session(SplashActivity.this);

            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (checkConnection(getApplicationContext())) {
                try {
                    /*PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    currentVersion = pInfo.versionName;
                    new GetVersionCode().execute();*/
                    displayData();
                } //catch (PackageManager.NameNotFoundException e) {
                catch (Exception e){
                    e.printStackTrace();
                }
            } else{
                // Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setMessage("Please Check Internet connection")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }

        ImageView spiderlogo = findViewById(R.id.spiderlogo);

        spiderlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.spiderindia.com/"));
                    startActivity(Getintent);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.lyt_update);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.onBackPressed();
        bottomSheetDialog.setCanceledOnTouchOutside(false);;
        TextView btn_update = bottomSheetDialog.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() + "&hl=en")
                        .timeout(1000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override
        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (onlineVersion.equals(currentVersion)) {
                    displayData();
                } else {
                    try {
                        showBottomSheetDialog();
                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                displayData();
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }

    private void displayData() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
