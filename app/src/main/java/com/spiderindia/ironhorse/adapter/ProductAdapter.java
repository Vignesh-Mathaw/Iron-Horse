package com.spiderindia.ironhorse.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.ProductDetailActivity;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.DatabaseHelper;
import com.spiderindia.ironhorse.model.PriceVariation;
import com.spiderindia.ironhorse.model.Product;

import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
    public ArrayList<Product> productList;
    public Activity activity;
    public int resource;
    SpannableString spannableString;
    DatabaseHelper databaseHelper;

    public ProductAdapter(ArrayList<Product> productList, int resource, Activity activity) {
        this.productList = productList;
        this.resource = resource;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, null);
        ProductHolder productHolder = new ProductHolder(v);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.setIsRecyclable(false);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        product.setGlobalStock(Double.parseDouble(priceVariations.get(0).getStock()));
        holder.lytQtymeasurement.setVisibility(View.GONE);
        holder.spinner.setVisibility(View.VISIBLE);
        holder.lyt_spinner.setVisibility(View.VISIBLE);
        if (priceVariations.size() == 1) {
            holder.imgarrow.setVisibility(View.GONE);
            holder.spinner.setVisibility(View.GONE);
            holder.lyt_spinner.setVisibility(View.GONE);
            holder.lytQtymeasurement.setVisibility(View.VISIBLE);
        }

        if (!product.getIndicator().equals("0")) {
            holder.imgIndicator.setVisibility(View.VISIBLE);
            if (product.getIndicator().equals("1"))
                holder.imgIndicator.setImageResource(R.drawable.veg_icon);
            else if (product.getIndicator().equals("2"))
                holder.imgIndicator.setImageResource(R.drawable.non_veg_icon);
        }
        //product name only showing in list
        // holder.productName.setText(Html.fromHtml("<font color='#000000'><b>" + product.getName() + "</b></font> - <small>" + product.getDescription().replaceFirst("<p>", "").replaceFirst("</p>", "") + "</small>"));
        //holder.productName.setText(Html.fromHtml("<font color='#000000'><b>" + product.getName() + "</b></font>"));
        String strProduct = product.getName().toString().trim();
        String[] split = strProduct.split(",");
        String secondSubString = "";
        String firstSubString = split[0];
        try {
            secondSubString = split[1];
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("The index you have entered is invalid");
        }

        holder.productName.setText(Html.fromHtml("<font color='#000000'><b>" + firstSubString + "</b></font>"));
        if(!secondSubString.toString().trim().equals("")) {
            holder.productName2.setVisibility(View.VISIBLE);
            holder.productName2.setText(Html.fromHtml("<font color='#000000'><b>" + secondSubString + "</b></font>"));
        }

        //holder.imgThumb.setImageUrl(product.getImage(), Constant.imageLoader);
        holder.txtbrandname.setText(product.getBrandname().toString().trim());
        holder.txtratingcount.setText(product.getRating_count().toString().trim()+" ratings");
        Double rating = Double.valueOf(product.getRating().toString());
        holder.txtratepercentage.setText(String.format(Locale.US, "%.1f", rating));
    /*    holder.imgThumb.setDefaultImageResId(R.drawable.placeholder);
        holder.imgThumb.setErrorImageResId(R.drawable.placeholder);
        holder.imgThumb.setImageUrl(product.getImage(), Constant.imageLoader);*/

        Uri uri = Uri.parse(product.getImage());
        holder.imgThumb.getHierarchy().setPlaceholderImage(R.drawable.placeholder);
        holder.imgThumb.getHierarchy().setFailureImage(R.drawable.placeholder);
        holder.imgThumb.setImageURI(uri);


        CustomAdapter customAdapter = new CustomAdapter(activity, priceVariations, holder, product);
        holder.spinner.setAdapter(customAdapter);

        holder.imgarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.spinner.performClick();
            }
        });

        SetSelectedData(product, holder, priceVariations.get(0));


        holder.imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition()).putExtra("model", product));
            }
        });

        holder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition()).putExtra("model", product));
            }
        });

//        holder.lytmain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
//                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition()).putExtra("model", product));
//            }
//        });

        ApiConfig.SetFavOnImg(databaseHelper, holder.imgFav, product.getId());

        holder.imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiConfig.AddRemoveFav(databaseHelper, holder.imgFav, product.getId());
            }
        });

        // setAnimation(holder.itemView, position);
    }

    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position) {
        try {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                //TranslateAnimation anim = new TranslateAnimation(0,-1000,0,-1000);
                ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
                anim.setDuration(550);//to make duration random number between [0,501)
                viewToAnimate.startAnimation(anim);
                lastPosition = position;

            }
        }catch (Exception ex){
            ex.printStackTrace();
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


    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        ProductHolder holder;
        Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ProductHolder holder, Product product) {
            this.context = applicationContext;
            this.extraList = extraList;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return extraList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);
            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

            if (i == 0) {
                txttitle.setVisibility(View.GONE);
            } else {
                txttitle.setVisibility(View.GONE);
            }

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
                price.setTextColor(context.getResources().getColor(R.color.black));
            }
            holder.imgdrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.spinner.performClick();
                }
            });

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    PriceVariation extra = extraList.get(i);
                    SetSelectedData(product, holder, extra);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            String qty = holder.txtqty.getText().toString();
            if (qty.equals("") || qty.equals("0")) {
                holder.txtAdd.setVisibility(View.VISIBLE);
                holder.qtyLyt.setVisibility(View.GONE);
            } else {
                holder.txtAdd.setVisibility(View.GONE);
                holder.qtyLyt.setVisibility(View.VISIBLE);
            }







            return view;
        }
    }

    public void SetSelectedData(final Product product, final ProductHolder holder, final PriceVariation extra) {
        // holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        // holder.productPrice.setText(activity.getResources().getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        // holder.productPrice.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        holder.txtstatus.setText(extra.getServe_for());
        holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        // holder.productPrice.setText(mactivity.getResources().getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        holder.productPrice.setText(extra.getProductPrice());
        holder.offerpercentage.setText(extra.getDiscountpercent());

        if(String.valueOf(extra.getDiscountpercent()).equals("0%") || String.valueOf(extra.getDiscountpercent()).equals("0")){
            holder.lytoffershows.setVisibility(View.GONE);
        }else{
            holder.lytoffershows.setVisibility(View.VISIBLE);
        }

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
            holder.productPrice.setText(activity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
            holder.txt_MRP_price.setText((Html.fromHtml("<strike>" + extra.getPrice() + "</strike>")));
        } else {
            spannableString = new SpannableString(activity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            double diff = Double.parseDouble(extra.getPrice()) - Double.parseDouble(extra.getProductPrice());
            // holder.showDiscount.setText(mactivity.getResources().getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + extra.getDiscountpercent());
            // holder.showDiscount.setText(Constant.SETTING_CURRENCY_SYMBOL + diff + extra.getDiscountpercent());
            holder.showDiscount.setText(extra.getDiscountpercent());
            holder.txt_MRP_price.setText((Html.fromHtml("<strike>" + extra.getPrice() + "</strike>")));
        }

        if(extra.getPrice().equals(extra.getProductPrice())){
            holder.lytdiscount.setVisibility(View.GONE);
        }else{
            holder.lytdiscount.setVisibility(View.VISIBLE);
        }

        if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            holder.txtstatus.setVisibility(View.VISIBLE);
            holder.txtstatus.setTextColor(Color.RED);
            holder.qtyLyt.setVisibility(View.GONE);
            holder.lytIndicator.setVisibility(View.GONE);
        } else {
            holder.txtstatus.setVisibility(View.INVISIBLE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
            holder.lytIndicator.setVisibility(View.VISIBLE);
        }




        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
                            holder.txtAdd.setVisibility(View.VISIBLE);
                            holder.qtyLyt.setVisibility(View.GONE);
                        } else {
                            holder.txtAdd.setVisibility(View.GONE);
                            holder.qtyLyt.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), false, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                    activity.invalidateOptionsMenu();

                    String qty = holder.txtqty.getText().toString();
                    if (qty.equals("") || qty.equals("0")) {
                        holder.txtAdd.setVisibility(View.VISIBLE);
                        holder.qtyLyt.setVisibility(View.GONE);
                    } else {
                        holder.txtAdd.setVisibility(View.GONE);
                        holder.qtyLyt.setVisibility(View.VISIBLE);
                    }
                }catch (Exception ex){
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
                            holder.txtAdd.setVisibility(View.VISIBLE);
                            holder.qtyLyt.setVisibility(View.GONE);
                        } else {
                            holder.txtAdd.setVisibility(View.GONE);
                            holder.qtyLyt.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), product.getId()));
        String qty = holder.txtqty.getText().toString();
        if (qty.equals("") || qty.equals("0")) {
            holder.txtAdd.setVisibility(View.VISIBLE);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            holder.txtAdd.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }

        if(Constant.StoreOpenStatus.equals("0"))
        {
            holder.txtAdd.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.GONE);
        }


        //  setAnimation(holder.itemView, position);
    }

    public void RegularCartAdd(final Product product, final ProductHolder holder, final PriceVariation extra) {

        if (Double.parseDouble(databaseHelper.CheckOrderExists(extra.getId(), product.getId())) < Double.parseDouble(extra.getStock()))
            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }
}
