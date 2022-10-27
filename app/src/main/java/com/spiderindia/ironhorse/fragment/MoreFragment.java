package com.spiderindia.ironhorse.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.activity.LoginActivity;
import com.spiderindia.ironhorse.activity.NotificationList;
import com.spiderindia.ironhorse.activity.OrderListActivity;
import com.spiderindia.ironhorse.activity.ProfileActivity;
import com.spiderindia.ironhorse.activity.ReferEarnActivity;
import com.spiderindia.ironhorse.activity.WebViewActivity;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.Constant;

public class MoreFragment extends Fragment {
    private TextView txtUsername_Two,txtUsername,txtMobile,txtEditProfile,txtwallet;
    LinearLayout lytmyorder,lytnotification,lytcontactus,lytabout,lytrateus,lytshare,lytRefer,lytFAQ,lytterms,lytprivacy,lytlogout;

    private Activity activity;
    public static Session session;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            activity = getActivity();
            setHasOptionsMenu(true);
            session = new Session(getActivity());
            txtUsername_Two = view.findViewById(R.id.txtUsername_Two);
            txtUsername = view.findViewById(R.id.txtUsername);
            txtMobile = view.findViewById(R.id.txtMobile);
            txtEditProfile = view.findViewById(R.id.txtEditProfile);
            txtwallet = view.findViewById(R.id.txtwallet);
            lytmyorder = view.findViewById(R.id.lytmyorder);
            lytnotification = view.findViewById(R.id.lytnotification);
            lytcontactus = view.findViewById(R.id.lytcontactus);
            lytabout = view.findViewById(R.id.lytabout);
            lytrateus = view.findViewById(R.id.lytrateus);
            lytshare = view.findViewById(R.id.lytshare);
            lytRefer = view.findViewById(R.id.lytRefer);
            lytFAQ = view.findViewById(R.id.lytFAQ);
            lytterms = view.findViewById(R.id.lytterms);
            lytprivacy = view.findViewById(R.id.lytprivacy);
            lytlogout = view.findViewById(R.id.lytlogout);

            String upperString = session.getData(Session.KEY_NAME).substring(0, 1).toUpperCase() + session.getData(Session.KEY_NAME).substring(1).toLowerCase();
            txtUsername.setText(upperString);
            txtMobile.setText(session.getData(Session.KEY_MOBILE));
            //Double percentage = Double.valueOf(Constant.WALLET_BALANCE);
            //BigDecimal bd = new BigDecimal(percentage).setScale(0, RoundingMode.HALF_UP);
            txtwallet.setText(String.valueOf(Constant.WALLET_BALANCE));
            txtUsername_Two.setText(session.getData(Session.KEY_NAME).substring(0, 2).toUpperCase().toString().trim());

            txtEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(activity, ProfileActivity.class));
                        else
                            startActivity(new Intent(activity, LoginActivity.class));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytmyorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(activity, OrderListActivity.class));
                        } else
                            startActivity(new Intent(activity, LoginActivity.class));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytnotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(activity, NotificationList.class));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytcontactus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent contact = new Intent(activity, WebViewActivity.class);
                        contact.putExtra("type", "contact");
                        startActivity(contact);
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            lytabout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent about = new Intent(activity, WebViewActivity.class);
                        about.putExtra("type", "about");
                        startActivity(about);
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytrateus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + activity.getPackageName())));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.take_a_look) + "\"" + getString(R.string.app_name) + "\" - " + Constant.PLAY_STORE_LINK + activity.getPackageName());
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytRefer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(activity, ReferEarnActivity.class));
                        else
                            startActivity(new Intent(activity, LoginActivity.class));
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytFAQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent faq = new Intent(activity, WebViewActivity.class);
                        faq.putExtra("type", "faq");
                        startActivity(faq);
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytterms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent terms = new Intent(activity, WebViewActivity.class);
                        terms.putExtra("type", "terms");
                        startActivity(terms);
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytprivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent privacy = new Intent(activity, WebViewActivity.class);
                        privacy.putExtra("type", "privacy");
                        startActivity(privacy);

                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            lytlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        session.logoutUser(activity);
                    } catch (Exception ex) {
                        Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch (Exception ex){
            Toast.makeText(activity,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setVisible(false);
    }
}
