package com.spiderindia.ironhorse.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.ProductListActivity;
import com.spiderindia.ironhorse.activity.SubCategoryActivity;
import com.spiderindia.ironhorse.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    public ArrayList<Category> categorylist;
    int layout;
    String from = "";
    Activity activity;


    public CategoryAdapter(Activity activity, ArrayList<Category> categorylist, int layout, String from) {
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = categorylist.get(position);
        holder.txttitle.setText(model.getName());
        holder.txtsubtitle.setText(model.getSubtitle());
        Uri imguri = Uri.parse(model.getImage());
        holder.imgcategory.getHierarchy().setFailureImage(R.drawable.placeholder);
        holder.imgcategory.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.imgcategory.setImageURI(imguri);
//        holder.lytMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = null;
//                String status = model.getStatus_get();
//                intent = new Intent(activity, CatProductListActivity.class);
//                if(status.equals("No")) {
//                    intent.putExtra("from", "item");
//                }else {
//                    intent.putExtra("from", "regular");
//                }
//                intent.putExtra("id", model.getId());
//                intent.putExtra("name", model.getName());
//                activity.startActivity(intent);
//            }
//        });

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                String status = model.getStatus_get();
                if(status.equals("No")){
                    intent = new Intent(activity, ProductListActivity.class);
                    intent.putExtra("from", "item");
                }else {
                    if (from.equals("cate")) {
                        intent = new Intent(activity, SubCategoryActivity.class);
                    } else if (from.equals("sub_cate")) {
                        intent = new Intent(activity, ProductListActivity.class);
                        intent.putExtra("from", "regular");
                    }
                }
                intent.putExtra("id", model.getId());
                intent.putExtra("name", model.getName());
                activity.startActivity(intent);
            }
        });

        setAnimation(holder.itemView, position);
    }

    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        try {
            if (position > lastPosition) {
                ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(400);
                viewToAnimate.startAnimation(anim);
                lastPosition = position;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txttitle,txtsubtitle;
        SimpleDraweeView imgcategory;
        LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            imgcategory = itemView.findViewById(R.id.imgcategory);
            txttitle = itemView.findViewById(R.id.txttitle);
            txtsubtitle = itemView.findViewById(R.id.txtsubtitle);
        }

    }
}
