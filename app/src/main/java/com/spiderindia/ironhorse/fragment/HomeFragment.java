package com.spiderindia.ironhorse.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarteist.autoimageslider.SliderView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.CategoryAdapter;
import com.spiderindia.ironhorse.adapter.SectionAdapter;
import com.spiderindia.ironhorse.adapter.SliderAdapter_offer;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Category;
import com.spiderindia.ironhorse.model.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class HomeFragment extends Fragment {
    private Activity activity;
    private String from;
    private RecyclerView categoryRecyclerView,categoryrecycleviewtwo, sectionView, dailyview;
    private ArrayList<Slider> sliderArrayList,offersliderArrayList;
    public static ArrayList<Category> categoryArrayList, sectionList,sectionList_daily;
    private ViewPager mPager,pager_offer;
    private LinearLayout mMarkersLayout,mMarkersLayout_offer;
    private int size,size_offer;
    private Timer swipeTimer,swipeTimer_offer;
    private Handler handler,handler_offer;
    private Runnable Update,Update_offer;
    private int currentPage = 0,currentPage_offer = 0;
private TextView storeclosedLabel;
    private LinearLayout lytCategory;
    private ProgressBar progressBar;

    CardView offerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_home, container, false);
        return v;
    }

    SliderView sliderView,slider_home;
    FloatingActionButton whatsapp;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
        progressBar = view.findViewById(R.id.progressBar);
        categoryRecyclerView = view.findViewById(R.id.categoryrecycleview);
        categoryrecycleviewtwo = view.findViewById(R.id.categoryrecycleviewtwo);
        GridLayoutManager layoutManager = new GridLayoutManager(activity,2);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.setItemViewCacheSize(2);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        categoryrecycleviewtwo.setLayoutManager(horizontalLayoutManagaer);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecyclerView.getContext(), layoutManager.getOrientation());
        Drawable horizontalDivider = ContextCompat.getDrawable(activity, R.drawable.divider_line);
        dividerItemDecoration.setDrawable(horizontalDivider);
        categoryRecyclerView.addItemDecoration(dividerItemDecoration);
        sectionView = view.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(activity));
        sectionView.setNestedScrollingEnabled(false);
        sectionView.setItemViewCacheSize(2);

        dailyview = view.findViewById(R.id.dailyview);
        dailyview.setLayoutManager(new LinearLayoutManager(activity));
        dailyview.setNestedScrollingEnabled(false);
        dailyview.setItemViewCacheSize(2);

        mMarkersLayout = view.findViewById(R.id.layout_markers);
        offerView = view.findViewById(R.id.offerView);
        offerView.setVisibility(View.GONE);
        lytCategory = view.findViewById(R.id.lytCategory);
        mPager = view.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, activity);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mPager.setPageMargin(5);

        mMarkersLayout_offer = view.findViewById(R.id.layout_markers_offer);
        pager_offer = view.findViewById(R.id.pager_offer);
        pager_offer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, offersliderArrayList, mMarkersLayout_offer, activity);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        pager_offer.setPageMargin(5);

       // GetData();

        sliderView = view.findViewById(R.id.slider);
        slider_home = view.findViewById(R.id.slider_home);

        ApiConfig.Getwhatsapp(activity);

        ApiConfig.GetStoreOpenSetting(activity);

        whatsapp = view.findViewById(R.id.whatsapp);



        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=+91"+Constant.Whatsapp+"&text=Hi";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    int i = 0;

    @Override
    public void onResume() {
        if(i == 0) {
            GetData();
            i = 1;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    i = 0;
                }
            }, 2000);
        }
        super.onResume();
    }

    public void GetData() {
        Map<String, String> params = new HashMap<>();
        progressBar.setVisibility(View.VISIBLE);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                progressBar.setVisibility(View.GONE);
                if (result) {
                    sliderArrayList = new ArrayList<>();
                    try {
                        System.out.println("====res section " + response);
                        JSONObject object1 = new JSONObject(response);
                        if (!object1.getBoolean(Constant.ERROR)) {
                            JSONObject json = object1.getJSONObject(Constant.DATA);
                            JSONArray jsonArray = json.getJSONArray("slider");
                            size = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
                            }
                            SliderAdapter_offer adapter2 = new SliderAdapter_offer(getActivity(), sliderArrayList);
                            slider_home.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                            slider_home.setSliderAdapter(adapter2);
                            slider_home.setScrollTimeInSec(5);
                            slider_home.setAutoCycle(true);
                            slider_home.startAutoCycle();
                            sectionList = new ArrayList<>();
                            sectionList_daily = new ArrayList<>();

                            JSONArray jsonArray1 = json.getJSONArray("section");
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                Category section = new Category();
                                JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                section.setName(jsonObject.getString(Constant.TITLE));
                                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                                section.setProductList(ApiConfig.GetProductList(productArray));
                                String Offer_shows = jsonObject.getString("place");

                                if(Offer_shows.equals("top")){
                                    sectionList_daily.add(section);
                                }else{
                                    sectionList.add(section);
                                }
                            }
                            sectionView.setVisibility(View.VISIBLE);
                            SectionAdapter sectionAdapter = new SectionAdapter(activity, sectionList);
                            sectionView.setAdapter(sectionAdapter);

                            dailyview.setVisibility(View.VISIBLE);
                            SectionAdapter sectionAdapter_daily = new SectionAdapter(activity, sectionList_daily);
                            dailyview.setAdapter(sectionAdapter_daily);
                            JSONArray jsonArray2 = json.getJSONArray("offers");
                            offersliderArrayList = new ArrayList<>();
                            offersliderArrayList.clear();
                            size_offer = jsonArray2.length();
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                offersliderArrayList.add(new Slider("", "", "", jsonObject.getString(Constant.IMAGE)));
                                offerView.setVisibility(View.VISIBLE);
                            }
                            SliderAdapter_offer adapter = new SliderAdapter_offer(getActivity(), offersliderArrayList);
                            sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                            sliderView.setSliderAdapter(adapter);
                            sliderView.setScrollTimeInSec(5);
                            sliderView.setAutoCycle(true);
                            sliderView.startAutoCycle();
                            JSONArray jsonArray3 = json.getJSONArray("category");
                            categoryArrayList = new ArrayList<>();
                            categoryArrayList.clear();
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                JSONObject jsonObject = jsonArray3.getJSONObject(i);
                                categoryArrayList.add(new Category(jsonObject.getString(Constant.ID),
                                        jsonObject.getString(Constant.NAME),
                                        jsonObject.getString(Constant.SUBTITLE),
                                        jsonObject.getString(Constant.IMAGE),
                                        jsonObject.getString(Constant.SUB_CAT_FUN)));
                            }
                            categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category, "cate"));
                            categoryrecycleviewtwo.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_top, "cate"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.Homepage, params, false);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(true);
    }

}
