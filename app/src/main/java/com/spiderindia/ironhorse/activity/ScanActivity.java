package com.spiderindia.ironhorse.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.spiderindia.ironhorse.R;
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

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    private static ArrayList<Product> productArrayList;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        productArrayList = new ArrayList<>();// Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
       // Toast.makeText(getApplicationContext(),rawResult.getText().toString().trim(),Toast.LENGTH_SHORT).show();
        Getbarcode(rawResult.getText().toString().trim(), ScanActivity.this);
        mScannerView.resumeCameraPreview(this);
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
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
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
