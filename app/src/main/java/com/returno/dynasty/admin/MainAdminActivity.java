package com.returno.dynasty.admin;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.returno.dynasty.R;
import com.returno.dynasty.admin.listeners.AnalyticsListener;
import com.returno.dynasty.admin.ui.AddDrinksFragment;
import com.returno.dynasty.admin.ui.AddOffersFragment;
import com.returno.dynasty.admin.ui.DeleteDrinksFragment;
import com.returno.dynasty.admin.ui.DeleteOffersFragment;
import com.returno.dynasty.admin.ui.MessagesFragment;
import com.returno.dynasty.admin.utils.FetchUtils;
import com.returno.dynasty.callbacks.FetchCallBacks;
import com.returno.dynasty.custom.AdminCashBacksDialog;
import com.returno.dynasty.models.CashBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class MainAdminActivity extends AppCompatActivity {
    private DrawerLayout navigationDrawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BarChart userStatsView, orderStatsView;PieChart mostOrderedStatsView,mostOrderedDrinksView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        toolbar = findViewById(R.id.toolbar);
        orderStatsView=findViewById(R.id.orderStats);
        mostOrderedStatsView=findViewById(R.id.mostOrdered);
        mostOrderedDrinksView=findViewById(R.id.drinksStats);
        navigationDrawer=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        userStatsView=findViewById(R.id.userStats);

        setSupportActionBar(toolbar);
        setupNavigationDrawer();
        fetchAnalytics();

         }

    private void fetchAnalytics() {

        Dialog pDialog=new Dialog(MainAdminActivity.this);
        pDialog.setContentView(R.layout.progress_dialog_transparent);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setCancelable(false);
        pDialog.show();

        new FetchUtils().fetchAnalytics(new AnalyticsListener() {
            @Override
            public void onError(String message) {
                pDialog.dismiss();
            }

            @Override
            public void onAnalytics(HashMap<String, Integer> usersMap, HashMap<String, Integer> categoryMap, HashMap<String, Integer> dayCategoryMap) {
pDialog.dismiss();

                List<BarEntry> usageDaysEntry=new ArrayList<>();
                List<String> labels=new ArrayList<>();
                int index=0;
                for(Map.Entry<String,Integer>maps:usersMap.entrySet()){
                    usageDaysEntry.add(new BarEntry(index,maps.getValue()));
                    labels.add(maps.getKey());
                    Timber.e("%s Hello %s", maps.getKey(), maps.getValue());
                    index++;
                }

                BarDataSet barDataSet=new BarDataSet(usageDaysEntry,"Users");
                BarData data=new BarData(barDataSet);
                data.setBarWidth(0.4f);

                userStatsView.setData(data);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                userStatsView.animateY(5000);
                XAxis axis=userStatsView.getXAxis();
                axis.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis.setLabelCount(labels.size());
                ValueFormatter formatter=new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return getDay(labels.get((int )value));
                    }
                };

                axis.setGranularity(1f);
                axis.setValueFormatter(formatter);
                userStatsView.setDrawGridBackground(false);
                userStatsView.invalidate();


                //orders in the last 5 days chat
                List<BarEntry> ordersEntry=new ArrayList<>();
                List<String> labels1=new ArrayList<>();
                int index1=0;
                for(Map.Entry<String,Integer>maps:dayCategoryMap.entrySet()){
                    ordersEntry.add(new BarEntry(index1,maps.getValue()));
                    labels1.add(maps.getKey());
                    Timber.e("%s Hello %s", maps.getKey(), maps.getValue());
                    index1++;
                }

                BarDataSet barDataSet1=new BarDataSet(ordersEntry,"Orders");
                BarData data1=new BarData(barDataSet1);
                data1.setBarWidth(0.4f);

                orderStatsView.setData(data1);
                barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
                orderStatsView.animateY(5000);
                XAxis axis1=orderStatsView.getXAxis();
                axis1.setPosition(XAxis.XAxisPosition.BOTTOM);
                axis1.setLabelCount(labels1.size());
                ValueFormatter formatter1=new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return getDay(labels1.get((int )value));
                    }
                };

                axis1.setGranularity(1f);
                axis1.setValueFormatter(formatter1);
                orderStatsView.setDrawGridBackground(false);
                orderStatsView.invalidate();


                //Most ordered categories
               // mostOrderedStatsView.setUsePercentValues(true);
                mostOrderedStatsView.setDrawHoleEnabled(true);

                List<PieEntry> mostOrderedEntry=new ArrayList<>();
                for(Map.Entry<String,Integer>maps:usersMap.entrySet()){
                    mostOrderedEntry.add(new PieEntry(maps.getValue(),getDay(maps.getKey())));

                }

                PieDataSet pieDataSet=new PieDataSet(mostOrderedEntry,"Categories");
                PieData pieData=new PieData(pieDataSet);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                //pieData.setValueFormatter(new PercentFormatter());

                mostOrderedStatsView.setData(pieData);
                mostOrderedStatsView.getDescription().setText("");
                mostOrderedStatsView.setHoleRadius(20);
                mostOrderedStatsView.invalidate();

            }
        });
    }

    public String getDay(String date){
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date1=format.parse(date);
            format.applyPattern("EEE");
            assert date1 != null;
            return format.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      drawerToggle.syncState();
      setUpNavigationView();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add_offers) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_layout, new AddOffersFragment()).commit();
                transaction.addToBackStack(null);
                navigationDrawer.closeDrawers();
                return true;
            } else if (itemId == R.id.nav_delete_offer) {
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.drawer_layout, new DeleteOffersFragment()).commit();
                transaction1.addToBackStack(null);
                navigationDrawer.closeDrawers();
                return true;
            } else if (itemId == R.id.nav_add_drinks) {
                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.drawer_layout, new AddDrinksFragment()).commit();
                transaction2.addToBackStack(null);
                navigationDrawer.closeDrawers();
                return true;
            } else if (itemId == R.id.nav_remove_drinks) {
                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                transaction3.replace(R.id.drawer_layout, new DeleteDrinksFragment()).commit();
                transaction3.addToBackStack(null);
                navigationDrawer.closeDrawers();
                return true;
            }
            else if (itemId == R.id.nav_messages) {
                FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                transaction4.replace(R.id.drawer_layout, new MessagesFragment()).commit();
                transaction4.addToBackStack(null);
                navigationDrawer.closeDrawers();
                return true;
            }else if (itemId == R.id.cashbacks) {
                Dialog pDialog=new Dialog(MainAdminActivity.this);
                pDialog.setContentView(R.layout.progress_dialog_transparent);
                pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pDialog.setCancelable(false);
                pDialog.show();
                new FetchUtils().fetchCashBacks(new FetchCallBacks() {
                    @Override
                    public void onError(String message) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"An Error Occured",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCashBacks(List<CashBack> cashBackList) {
                        pDialog.dismiss();
                        AdminCashBacksDialog.showDialog(getSupportFragmentManager(),cashBackList);
                    }
                });
                return true;
            }
            return false;
        });
         }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
       return super.onSupportNavigateUp();
    }
}