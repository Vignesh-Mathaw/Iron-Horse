package com.spiderindia.ironhorse.adapter;

import static com.spiderindia.ironhorse.activity.MainActivity.session;
import static com.spiderindia.ironhorse.activity.TrackerDetailActivity.pBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.OnItemClick;
import com.spiderindia.ironhorse.activity.TrackerDetailActivity;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.OrderTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.CartItemHolder> {

    Activity activity;
    ArrayList<OrderTracker> orderTrackerArrayList;
    String from = "";

    private OnItemClick mCallback;

    public ItemsAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    public ItemsAdapter(Activity activity, ArrayList<OrderTracker> orderTrackerArrayList, String from, OnItemClick listener) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
        this.from = from;
        this.mCallback = listener;
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_lyt, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }
    String star_count ="";
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {

        final OrderTracker order = orderTrackerArrayList.get(position);

        String payType = "";
        if (order.getPayment_method().equalsIgnoreCase("cod"))
            payType = activity.getResources().getString(R.string.cod);
        else
            payType = order.getPayment_method();
        String activeStatus = order.getActiveStatus().substring(0, 1).toUpperCase() + order.getActiveStatus().substring(1).toLowerCase();
        holder.txtqty.setText(order.getQuantity());
        holder.txtprice.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getPrice());
        holder.txtpaytype.setText(activity.getResources().getString(R.string.via) + payType);
        holder.txtstatus.setText(activeStatus);
        holder.txtstatusdate.setText(order.getActiveStatusDate());
        holder.txtname.setText(order.getName());
        Uri uri = Uri.parse(order.getImage());
        holder.imgorder.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.imgorder.setImageURI(uri);
        holder.lyt_Rating.setVisibility(View.GONE);

        star_count = order.getRating().toString().trim();
        if (!star_count.equals("0")) {
            switch (order.getRating()) {
                case "1":
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar3.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    break;
                case "2":
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    break;
                case "3":
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    break;
                case "4":
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    break;
                case "5":
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar5.setImageResource(R.drawable.ic_rating_star);
                    break;
            }
        } else {
            holder.Imgstar1.setImageResource(R.drawable.ic_star_icon);
            holder.Imgstar2.setImageResource(R.drawable.ic_star_icon);
            holder.Imgstar3.setImageResource(R.drawable.ic_star_icon);
            holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
            holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
        }


        holder.Imgstar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    star_count = "1";
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar3.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    String strReview = holder.txtratingdesc.getText().toString().trim();
                    updateOrderStatus_rating(activity, order, star_count, holder, strReview);
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.Imgstar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    star_count = "2";
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    String strReview = holder.txtratingdesc.getText().toString().trim();
                    updateOrderStatus_rating(activity, order, star_count, holder, strReview);
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.Imgstar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    star_count = "3";
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_star_icon);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    String strReview = holder.txtratingdesc.getText().toString().trim();
                    updateOrderStatus_rating(activity, order, star_count, holder, strReview);
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.Imgstar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    star_count = "4";
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar5.setImageResource(R.drawable.ic_star_icon);
                    String strReview = holder.txtratingdesc.getText().toString().trim();
                    updateOrderStatus_rating(activity, order, star_count, holder, strReview);
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.Imgstar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    star_count = "5";
                    holder.Imgstar1.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar2.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar3.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar4.setImageResource(R.drawable.ic_rating_star);
                    holder.Imgstar5.setImageResource(R.drawable.ic_rating_star);
                    String strReview = holder.txtratingdesc.getText().toString().trim();
                    updateOrderStatus_rating(activity, order, star_count, holder, strReview);
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.btn_review_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!holder.txtratingdesc.getText().toString().trim().equals("")) {
                        String strReview = holder.txtratingdesc.getText().toString().trim();
                        updateOrderStatus_rating(activity, order, "", holder, strReview);
                        holder.txtratingdesc.setText("");
                        holder.btn_review_submit.setVisibility(View.GONE);
                        holder.txtratingdesc.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(activity,"Please Enter review", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        if (!order.getReview().toString().trim().equals("")) {
            holder.btn_review_submit.setVisibility(View.GONE);
            holder.txtratingdesc.setVisibility(View.GONE);
        } else {
            holder.btn_review_submit.setVisibility(View.VISIBLE);
            holder.txtratingdesc.setVisibility(View.VISIBLE);
        }
        holder.txtratingdesc.setText(order.getReview().toString().trim());

        holder.carddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, TrackerDetailActivity.class).putExtra("model", order));
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus(activity, order, Constant.CANCELLED, holder, from);
            }
        });


        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = new Date();
                System.out.println(myFormat.format(date));
                String inputString1 = order.getActiveStatusDate();
                String inputString2 = myFormat.format(date);
                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= Constant.ORDER_DAY_LIMIT) {
                        updateOrderStatus(activity, order, Constant.RETURNED, holder, from);
                    } else {
                        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.product_return) + Constant.ORDER_DAY_LIMIT + activity.getString(R.string.day_max_limit), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(activity.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        View snackbarView = snackbar.getView();
                        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setMaxLines(5);
                        snackbar.show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        if (from.equals("detail")) {
            if (order.getActiveStatus().equalsIgnoreCase("cancelled")) {
                holder.txtstatus.setTextColor(Color.RED);
                holder.btnCancel.setVisibility(View.GONE);
            } else if (order.getActiveStatus().equalsIgnoreCase("delivered")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.lyt_Rating.setVisibility(View.VISIBLE);
                //Backend return minimum date set
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = new Date();
                System.out.println(myFormat.format(date));
                String inputString1 = order.getActiveStatusDate();
                String inputString2 = myFormat.format(date);
                try {
                    Date date1 = myFormat.parse(inputString1);
                    Date date2 = myFormat.parse(inputString2);
                    long diff = date2.getTime() - date1.getTime();
                    if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= Constant.ORDER_DAY_LIMIT) {
                        holder.btnReturn.setVisibility(View.VISIBLE);
                    } else {
                        holder.btnReturn.setVisibility(View.GONE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (order.getActiveStatus().equalsIgnoreCase("returned")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.GONE);
            } else if (order.getActiveStatus().equalsIgnoreCase("shipped")) {
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.GONE);
            } else {
                holder.btnCancel.setVisibility(View.VISIBLE);
            }
            holder.lyttracker.setVisibility(View.GONE);
            if (order.getActiveStatus().equalsIgnoreCase("cancelled")) {
                holder.lyttracker.setVisibility(View.GONE);
            } else {
                if (order.getActiveStatus().equals("returned")) {
                    holder.l4.setVisibility(View.VISIBLE);
                    holder.returnLyt.setVisibility(View.VISIBLE);
                }
                holder.lyttracker.setVisibility(View.GONE);

                ApiConfig.setOrderTrackerLayout(activity, order, holder);
            }
        }

        if (!from.equals("detail")) {
            holder.txtprice.setVisibility(View.GONE);
            holder.txtpaytype.setVisibility(View.GONE);
            holder.carddetail.setVisibility(View.GONE);
            holder.lyt_Rating.setVisibility(View.GONE);
        }
        holder.txtpaytype.setVisibility(View.GONE);
        holder.txtstatusdate.setVisibility(View.GONE);
        holder.txtstatus.setVisibility(View.GONE);
        holder.txtmeasurement.setText(order.getMeasurement() +" "+order.getUnit());
        setAnimation(holder.itemView, position);
    }

    private void updateOrderStatus(final Activity activity, final OrderTracker order, final String status, final CartItemHolder holder, final String from) {

        final Map<String, String> params = new HashMap<>();
        params.put(Constant.UPDATE_ORDER_ITEM_STATUS, Constant.GetVal);
        params.put(Constant.ORDER_ITEM_ID, order.getId());
        params.put(Constant.ORDER_ID, order.getOrder_id());
        params.put(Constant.STATUS, status);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        if (status.equals(Constant.CANCELLED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.cancel_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.cancel_msg));
        } else if (status.equals(Constant.RETURNED)) {
            alertDialog.setTitle(activity.getResources().getString(R.string.return_order));
            alertDialog.setMessage(activity.getResources().getString(R.string.return_msg));
        }
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (pBar != null)
                    pBar.setVisibility(View.VISIBLE);
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        if (result) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (!object.getBoolean(Constant.ERROR)) {
                                    if (status.equals(Constant.CANCELLED)) {
                                        holder.btnCancel.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                        holder.txtstatus.setTextColor(Color.RED);
                                        holder.lyttracker.setVisibility(View.GONE);
                                        order.status = status;
                                        if (from.equals("detail")) {
                                            if (orderTrackerArrayList.size() == 1) {
                                                TrackerDetailActivity.btnCancel.setVisibility(View.GONE);
                                                TrackerDetailActivity.lyttracker.setVisibility(View.GONE);
                                            }
                                        }
                                        ApiConfig.getWalletBalance(activity, new Session(activity));
                                        mCallback.onClick("", order.getPrice(), "");
                                    } else {
                                        holder.btnReturn.setVisibility(View.GONE);
                                        holder.txtstatus.setText(status);
                                    }
                                    Constant.isOrderCancelled = true;
                                }
                                Toast.makeText(activity, object.getString("message"), Toast.LENGTH_LONG).show();
                                if (pBar != null)
                                    pBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, activity, Constant.ORDERPROCESS_URL, params, false);

            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtqty, txtprice, txtpaytype, txtstatus, txtstatusdate, txtname, txtratingdesc,
                txtmeasurement;
        SimpleDraweeView imgorder;
        CardView carddetail;
        RecyclerView recyclerView;
        Button btnCancel, btnReturn, btn_review_submit;
        View l4;
        LinearLayout lyttracker, returnLyt, lyt_Rating;
        ImageView Imgstar1, Imgstar2, Imgstar3, Imgstar4, Imgstar5;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtqty = itemView.findViewById(R.id.txtqty);
            txtprice = itemView.findViewById(R.id.txtprice);
            txtpaytype = itemView.findViewById(R.id.txtpaytype);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            txtstatusdate = itemView.findViewById(R.id.txtstatusdate);
            txtname = itemView.findViewById(R.id.txtname);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgorder = itemView.findViewById(R.id.imgorder);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
            //veeramani added Rating and review elements
            lyt_Rating = itemView.findViewById(R.id.lyt_Rating);
            txtratingdesc = itemView.findViewById(R.id.txtratingdesc);
            Imgstar1 = itemView.findViewById(R.id.star1);
            Imgstar2 = itemView.findViewById(R.id.star2);
            Imgstar3 = itemView.findViewById(R.id.star3);
            Imgstar4 = itemView.findViewById(R.id.star4);
            Imgstar5 = itemView.findViewById(R.id.star5);
            btn_review_submit = itemView.findViewById(R.id.btn_review_submit);
            txtmeasurement = itemView.findViewById(R.id.txtmeasurement);

        }
    }


    @Override
    public int getItemCount() {
        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private void updateOrderStatus_rating(final Activity activity, final OrderTracker order, final String rating, final CartItemHolder holder, final String review) {

        final Map<String, String> params = new HashMap<>();
        params.put("product_id", order.getProductid());
        params.put("order_id", order.getOrder_id());
        params.put("product_rating", "1");
        params.put("rating", rating);
        params.put("rating_desc", review);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));

        if (pBar != null)
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                order.setRating(rating);
                                order.setReview(review);
                               // Toast.makeText(activity, "Your t", Toast.LENGTH_LONG).show();
                                Constant.isOrderRating = true;
                            }
                            if (pBar != null)
                                pBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        try {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                //TranslateAnimation anim = new TranslateAnimation(0,-1000,0,-1000);
                ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(550);
                viewToAnimate.startAnimation(anim);
                lastPosition = position;

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}


