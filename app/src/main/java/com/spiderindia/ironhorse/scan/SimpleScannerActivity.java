package com.spiderindia.ironhorse.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.ProductDetailScan;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;

    private static ArrayList<Product> productArrayList;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scan);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        productArrayList = new ArrayList<>();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
      //  Toast.makeText(this, "Contents = " + rawResult.getContents() +
             //   ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        Getbarcode(rawResult.getContents(), SimpleScannerActivity.this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
            }
        }, 1000);
    }

    public static void Getbarcode(final String barcode, final Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.Barcode, barcode);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            // Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            productArrayList.addAll(ApiConfig.GetProductList(jsonArray));
                            Product product = productArrayList.get(0);
                            ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
                            Intent intent = new Intent(activity, ProductDetailScan.class);
                            intent.putExtra("vpos", 0);
                            intent.putExtra("model", product);
                            activity.startActivity(intent);
                            // final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
                        }else{
                            Toast.makeText(activity,"No products available",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BARCODE_URL, params, false);

    }
}
