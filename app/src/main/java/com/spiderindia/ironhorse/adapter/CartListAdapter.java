package com.spiderindia.ironhorse.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;

import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.activity.CartActivity;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;

import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartItemHolder> {
    public ArrayList<Product> productList;
    public Activity activity;
    SpannableString spannableString;
    DatabaseHelper databaseHelper;


    public CartListAdapter(ArrayList<Product> cartDataList, Activity activity) {
        this.productList = cartDataList;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }


    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_cartlist, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {
        final Product order = productList.get(position);
        final PriceVariation priceVariation = order.getPriceVariations().get(0);
        order.setGlobalStock(Double.parseDouble(priceVariation.getStock()));
        holder.txtMenuName.setText(order.getName());
        holder.txtQuantity.setText(priceVariation.getQty() + "");
        holder.txtMeasurement.setText(priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name());
        //holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(priceVariation.getTotalprice()));
        holder.txttotalprice.setText( DatabaseHelper.decimalformatData.format(priceVariation.getTotalprice()));
       /* holder.thumb.setDefaultImageResId(R.drawable.placeholder);
        holder.thumb.setErrorImageResId(R.drawable.placeholder);
        holder.thumb.setImageUrl(order.getImage(), Constant.imageLoader);*/
        Uri imguri = Uri.parse(order.getImage());
        holder.thumb.getHierarchy().setFailureImage(R.drawable.placeholder);
        holder.thumb.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.thumb.setImageURI(imguri);


      //  holder.txtprice.setText(" Offer Price : " + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getProductPrice());
        holder.txtprice.setText(priceVariation.getProductPrice());

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
        } else {
           // spannableString = new SpannableString("M.R.P : " + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString = new SpannableString(priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            holder.showDiscount.setText("You save :" + priceVariation.getDiscountpercent());
        }

        holder.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (priceVariation.getType().equals("loose")) {
                    String measurement = priceVariation.getMeasurement_unit_name();

                    if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                        double totalKg;
                        if (measurement.equals("kg") || measurement.equals("ltr"))
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                        else
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                        double cartKg = ((databaseHelper.getTotalKG(order.getId()) + totalKg) / 1000);

                        if (cartKg <= order.getGlobalStock()) {
                            SetData(true, holder, priceVariation, order);
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd(order, holder, priceVariation);
                    }
                } else {
                    RegularCartAdd(order, holder, priceVariation);
                }
                String qty = holder.txtQuantity.getText().toString();
                if(qty.equals("")||qty.equals("0")){
                    holder.txtAdd.setVisibility(View.VISIBLE);
                    holder.lytqty.setVisibility(View.GONE);
                }else{
                    holder.txtAdd.setVisibility(View.GONE);
                    holder.lytqty.setVisibility(View.VISIBLE);
                }

                if(Constant.StoreOpenStatus.equals("0"))
                {
                    holder.txtAdd.setVisibility(View.GONE);
                    holder.lytqty.setVisibility(View.GONE);
                }


            }
        });

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceVariation.getType().equals("loose")) {
                    String measurement = priceVariation.getMeasurement_unit_name();

                    if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                        double totalKg;
                        if (measurement.equals("kg") || measurement.equals("ltr"))
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                        else
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                        double cartKg = ((databaseHelper.getTotalKG(order.getId()) + totalKg) / 1000);

                        if (cartKg <= order.getGlobalStock()) {
                            SetData(true, holder, priceVariation, order);
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd(order, holder, priceVariation);
                    }
                } else {
                    RegularCartAdd(order, holder, priceVariation);
                }
                String qty = holder.txtQuantity.getText().toString();
                if(qty.equals("")||qty.equals("0")){
                    holder.txtAdd.setVisibility(View.VISIBLE);
                    holder.lytqty.setVisibility(View.GONE);
                }else{
                    holder.txtAdd.setVisibility(View.GONE);
                    holder.lytqty.setVisibility(View.VISIBLE);
                }

                if(Constant.StoreOpenStatus.equals("0"))
                {
                    holder.txtAdd.setVisibility(View.GONE);
                    holder.lytqty.setVisibility(View.GONE);
                }


            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetData(false, holder, priceVariation, order);
                String qty = holder.txtQuantity.getText().toString();
                if(qty.equals("")||qty.equals("0")){
                    holder.txtAdd.setVisibility(View.VISIBLE);
                    holder.lytqty.setVisibility(View.GONE);
                }else{
                    holder.txtAdd.setVisibility(View.GONE);
                    holder.lytqty.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos",0).putExtra("model",order));
            }
        });


        holder.imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getString(R.string.deleteproducttitle));
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setMessage(activity.getResources().getString(R.string.deleteproductmsg));

                builder.setCancelable(false);
                builder.setPositiveButton(activity.getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.DeleteOrderData(priceVariation.getId(), order.getId());
                        productList.remove(position);
                        CartActivity.SetDataTotal();
                        notifyItemRemoved(position);
                        activity.invalidateOptionsMenu();
                        if (getItemCount() == 0) {
                            CartActivity.lytempty.setVisibility(View.VISIBLE);
                            CartActivity.lyttotal.setVisibility(View.GONE);
                        }

                    }
                });

                builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        String qty = holder.txtQuantity.getText().toString();
        if(qty.equals("")||qty.equals("0")){
            holder.txtAdd.setVisibility(View.VISIBLE);
            holder.lytqty.setVisibility(View.GONE);
        }else{
            holder.txtAdd.setVisibility(View.GONE);
            holder.lytqty.setVisibility(View.VISIBLE);
        }

        if(Constant.StoreOpenStatus.equals("0"))
        {
            holder.txtAdd.setVisibility(View.GONE);
            holder.lytqty.setVisibility(View.GONE);
            holder.imgdelete.setVisibility(View.GONE);
        }

        setAnimation(holder.itemView, position);
    }

    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        try {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                //TranslateAnimation anim = new TranslateAnimation(0,-1000,0,-1000);
                ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
                anim.setDuration(400);//to make duration random number between [0,501)
                viewToAnimate.startAnimation(anim);
                lastPosition = position;

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void RegularCartAdd(final Product order, final CartItemHolder holder, final PriceVariation priceVariation) {
        if (Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), order.getId())) < Double.parseDouble(priceVariation.getStock()))
            SetData(true, holder, priceVariation, order);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void SetData(boolean isadd, CartItemHolder holder, PriceVariation priceVariation, Product order) {
        String[] qty_total = databaseHelper.AddUpdateOrder(priceVariation.getId(), order.getId(), isadd, activity, true, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + order.getName() + "==" + priceVariation.getProductPrice()).split("=");
        holder.txtQuantity.setText(qty_total[0]);
       // holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL + qty_total[1]);
        holder.txttotalprice.setText(qty_total[1]);
        CartActivity.SetDataTotal();
    }


    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtMenuName, txtQuantity, txttotalprice, txtMeasurement, txtprice, count,txtAdd;
        ImageView imgdelete, imgAdd, imgMinus;
        SimpleDraweeView thumb;
        TextView showDiscount, originalPrice;
        RelativeLayout lytmain;
        LinearLayout lytqty;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtMenuName = (TextView) itemView.findViewById(R.id.txtproductname);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            txttotalprice = (TextView) itemView.findViewById(R.id.txttotalprice);
            txtMeasurement = (TextView) itemView.findViewById(R.id.txtmeasurement);
            txtprice = (TextView) itemView.findViewById(R.id.txtprice);
            thumb = (SimpleDraweeView) itemView.findViewById(R.id.imgproduct);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            imgdelete = (ImageView) itemView.findViewById(R.id.imgdelete);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            lytmain = itemView.findViewById(R.id.lytmain);
            txtAdd = itemView.findViewById(R.id.txtAdd);
            lytqty = itemView.findViewById(R.id.lytqty);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
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
