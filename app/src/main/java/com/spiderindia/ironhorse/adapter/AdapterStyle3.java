package com.spiderindia.ironhorse.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.ProductDetailActivity;
import com.spiderindia.ironhorse.helper.AppController;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;

import java.util.ArrayList;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle3 extends RecyclerView.Adapter<AdapterStyle3.VideoHolder> {
    public ArrayList<Product> productList;

    public Activity activity;
    public int itemResource;
    ImageLoader netImageLoader = AppController.getInstance().getImageLoader();

    public AdapterStyle3(Activity activity, ArrayList<Product> productList, int itemResource) {
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;

    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView thumbnail;
        public TextView v_title, v_date, description;
        public RelativeLayout relativeLayout;
        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            v_title = (TextView) itemView.findViewById(R.id.title);
            v_date = (TextView) itemView.findViewById(R.id.date);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.play_layout);

        }
    }


    @Override
    public int getItemCount() {
        int product;
        if (productList.size() > 4) {
            product = 4;
        } else {
            product = productList.size();
        }
        return product;
    }

    @Override
    public void onBindViewHolder(com.spiderindia.ironhorse.adapter.AdapterStyle3.VideoHolder holder, final int position) {
        final Product product = productList.get(position);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        product.setGlobalStock(Double.parseDouble(priceVariations.get(0).getStock()));
      //  holder.thumbnail.setImageUrl(product.getImage(), netImageLoader);
        Uri uri = Uri.parse(product.getImage());
        holder.thumbnail.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.thumbnail.setImageURI(uri);
        holder.v_title.setText(product.getName());

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", product));


            }
        });

        holder.v_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", product));


            }
        });

//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", product));
//
//
//            }
//        });
    }

    @Override
    public com.spiderindia.ironhorse.adapter.AdapterStyle3.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new com.spiderindia.ironhorse.adapter.AdapterStyle3.VideoHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}




