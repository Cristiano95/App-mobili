package com.teocri.recycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//Questa classe contiene solamente il costruttore che serve nel SectionsPagerAdapter.
public class Fragment_cities extends Fragment {
    public static Fragment_cities newInstance() {
        return new Fragment_cities();
    }
    //Creazione del fragment per la gestione delle città

    @Nullable
    @Override                                   //Chiamato per fare in modo che il fragment istanzi la vista dell'interfaccia utente.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cities, container, false);
    }                                           //Collegando il relativo file .xml -> (layout/fragment_cities.cml)
}
