package com.example.stath.crookclient.Activity;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.stath.crookclient.Adapter.ViewPagerAdapter;
import com.example.stath.crookclient.Fragment.FragmentCart;
import com.example.stath.crookclient.Fragment.FragmentOrders;
import com.example.stath.crookclient.Fragment.FragmentProfile;
import com.example.stath.crookclient.R;

public class AccountActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tabLayout = findViewById(R.id.tabLayout);
        appBarLayout = findViewById(R.id.appbar);
        viewPager = findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentCart(), "Cart");
        adapter.AddFragment(new FragmentOrders(), "My Orders");
        adapter.AddFragment(new FragmentProfile(), "Profile");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);


        int intentFragment = getIntent().getExtras().getInt("fragment");

        switch (intentFragment) {
            case 0:
                viewPager.setCurrentItem(0);
                break;
            case 1:
                viewPager.setCurrentItem(1);
                break;
            case 2:
                viewPager.setCurrentItem(2);
                break;
        }

    }
}
