package com.example.stath.crookclient.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stath.crookclient.R;

public class FragmentOrders extends Fragment {

    private View view;
    public FragmentOrders(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.orders_fragment, container, false);
        return view;
    }
}
