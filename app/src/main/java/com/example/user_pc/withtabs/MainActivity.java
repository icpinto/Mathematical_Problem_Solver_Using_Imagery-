package com.example.user_pc.withtabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements Tab1Fragment.SendMessage, Tab3Fragment.SendMessage2 {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    //implement Tab1Fragment.SendMessage interface method to pass data
    @Override
    public void sendData(String message) {
        String tag = "android:switcher:"+ R.id.viewPager + ":" + 1;
        Tab2Fragment f = (Tab2Fragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (message!=null) {
            f.displayReceivedData(message);
        }
    }
    //implement Tab2Fragment.SendMessage interface method to pass data
    @Override
    public void sendData2(String message) {
        String tag = "android:switcher:"+ R.id.viewPager + ":" + 1;
        Tab2Fragment f = (Tab2Fragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (message!=null) {
            f.displayReceivedData(message);
        }

    }
}