package com.simadesarrollos.smartmonitor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Muestra el mapa
 */
public class FirstMapFragment_old extends SupportMapFragment {

    public FirstMapFragment_old() {
    }

    // TODO: Rename and change types and number of parameters
    public static FirstMapFragment_old newInstance() {
        return new FirstMapFragment_old();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        return root;
    }
}
