package com.spiderindia.ironhorse.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.CatProductListActivity;
import com.spiderindia.ironhorse.activity.ProductDetailActivity;
import com.spiderindia.ironhorse.model.Slider;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter_offer extends SliderViewAdapter<SliderAdapter_offer.SliderAdapterViewHolder> {

    // list for storing urls of images.
    private final List<Slider> mSliderItems;

    Context context;

    // Constructor
    public SliderAdapter_offer(Context context, ArrayList<Slider> sliderDataArrayList) {
        this.mSliderItems = sliderDataArrayList;
        this.context = context;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_slider, null);
        return new SliderAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final Slider sliderItem = mSliderItems.get(position);
        Uri uri = Uri.parse(sliderItem.getImage());
        viewHolder.imgslider.getHierarchy().setPlaceholderImage(R.drawable.ic_banner_loading);
        viewHolder.imgslider.getHierarchy().setFailureImage(R.drawable.ic_banner_loading);
        viewHolder.imgslider.setImageURI(uri);


        viewHolder.lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (sliderItem.getType().equals("category")) {
                        Intent intent = new Intent(context, CatProductListActivity.class);
                        intent.putExtra("id", sliderItem.getType_id());
                        intent.putExtra("name", sliderItem.getType());
                        intent.putExtra("from", "item");
                        context.startActivity(intent);
                    } else if (sliderItem.getType().equals("product")) {
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra("id", sliderItem.getType_id());
                        intent.putExtra("vpos", 0);
                        intent.putExtra("from", "share");
                        context.startActivity(intent);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;
        SimpleDraweeView imgslider;
        CardView lytmain;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
           // imageViewBackground = itemView.findViewById(R.id.imgslider);
            imgslider = itemView.findViewById(R.id.imgslider);
            lytmain = itemView.findViewById(R.id.lytmain);
            this.itemView = itemView;
        }
    }
}