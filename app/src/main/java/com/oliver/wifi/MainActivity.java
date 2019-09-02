package com.oliver.wifi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.oliver.sdk.base.WifiFragment;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private WifiFragment mWifiFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWifiFragment = WifiFragment.newInstance();

        showFragment(mWifiFragment);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment, fragment.getClass().getSimpleName()).commit();
    }

}
