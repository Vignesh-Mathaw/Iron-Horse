package com.spiderindia.ironhorse.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.AddressAddActivity;
import com.spiderindia.ironhorse.activity.OnItemClick;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Address;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> implements View.OnClickListener {

    public ArrayList<Address> categorylist;
    int layout;
    String from = "";
    Activity activity;
    int lastposition;

    private OnItemClick mCallback;

    public AddressAdapter(Activity activity, ArrayList<Address> categorylist, int layout, String from, OnItemClick listener) {
        this.categorylist = categorylist;
        this.layout = layout;
        this.activity = activity;
        this.from = from;
        this.lastposition = 0;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Address model = categorylist.get(position);
        holder.tvName.setText(model.getName());
        holder.tvAddress.setText(model.getStreet()+", "+model.getArea_name()+", "+model.getCity_name()+" ,"+model.getPincode());
        holder.tvMobile.setText(model.getMobile());

        if(lastposition == position){
            holder.imgSelect.setBackgroundResource(R.drawable.ic_select_radio);
            session.setData(Session.KEY_DELIVERY_ADDRESS,model.getFlat_no()+" "+model.getStreet());
            session.setData(Session.KEY_DELIVERY_CITYID,model.getCity_id());
            session.setData(Session.KEY_DELIVERY_AREAID,model.getArea_id());
            session.setData(Session.KEY_DELIVERY_LAT,model.getLatitude());
            session.setData(Session.KEY_DELIVERY_LONG,model.getLongitude());
            session.setData(Session.KEY_DELIVERY_PINCODE,model.getPincode());

            session.setData(Session.KEY_DELIVERY_AREA,model.getArea_name());
            session.setData(Session.KEY_DELIVERY_CITY,model.getCity_name());
            session.setData(Session.KEY_DELIVERY_MOBILE,model.getMobile());
            session.setData(Session.KEY_DELIVERY_ID,model.getId());
            session.setData(Session.KEY_DELIVERY_NAME,model.getName());
        }else {
            holder.imgSelect.setBackgroundResource(R.drawable.ic_unselect_radio);
        }


        if(categorylist.get(position).getIs_default().equals("1"))
        {
            holder.tvEdit.setVisibility(View.GONE);
            holder.tvDelete.setVisibility(View.GONE);
        }


        holder.lytMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastposition = position;
                notifyDataSetChanged();
            }
        });


        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddressAddActivity.class);
                intent.putExtra("street",model.getStreet());
                intent.putExtra("flatno",model.getFlat_no());
                intent.putExtra("city_id",model.getCity_id());
                intent.putExtra("area_id",model.getArea_id());
                intent.putExtra("area_name",model.getArea_name());
                intent.putExtra("city_name",model.getCity_name());

                intent.putExtra("pincode",model.getPincode());
                intent.putExtra("landmark",model.getLandmark());
                intent.putExtra("mobile",model.getMobile());
                intent.putExtra("name",model.getName());
                intent.putExtra("latitute",model.getLatitude());
                intent.putExtra("longtitude",model.getLongitude());
                intent.putExtra("id",model.getId());
                activity.startActivity(intent);
            }
        });


        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type","delete_address");
                params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                params.put(Constant.ID, model.getId());
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                JSONObject json = new JSONObject(response);
                                Toast.makeText(activity,json.getString("message").toString().trim(),Toast.LENGTH_LONG).show();
                                mCallback.onClick("item", model.getName(), model.getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, activity, Constant.USER_ADDRESSES, params, false);
            }
        });

        setAnimation(holder.itemView, position);
    }

    private int lastPosition = -1;

    Session session;
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

    @Override
    public void onClick(View view) {

    }


    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public interface OnItemClickListener {
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvName, tvAddress, tvMobile, tvEdit,tvDelete;
        LinearLayout lytMain;

        ImageView imgSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            imgSelect = itemView.findViewById(R.id.imgSelect);
            session = new Session(activity);

            itemView.setClickable(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Toast.makeText(activity, "sdf", Toast.LENGTH_SHORT).show();
        }
    }



}
