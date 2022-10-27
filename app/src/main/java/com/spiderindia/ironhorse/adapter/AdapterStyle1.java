package com.spiderindia.ironhorse.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.ProductDetailActivity;
import com.spiderindia.ironhorse.helper.AppController;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;

import java.util.ArrayList;

/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle1 extends RecyclerView.Adapter<AdapterStyle1.VideoHolder> {

    public ArrayList<Product> productList;

    public Activity activity;
    public int itemResource;
    ImageLoader netImageLoader = AppController.getInstance().getImageLoader();
    DatabaseHelper databaseHelper;

    public AdapterStyle1(Activity activity, ArrayList<Product> productList, int itemResource) {
        this.activity = activity;
        this.productList = productList;
        this.itemResource = itemResource;
        databaseHelper = new DatabaseHelper(activity);

    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView thumbnail;
        public TextView v_title, v_date, description,txtmeasurement,txtqty,txtamount,btnAdd;
        public RelativeLayout relativeLayout;
        ImageButton imgMinus,txtAdd;
        LinearLayout qtyLyt;

        public VideoHolder(View itemView) {
            super(itemView);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            v_title = (TextView) itemView.findViewById(R.id.title);
            v_date = (TextView) itemView.findViewById(R.id.date);
            txtmeasurement = (TextView) itemView.findViewById(R.id.txtmeasurement);
            txtamount = (TextView) itemView.findViewById(R.id.txtamount);
            btnAdd = (TextView) itemView.findViewById(R.id.txtAdd);
            txtqty = (TextView) itemView.findViewById(R.id.txtqty);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.play_layout);
            qtyLyt = (LinearLayout) itemView.findViewById(R.id.qtyLyt);
            imgMinus = (ImageButton) itemView.findViewById(R.id.btnminusqty);
            txtAdd = (ImageButton) itemView.findViewById(R.id.btnaddqty);
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
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final Product product = productList.get(position);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        product.setGlobalStock(Double.parseDouble(priceVariations.get(0).getStock()));

      /*  holder.thumbnail.setDefaultImageResId(R.drawable.placeholder);
        holder.thumbnail.setErrorImageResId(R.drawable.placeholder);
        holder.thumbnail.setImageUrl(product.getImage(), netImageLoader);*/
        Uri uri = Uri.parse(product.getImage());
        holder.thumbnail.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.thumbnail.getHierarchy().setFailureImage(R.drawable.placeholder);
        holder.thumbnail.setImageURI(uri);

        holder.v_title.setText(product.getName());
        SetSelectedData(product, holder, priceVariations.get(0));
    /*    holder.txtqty.setText("");
        holder.txtamount.setText("");*/
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
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemResource, parent, false);
        return new VideoHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void SetSelectedData(final Product product, final VideoHolder holder, final PriceVariation extra) {


        holder.txtamount.setText(extra.getProductPrice());
        holder.txtmeasurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!extra.getServe_for().equals(Constant.SOLDOUT_TEXT)) {
                    if (extra.getType().equals("loose")) {
                        String measurement = extra.getMeasurement_unit_name();

                        if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                            double totalKg;
                            if (measurement.equals("kg") || measurement.equals("ltr"))
                                totalKg = (Integer.parseInt(extra.getMeasurement()) * 1000);
                            else
                                totalKg = (Integer.parseInt(extra.getMeasurement()));
                            double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                            if (cartKg <= product.getGlobalStock()) {
                                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            RegularCartAdd(product, holder, extra);
                        }
                    } else {
                        RegularCartAdd(product, holder, extra);
                    }

                    activity.invalidateOptionsMenu();
                    String qty = holder.txtqty.getText().toString();
                    if (qty.equals("") || qty.equals("0")) {
                        holder.btnAdd.setVisibility(View.VISIBLE);
                        holder.qtyLyt.setVisibility(View.GONE);
                    } else {
                        holder.btnAdd.setVisibility(View.GONE);
                        holder.qtyLyt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), false, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                activity.invalidateOptionsMenu();

                String qty = holder.txtqty.getText().toString();
                if (qty.equals("") || qty.equals("0")) {
                    holder.btnAdd.setVisibility(View.VISIBLE);
                    holder.qtyLyt.setVisibility(View.GONE);
                } else {
                    holder.btnAdd.setVisibility(View.GONE);
                    holder.qtyLyt.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!extra.getServe_for().equals(Constant.SOLDOUT_TEXT)) {
                    if (extra.getType().equals("loose")) {
                        String measurement = extra.getMeasurement_unit_name();

                        if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                            double totalKg;
                            if (measurement.equals("kg") || measurement.equals("ltr"))
                                totalKg = (Integer.parseInt(extra.getMeasurement()) * 1000);
                            else
                                totalKg = (Integer.parseInt(extra.getMeasurement()));
                            double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                            if (cartKg <= product.getGlobalStock()) {
                                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            RegularCartAdd(product, holder, extra);
                        }
                    } else {
                        RegularCartAdd(product, holder, extra);
                    }

                    activity.invalidateOptionsMenu();
                    String qty = holder.txtqty.getText().toString();
                    if (qty.equals("") || qty.equals("0")) {
                        holder.btnAdd.setVisibility(View.VISIBLE);
                        holder.qtyLyt.setVisibility(View.GONE);
                    } else {
                        holder.btnAdd.setVisibility(View.GONE);
                        holder.qtyLyt.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), product.getId()));
        String qty = holder.txtqty.getText().toString();
        if (qty.equals("") || qty.equals("0")) {
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            holder.btnAdd.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }
    }

    public void RegularCartAdd(final Product product, final VideoHolder holder, final PriceVariation extra) {

        if (Double.parseDouble(databaseHelper.CheckOrderExists(extra.getId(), product.getId())) < Double.parseDouble(extra.getStock()))
            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }
}




