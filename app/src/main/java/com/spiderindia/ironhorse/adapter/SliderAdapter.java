package com.spiderindia.ironhorse.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.FullScreenViewActivity;
import com.spiderindia.ironhorse.activity.ProductDetailActivity;
import com.spiderindia.ironhorse.activity.SubCategoryActivity;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.model.Slider;

import java.util.ArrayList;


    public class SliderAdapter extends PagerAdapter {

        ArrayList<Slider> dataList;
        Activity activity;
        int layout;
        String from;

        public SliderAdapter(ArrayList<Slider> dataList, Activity activity, int layout, String from) {
            this.dataList = dataList;
            this.activity = activity;
            this.layout = layout;
            this.from = from;
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = LayoutInflater.from(activity).inflate(layout, view, false);

            assert imageLayout != null;
            //

            CardView lytmain = imageLayout.findViewById(R.id.lytmain);

            final Slider singleItem = dataList.get(position);

            // imgslider.setImageUrl(singleItem.getImage(), Constant.imageLoader);
       /* if (from.equalsIgnoreCase("detail") || from.equalsIgnoreCase("fullscreen") ) {
            NetworkImageView imgslider = imageLayout.findViewById(R.id.imgslider);
            imgslider.setImageUrl(singleItem.getImage(), Constant.imageLoader);
        }else {
            ImageView imgslider = imageLayout.findViewById(R.id.imgslider);
            Glide.with(activity)  //2
                    .load(singleItem.getImage()) //3
                    .centerCrop() //4
                    .placeholder(R.drawable.placeholder) //5
                    .error(R.drawable.placeholder) //6
                    .fallback(R.drawable.placeholder) //7
                    .into(imgslider);
        }
        /*
        imgslider.setDefaultImageResId(R.drawable.ic_banner_loading);
        imgslider.setErrorImageResId(R.drawable.ic_banner_loading);
        imgslider.setImageUrl(singleItem.getImage(), Constant.imageLoader);*/


            if(from.equals("fullscreen")){
                NetworkImageView imgslider = imageLayout.findViewById(R.id.imgslider);
                imgslider.setImageUrl(singleItem.getImage(), Constant.imageLoader);
            }else {
                SimpleDraweeView imgslider = imageLayout.findViewById(R.id.imgslider);
                Uri uri = Uri.parse(singleItem.getImage());
                imgslider.getHierarchy().setPlaceholderImage(R.drawable.ic_banner_loading);
                imgslider.getHierarchy().setFailureImage(R.drawable.ic_banner_loading);
                imgslider.setImageURI(uri);
            }


            view.addView(imageLayout, 0);

            lytmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (from.equalsIgnoreCase("detail")) {
                        Intent intent = new Intent(activity, FullScreenViewActivity.class);
                        intent.putExtra("pos", position);
                        activity.startActivity(intent);
                    } else {
                        if (singleItem.getType().equals("category")) {
                            Intent intent = new Intent(activity, SubCategoryActivity.class);
                            intent.putExtra("id", singleItem.getType_id());
                            intent.putExtra("name", singleItem.getName());
                            activity.startActivity(intent);
                        } else if (singleItem.getType().equals("product")) {
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
                            intent.putExtra("id", singleItem.getType_id());
                            intent.putExtra("vpos", 0);
                            intent.putExtra("from", "share");
                            activity.startActivity(intent);
                        }

                    }
                }
            });

            return imageLayout;
        }




        @Override
        public int getCount() {
            return (null != dataList ? dataList.size() : 0);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }