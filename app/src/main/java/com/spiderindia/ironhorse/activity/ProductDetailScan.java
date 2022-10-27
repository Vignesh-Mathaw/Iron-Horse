package com.spiderindia.ironhorse.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.spiderindia.ironhorse.BuildConfig;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.SliderAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;
import com.spiderindia.ironhorse.model.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductDetailScan extends AppCompatActivity {

    int vpos;
    String from;
    Product product;
    PriceVariation priceVariation;
    ArrayList<PriceVariation> priceVariationslist;
    Toolbar toolbar;
    ImageView imgIndicator,imgdrop;
    public TextView txtProductName,txtProductName2, txtqty, txtPrice, txtOriginalPrice, txtDiscountedPrice, txtMeasurement, imgarrow,
            txtmeasurement,txtOffer_details;
    public static ArrayList<Slider> sliderArrayList;
    public WebView webDescription;
    public TextView btncart,txtratings,txtbrandname,txtratepercentage;
    public ImageButton imgAdd, imgMinus;
    SpannableString spannableString;
    public int size;
    public ViewPager viewPager;
    public AppCompatSpinner spinner;
    TextView txtstatus;
    public ImageView imgFav;
    public LinearLayout mMarkersLayout,lyt_spinner;
    public LinearLayout lytqty, txtAdd;
    public LinearLayout lytQtymeasurement;
    public static LinearLayout lytunselect_fav;
    public static LinearLayout lytselected_fav,lytoffershows;
    RelativeLayout lytmainprice;

    FrameLayout fragment_container;


    public static FragmentManager fragmentManager;
    DatabaseHelper databaseHelper;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scan);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Details");

        vpos = getIntent().getIntExtra("vpos", 0);
        from = getIntent().getStringExtra("from");

        InitializeViews();

        if (from != null && from.equals("share")) {
            GetProductDetail(getIntent().getStringExtra("id"));
        } else {
            product = (Product) getIntent().getSerializableExtra("model");
            priceVariationslist = product.getPriceVariations();
            SetProductDetails();
        }
        String Qty = txtqty.getText().toString();
        if(Qty.equals("") || Qty.equals("0")){
            txtAdd.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        }else{
            txtAdd.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }

        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (!priceVariation.getServe_for().equals(Constant.SOLDOUT_TEXT)) {
                        if (priceVariation.getType().equals("loose")) {
                            String measurement = priceVariation.getMeasurement_unit_name();

                            if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                                double totalKg;
                                if (measurement.equals("kg") || measurement.equals("ltr"))
                                    totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                                else
                                    totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                                double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                                if (cartKg <= product.getGlobalStock()) {
                                    txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), true, ProductDetailScan.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                RegularCartAdd();
                            }

                        } else {
                            RegularCartAdd();
                        }
                        invalidateOptionsMenu();
                        String Qty = txtqty.getText().toString();
                        if (Qty.equals("") || Qty.equals("0")) {
                            txtAdd.setVisibility(View.VISIBLE);
                            lytqty.setVisibility(View.GONE);
                        } else {
                            txtAdd.setVisibility(View.GONE);
                            lytqty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getApplication().getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        lytunselect_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ApiConfig.AddRemoveFav(databaseHelper, imgFav, product.getId());
                    lytunselect_fav.setVisibility(View.GONE);
                    lytselected_fav.setVisibility(View.VISIBLE);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        lytselected_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ApiConfig.AddRemoveFav(databaseHelper, imgFav, product.getId());
                    lytunselect_fav.setVisibility(View.VISIBLE);
                    lytselected_fav.setVisibility(View.GONE);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    private void GetProductDetail(String productid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.PRODUCT_ID, productid);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        //  System.out.println("======product " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            product = ApiConfig.GetProductList(jsonArray).get(0);
                            priceVariationslist = product.getPriceVariations();
                            product.setGlobalStock(Double.parseDouble(priceVariationslist.get(0).getStock()));
                            SetProductDetails();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProductDetailScan.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
    }


    private void InitializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        databaseHelper = new DatabaseHelper(ProductDetailScan.this);
        lytqty = findViewById(R.id.lytqty);
        mMarkersLayout = findViewById(R.id.layout_markers);
        fragment_container = findViewById(R.id.fragment_container);

        viewPager = findViewById(R.id.viewPager);
        btncart = findViewById(R.id.btncart);
        txtProductName = findViewById(R.id.txtproductname);
        txtProductName2 = findViewById(R.id.txtproductname2);
        txtOriginalPrice = findViewById(R.id.txtoriginalprice);
        txtDiscountedPrice = findViewById(R.id.txtdiscountPrice);
        webDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtprice);
        txtMeasurement = findViewById(R.id.txtmeasurement);
        imgarrow = findViewById(R.id.imgarrow);
        imgFav = findViewById(R.id.imgFav);
        lytmainprice = findViewById(R.id.lytmainprice);
        txtqty = findViewById(R.id.txtqty);
        txtstatus = findViewById(R.id.txtstatus);
        imgAdd = findViewById(R.id.btnaddqty);
        imgMinus = findViewById(R.id.btnminusqty);
        spinner = findViewById(R.id.spinner);
        imgIndicator = findViewById(R.id.imgIndicator);
        imgdrop = findViewById(R.id.imgdrop);
        txtAdd = findViewById(R.id.txtAdd);
        txtmeasurement= findViewById(R.id.txtmeasurement);
        lytQtymeasurement= findViewById(R.id.lytQtymeasurement);
        txtOffer_details= findViewById(R.id.txtOffer_details);
        lyt_spinner= findViewById(R.id.lyt_spinner);
        lytselected_fav= findViewById(R.id.lytselected_fav);
        lytunselect_fav= findViewById(R.id.lytunselect_fav);
        lytoffershows = findViewById(R.id.lytoffershows);
        txtratings = findViewById(R.id.txtratings);
        txtbrandname = findViewById(R.id.txtbrandname);
        txtratepercentage = findViewById(R.id.txtratepercentage);
        imgarrow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown, 0);
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


    private void SetProductDetails() {

        try {

            lytQtymeasurement.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            lyt_spinner.setVisibility(View.VISIBLE);
            if (priceVariationslist.size() == 1) {
                spinner.setVisibility(View.GONE);
                lyt_spinner.setVisibility(View.GONE);
                lytQtymeasurement.setVisibility(View.VISIBLE);
                lytmainprice.setEnabled(false);
                imgarrow.setVisibility(View.GONE);
                priceVariation = priceVariationslist.get(0);
                SetSelectedData(priceVariation);
            }

            if (!product.getIndicator().equals("0")) {
                imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    imgIndicator.setImageResource(R.drawable.veg_icon);
                else if (product.getIndicator().equals("2"))
                    imgIndicator.setImageResource(R.drawable.non_veg_icon);
            }
            CustomAdapter customAdapter = new CustomAdapter();
            spinner.setAdapter(customAdapter);


            webDescription.setVerticalScrollBarEnabled(true);
            webDescription.loadDataWithBaseURL("", getWebString(product.getDescription()), "text/html", "UTF-8", "");
            webDescription.setBackgroundColor(getResources().getColor(R.color.white));
            String strProduct = product.getName().toString().trim();
            String[] split = strProduct.split(",");
            String secondSubString = "";
            String firstSubString = split[0];
            try {
                secondSubString = split[1];
            } catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("The index you have entered is invalid");
            }
            txtProductName.setText(firstSubString);

            if(!secondSubString.toString().trim().equals("")) {
                txtProductName2.setVisibility(View.VISIBLE);
                txtProductName2.setText(Html.fromHtml( secondSubString ));
            }

            txtbrandname.setText(product.getBrandname().toString().trim());
            txtratings.setText(product.getRating_count().toString().trim()+" ratings");
            //txtratepercentage.setText(product.getRating().toString().trim());
            Double rating = Double.valueOf(product.getRating().toString());
            txtratepercentage.setText(String.format(Locale.US, "%.1f", rating));
            spinner.setSelection(vpos);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int position) {
                    ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, ProductDetailScan.this);
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });
            imgdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinner.performClick();
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    priceVariation = product.getPriceVariations().get(i);
                    SetSelectedData(priceVariation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            SetFavOnImg(databaseHelper, imgFav, product.getId());

            String Qty = txtqty.getText().toString();
            if(Qty.equals("") || Qty.equals("0")){
                txtAdd.setVisibility(View.VISIBLE);
                lytqty.setVisibility(View.GONE);
            }else{
                txtAdd.setVisibility(View.GONE);
                lytqty.setVisibility(View.VISIBLE);
            }


            sliderArrayList = new ArrayList<>();

           // JSONArray jsonArray = new JSONArray(product.getOther_images());

           /* String str = product.getOther_images().toString();
            JSONArray jsonArray = new JSONArray(str);
            size = str.length();*/

            //ArrayList<String> Image = new ArrayList<>();

            //String Image =  product.getOther_images();

            sliderArrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(product.getOther_images());
            size = jsonArray.length();
            sliderArrayList.add(new Slider(product.getImage()));
            for (int i = 0; i < jsonArray.length(); i++) {
                sliderArrayList.add(new Slider(jsonArray.getString(i)));
            }


          //  sliderArrayList.add(new Slider(product.getImage()));

//            for (int i = 0; i < Image.size(); i++) {
//                sliderArrayList.add(new Slider(Image.get(i)));
//            }

            viewPager.setAdapter(new SliderAdapter(sliderArrayList, ProductDetailScan.this, R.layout.lyt_detail_slider, "detail"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, ProductDetailScan.this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void SetFavOnImg(DatabaseHelper databaseHelper, ImageView imgFav, String id) {
        if (databaseHelper.getFavouriteById(id)) {
            imgFav.setImageResource(R.drawable.ic_favorite);
            imgFav.setTag("y");
            lytunselect_fav.setVisibility(View.VISIBLE);
            lytselected_fav.setVisibility(View.GONE);
        } else {
            lytselected_fav.setVisibility(View.VISIBLE);
            lytunselect_fav.setVisibility(View.GONE);
            imgFav.setImageResource(R.drawable.ic_favorite_not);
            imgFav.setTag("n");
        }
    }


    private String getWebString(String data) {
        String webb = "<html>\n" +
                "<head>\n" +
                "<style type=\"text/css\">\n" +
                "@font-face {\n" +
                "    font-family: MyFont;\n" +
                "    src: url(\"file:///android_asset/fonts/poppins_regular.ttf\")\n" +
                "}\n" +
                "body {\n" +
                "    font-family: MyFont;\n" +
                "    font-size: 12px;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                data +
                "</body>\n" +
                "</html>";
        return webb;
    }


    public void OnBtnClick(View view) {
        int id = view.getId();

        String Qty ="0";
        switch (id) {
            case R.id.lytshare:
                //ShareProduct();
                new ShareProduct().execute();
                break;
          /*  case R.id.lytsave:
                ApiConfig.AddRemoveFav(databaseHelper, imgFav, product.getId());
                break;*/
            case R.id.btncart:
                OpenCart();
                break;
            case R.id.lytsimilarproducts:
                ViewMoreProduct();
                break;
            case R.id.lytmainprice:
                spinner.performClick();
                break;
            case R.id.btnminusqty:

                txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), false, ProductDetailScan.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
                invalidateOptionsMenu();
                Qty = txtqty.getText().toString();
                if(Qty.equals("") || Qty.equals("0")){
                    txtAdd.setVisibility(View.VISIBLE);
                    lytqty.setVisibility(View.GONE);
                }else{
                    txtAdd.setVisibility(View.GONE);
                    lytqty.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnaddqty:
                if (!priceVariation.getServe_for().equals(Constant.SOLDOUT_TEXT)) {
                    if (priceVariation.getType().equals("loose")) {
                        String measurement = priceVariation.getMeasurement_unit_name();

                        if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                            double totalKg;
                            if (measurement.equals("kg") || measurement.equals("ltr"))
                                totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                            else
                                totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                            double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                            if (cartKg <= product.getGlobalStock()) {
                                txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), true, ProductDetailScan.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            RegularCartAdd();
                        }


                    } else {
                        RegularCartAdd();
                    }
                    invalidateOptionsMenu();

                    Qty = txtqty.getText().toString();
                    if (Qty.equals("") || Qty.equals("0")) {
                        txtAdd.setVisibility(View.VISIBLE);
                        lytqty.setVisibility(View.GONE);
                    } else {
                        txtAdd.setVisibility(View.GONE);
                        lytqty.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void RegularCartAdd() {
        if (Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), product.getId())) < Double.parseDouble(priceVariation.getStock()))
            txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), true, ProductDetailScan.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(ProductDetailScan.this, getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

    private class ShareProduct extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Bitmap bitmap = null;
                URL url = null;
                url = new URL(product.getImage());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                Date now = new Date();
                File file = new File(getExternalCacheDir(), formatter.format(now) + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

                String message = product.getName() + "\n";
                message = message + Constant.share_url + "itemdetail/" + product.getId() + "/" + product.getSlug();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.setDataAndType(uri, getContentResolver().getType(uri));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

    }


    private void ViewMoreProduct() {
        Intent intent = new Intent(ProductDetailScan.this, ProductListActivity.class);
        intent.putExtra("from", "regular");
        intent.putExtra("id", product.getSubcategory_id());
        intent.putExtra("name", product.getName());
        startActivity(intent);
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item_product_details, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);


            PriceVariation extra = product.getPriceVariations().get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
            //  txtmeasurement.setText(extra.getMeasurement());
            // txtOffer_details.setText(extra.getDiscountpercent());
        /*    try {
                Double percentage = Double.valueOf(extra.getDiscountpercent());
                BigDecimal bd = new BigDecimal(percentage).setScale(0, RoundingMode.HALF_UP);
                double Offer = bd.doubleValue();
                txtOffer_details.setText(String.valueOf(Offer));
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }*/
            if (i == 0) {
                txttitle.setVisibility(View.GONE);
            } else {
                txttitle.setVisibility(View.GONE);
            }

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(getResources().getColor(R.color.red));
                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(getResources().getColor(R.color.black));
                price.setTextColor(getResources().getColor(R.color.black));
            }
            String Qty = txtqty.getText().toString();
            if(Qty.equals("") || Qty.equals("0")){
                txtAdd.setVisibility(View.VISIBLE);
                lytqty.setVisibility(View.GONE);
            }else{
                txtAdd.setVisibility(View.GONE);
                lytqty.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }


    public void SetSelectedData(PriceVariation priceVariation) {

        txtMeasurement.setText(  priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() );
        //  txtPrice.setText(getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getProductPrice());
        txtPrice.setText(priceVariation.getProductPrice());
        txtstatus.setText(priceVariation.getServe_for());
        txtOffer_details.setText(priceVariation.getDiscountpercent());
        //txtrupees_original_price.setVisibility(View.VISIBLE);
        if(String.valueOf(priceVariation.getDiscountpercent()).equals("0") || String.valueOf(priceVariation.getDiscountpercent()).equals("0%")){
            lytoffershows.setVisibility(View.GONE);
        }else{
            lytoffershows.setVisibility(View.VISIBLE);
        }

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            txtOriginalPrice.setText("");
            txtDiscountedPrice.setText("");
            // txtrupees_original_price.setVisibility(View.GONE);
        } else {
            // spannableString = new SpannableString(getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString = new SpannableString(priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtOriginalPrice.setText(spannableString);
            double diff = Double.parseDouble(priceVariation.getPrice()) - Double.parseDouble(priceVariation.getProductPrice());
            txtDiscountedPrice.setText(getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + priceVariation.getDiscountpercent());
        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        } else {
            txtstatus.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }

        txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), product.getId()));

        String Qty = txtqty.getText().toString();
        if(Qty.equals("") || Qty.equals("0")){
            txtAdd.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        }else{
            txtAdd.setVisibility(View.GONE);
            lytqty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart_new, ProductDetailScan.this));


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_cart:
                OpenCart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void OpenCart() {
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }


}
