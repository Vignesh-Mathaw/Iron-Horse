package com.spiderindia.ironhorse.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.spiderindia.ironhorse.R;

public class ProductHolder extends RecyclerView.ViewHolder {
    TextView productName,productName2, productPrice, txtqty, Measurement, showDiscount, originalPrice, txtstatus, imgarrow,txtAdd, txt_MRP_price,
            offerpercentage,txtbrandname,txtratepercentage,txtratingcount;
    SimpleDraweeView imgThumb;
    ImageView imgFav, imgIndicator, imgdrop;
    CardView lytmain;
    AppCompatSpinner spinner;

    public ImageButton imgAdd, imgMinus;
    LinearLayout qtyLyt,lytQtymeasurement,lyt_spinner, lytdiscount;
    RelativeLayout lytoffershows, lytIndicator;

    public ProductHolder(View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        productName2 = itemView.findViewById(R.id.productName2);
        productPrice = itemView.findViewById(R.id.txtprice);
        showDiscount = itemView.findViewById(R.id.showDiscount);
        originalPrice = itemView.findViewById(R.id.txtoriginalprice);
        Measurement = itemView.findViewById(R.id.txtmeasurement);
        txtstatus = itemView.findViewById(R.id.txtstatus);
        imgThumb = itemView.findViewById(R.id.imgThumb);
        imgIndicator = itemView.findViewById(R.id.imgIndicator);
        imgarrow = itemView.findViewById(R.id.imgarrow);
        imgAdd = itemView.findViewById(R.id.btnaddqty);
        imgMinus = itemView.findViewById(R.id.btnminusqty);
        txtqty = itemView.findViewById(R.id.txtqty);
        qtyLyt = itemView.findViewById(R.id.qtyLyt);
        txtAdd = itemView.findViewById(R.id.txtAdd);
        imgFav = itemView.findViewById(R.id.imgFav);
        imgdrop = itemView.findViewById(R.id.imgdrop);
        lytmain = itemView.findViewById(R.id.lytmain);
        spinner = itemView.findViewById(R.id.spinner);
        lyt_spinner = itemView.findViewById(R.id.lyt_spinner);
        lytoffershows = itemView.findViewById(R.id.lytoffershows);
        lytdiscount = itemView.findViewById(R.id.lytdiscount);
        lytIndicator = itemView.findViewById(R.id.lytIndicator);

        txt_MRP_price = itemView.findViewById(R.id.txt_MRP_price);
        lytQtymeasurement = itemView.findViewById(R.id.lytQtymeasurement);
        offerpercentage = itemView.findViewById(R.id.offerpercentage);
        txtbrandname = itemView.findViewById(R.id.txtbrandname);
        txtratepercentage = itemView.findViewById(R.id.txtratepercentage);
        txtratingcount = itemView.findViewById(R.id.txtratingcount);

    }
}