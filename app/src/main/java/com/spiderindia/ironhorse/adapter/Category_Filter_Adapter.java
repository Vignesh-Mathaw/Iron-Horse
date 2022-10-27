package com.spiderindia.ironhorse.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.OnItemClick;
import com.spiderindia.ironhorse.model.Category;

import java.util.ArrayList;

public class Category_Filter_Adapter extends RecyclerView.Adapter<com.spiderindia.ironhorse.adapter.Category_Filter_Adapter.ViewHolder> {
    public ArrayList<Category> categorylist;
    int layout;
    String from = "",CatID;
    Activity activity;
    private static int lastClickedPosition = -1;
    private int selectedItem, selectcat;

    private OnItemClick mCallback;

    public Category_Filter_Adapter(Activity activity, ArrayList<Category> categorylist, int layout, String from, OnItemClick listener, String catid) {
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        this.mCallback = listener;
        CatID = catid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Category model = categorylist.get(position);
        holder.setIsRecyclable(false);
        holder.txttitle.setText(model.getName());
        if (CatID.equals(model.getId())) {
            selectcat = position;
            if (selectcat == position) {
                holder.txttitle.setBackgroundColor(activity.getResources().getColor(R.color.select_cat));
                holder.txtback.setVisibility(View.GONE);
            }
        } else {
            holder.txttitle.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.txtback.setVisibility(View.GONE);
        }

        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getStatus_get().equals("No")) {
                    mCallback.onClick("item", model.getName(), model.getId());
                    CatID = model.getId();
                    selectcat = position;
                    notifyDataSetChanged();
                } else {
                    mCallback.onClick("cate", model.getName(), model.getId());
                    CatID = model.getId();
                    selectcat = position;
                    notifyDataSetChanged();
                }
            }
        });
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
        public TextView txttitle,txtback;
        LinearLayout lytMain;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            txttitle = itemView.findViewById(R.id.txttitle);
            txtback = itemView.findViewById(R.id.txtback);
        }

    }
}
