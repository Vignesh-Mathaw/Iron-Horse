package com.spiderindia.ironhorse.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.fragment.CategoryFragment;
import com.spiderindia.ironhorse.fragment.FavouriteFragment;
import com.spiderindia.ironhorse.fragment.HomeFragment;
import com.spiderindia.ironhorse.fragment.MoreFragment;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.AppController;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.scan.SimpleScannerActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends DrawerActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private DatabaseHelper databaseHelper;
    public static Session session;
    private Toolbar toolbar;
    public LinearLayout layoutSearch,Main_Searchlayout,Chooselayout,Deliverylayout;
    private Activity activity;
    private BottomNavigationView bottomNavigation;
    private Menu menu;
    private String from;
    private TextView storeclosedLabel;

    private TextView txtdeliveryaddress,btnCategory,toolbar_title;
    String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        databaseHelper = new DatabaseHelper(MainActivity.this);

        session = new Session(MainActivity.this);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity = MainActivity.this;
        Fresco.initialize(this);
        from = getIntent().getStringExtra("from");
        layoutSearch = findViewById(R.id.layoutSearch);
        Main_Searchlayout = findViewById(R.id.Main_Searchlayout);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btnCategory = findViewById(R.id.btnCategory);

        ((MainActivity) activity).Main_Searchlayout.setVisibility(View.VISIBLE);
        // layoutSearch.setVisibility(View.GONE);
        Chooselayout = findViewById(R.id.Chooselayout);
        Deliverylayout = findViewById(R.id.Deliverylayout);
        txtdeliveryaddress = findViewById(R.id.txtdeliveryaddress);

        bottomNavigation.setOnNavigationItemSelectedListener(onNavListener);
        bottomNavigation.setSelectedItemId(R.id.menu_home);


        ApiConfig.GetStoreOpenSetting(activity);

        storeclosedLabel = findViewById(R.id.storeclosedLabel);


        if(Constant.StoreOpenStatus.equals("0"))
        {
            storeclosedLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            storeclosedLabel.setVisibility(View.GONE);
        }

        //veeramani added code in location displaying in menu
        Deliverylayout.setVisibility(View.GONE);
        Chooselayout.setVisibility(View.GONE);

        if (session.isUserLoggedIn()) {
            Deliverylayout.setVisibility(View.VISIBLE);
            tvName.setText(session.getData(Session.KEY_NAME));
            tvMobile.setText(session.getData(Session.KEY_MOBILE));
         /*   txtdeliveryaddress.setText(session.getData(Session.KEY_ADDRESS.toString().trim() + ", "+ Session.KEY_AREA.toString().trim() +", "
                    + Session.KEY_CITY.toString().trim()+", "+Session.KEY_PINCODE.toString().trim()));*/
            String address = session.getData(Session.KEY_ADDRESS) + ","
                    + session.getData(Session.KEY_AREA)
                    + ", <br>" + session.getData(Session.KEY_CITY)
                    + "- " + session.getData(Session.KEY_PINCODE);
            txtdeliveryaddress.setText(Html.fromHtml(address));
        } else {
            //tvName.setText(getResources().getString(R.string.is_login));
            tvName.setText(getResources().getString(R.string.username));
            Chooselayout.setVisibility(View.VISIBLE);
        }

        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                if (!token.equals(session.getData(Session.KEY_FCM_ID))) {
                    UpdateToken(token, MainActivity.this);
                }
            }
        });

        if (AppController.isConnected(MainActivity.this)) {
            ApiConfig.GetSettings(MainActivity.this);
            ApiConfig.displayLocationSettingsRequest(MainActivity.this);
            if (Constant.REFER_EARN_ACTIVE.equals("0")) {
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.refer).setVisible(false);
            }
        }
        ApiConfig.getLocation(MainActivity.this);

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    bottomNavigation.setOnNavigationItemSelectedListener(onNavListener);
                    bottomNavigation.setSelectedItemId(R.id.menu_category);
                    // Toast.makeText(getApplicationContext(),"Category Button clicked",Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Chooselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(activity, LoginActivity.class));
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Deliverylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(activity, ProfileActivity.class));
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("from", Constant.FROMSEARCH));
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = pInfo.versionName;
            new GetVersionCode().execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
                } else {
                    try {
                        showBottomSheetDialog();
                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
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

    public void setCenterTitle(String title) {
        //toolbar_title.setVisibility(View.VISIBLE);

    }

    public void setLeftTitle(String title) {
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(title);

    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        bottomNavigation.setSelectedItemId(R.id.menu_home);
        if (bottomNavigation.getSelectedItemId() == R.id.menu_home) {
            super.onBackPressed();
        } else {
            bottomNavigation.setSelectedItemId(R.id.menu_home);
        }
ApiConfig.GetStoreOpenSetting(MainActivity.this);
        if(Constant.StoreOpenStatus.equals("0"))
        {
            storeclosedLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            storeclosedLabel.setVisibility(View.GONE);
        }




    }

    public void gotoFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void OnClickBtn(View view) {
        int id = view.getId();
        if (id == R.id.lythome) {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.lytcategory) {
            OnViewAllClick(view);
        } else if (id == R.id.lytfav) {
            startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
        } else if (id == R.id.layoutSearch) {

            //need to code


            startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("from", Constant.FROMSEARCH));





        } else if (id == R.id.lytcart) {
            OpenCart();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar_title.setVisibility(View.GONE);
                    ((MainActivity) activity).Main_Searchlayout.setVisibility(View.VISIBLE);
                    gotoFragment(new HomeFragment());
                    break;
                case R.id.menu_fav:
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar_title.setVisibility(View.VISIBLE);
                    ((MainActivity) activity).setLeftTitle(String.valueOf(Html.fromHtml(getResources().getString(R.string.title_fav) )));
                    ((MainActivity) activity).Main_Searchlayout.setVisibility(View.GONE);
                    gotoFragment(new FavouriteFragment());
                   // startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
                    break;
                case R.id.menu_category:
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar_title.setVisibility(View.VISIBLE);
                    ((MainActivity) activity).setLeftTitle(String.valueOf(Html.fromHtml( "All Categories")));
                    ((MainActivity) activity).Main_Searchlayout.setVisibility(View.GONE);
                    gotoFragment(new CategoryFragment());
                    break;
                case R.id.menu_cart:
                    if (session.isUserLoggedIn()) {
                        toolbar.setVisibility(View.GONE);
                        toolbar_title.setVisibility(View.GONE);
                        ((MainActivity) activity).Main_Searchlayout.setVisibility(View.GONE);
                        gotoFragment(new MoreFragment());
                    } else
                        startActivity(new Intent(activity, LoginActivity.class));
                   // OpenCart();
                    break;

                case R.id.menu_scan:
                    //startActivity(new Intent(MainActivity.this, ScanActivity.class));
                  // startActivity(new Intent(MainActivity.this, SimpleScannerActivity.class));
                    launchActivity(SimpleScannerActivity.class);
                    break;

            }
            return true;
        }
    };


    public void OnViewAllClick(View view) {
        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
    }

    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Class<?> mClss;

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    public static void UpdateToken(final String token, Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.REGISTER_DEVICE);
        params.put(Constant.TOKEN, token);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            session.setData(Session.KEY_FCM_ID, token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.RegisterUrl, params, false);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView))
            drawer.closeDrawers();
        else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment instanceof HomeFragment || getSupportFragmentManager().getBackStackEntryCount() == 1) {
                doubleBack();
            } else {
               // getSupportFragmentManager().popBackStackImmediate();
                bottomNavigation.setOnNavigationItemSelectedListener(onNavListener);
                bottomNavigation.setSelectedItemId(R.id.menu_home);
            }
        }
    }

    public void doubleBack() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 5000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        setIconTint(R.id.menu_cart);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart_new, MainActivity.this));
        //setIconTint(R.id.menu_search);
        //menu.findItem(R.id.menu_search).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setIconTint(int id) {
        Drawable drawable = menu.findItem(id).getIcon();
        if (drawable != null) {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, Color.argb(255, 255, 255, 255));
            menu.findItem(id).setIcon(drawable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cart:
                OpenCart();
                return true;
            case R.id.menu_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("from", Constant.FROMSEARCH));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void OpenCart() {
        startActivity(new Intent(MainActivity.this, CartActivity.class));
    }
}



