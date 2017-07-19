/**
 * Mundo
 * Copyright (c) 2017 Leviathan Software <http://www.leviathansoftware.net/>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 */

package com.github.benway0.mundo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.benway0.mundo.CountryActivity;
import com.github.benway0.mundo.R;
import com.github.benway0.mundo.adapters.CountriesAdapter;
import com.github.benway0.mundo.views.CountryItem;
import com.github.benway0.mundo.views.MarginDecoration;

public class CountriesFragment extends Fragment
        implements CountriesAdapter.CountriesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private CountriesAdapter mAdapter;

    public static Fragment newInstance() {
        CountriesFragment fragment = new CountriesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_countries, container, false);

        /* Enabled so we can change the menu */
        setHasOptionsMenu(true);

        /* Initialise array containing all the country flags and names */
        CountryItem[] countries = {
                new CountryItem(R.drawable.flag_ao, getString(R.string.country_angola)),
                new CountryItem(R.drawable.flag_au, getString(R.string.country_australia)),
                new CountryItem(R.drawable.flag_ca, getString(R.string.country_canada)),
                new CountryItem(R.drawable.flag_cn, getString(R.string.country_china)),
                new CountryItem(R.drawable.flag_co, getString(R.string.country_colombia)),
                new CountryItem(R.drawable.flag_dk, getString(R.string.country_denmark)),
                new CountryItem(R.drawable.flag_fr, getString(R.string.country_france)),
                new CountryItem(R.drawable.flag_de, getString(R.string.country_germany)),
                new CountryItem(R.drawable.flag_in, getString(R.string.country_india)),
                new CountryItem(R.drawable.flag_ir, getString(R.string.country_iran)),
                new CountryItem(R.drawable.flag_ie, getString(R.string.country_ireland)),
                new CountryItem(R.drawable.flag_il, getString(R.string.country_israel)),
                new CountryItem(R.drawable.flag_jp, getString(R.string.country_japan)),
                new CountryItem(R.drawable.flag_ke, getString(R.string.country_kenya)),
                new CountryItem(R.drawable.flag_nz, getString(R.string.country_newzealand)),
                new CountryItem(R.drawable.flag_ng, getString(R.string.country_nigeria)),
                new CountryItem(R.drawable.flag_ph, getString(R.string.country_philippines)),
                new CountryItem(R.drawable.flag_pl, getString(R.string.country_poland)),
                new CountryItem(R.drawable.flag_qa, getString(R.string.country_qatar)),
                new CountryItem(R.drawable.flag_ru, getString(R.string.country_russia)),
                new CountryItem(R.drawable.flag_sa, getString(R.string.country_saudiarabia)),
                new CountryItem(R.drawable.flag_sg, getString(R.string.country_singapore)),
                new CountryItem(R.drawable.flag_za, getString(R.string.country_southafrica)),
                new CountryItem(R.drawable.flag_kr, getString(R.string.country_southkorea)),
                new CountryItem(R.drawable.flag_sy, getString(R.string.country_syria)),
                new CountryItem(R.drawable.flag_tr, getString(R.string.country_turkey)),
                new CountryItem(R.drawable.flag_ae, getString(R.string.country_uae)),
                new CountryItem(R.drawable.flag_ua, getString(R.string.country_ukraine)),
                new CountryItem(R.drawable.flag_uk, getString(R.string.country_unitedkingdom)),
                new CountryItem(R.drawable.flag_uy, getString(R.string.country_uruguay)),
                new CountryItem(R.drawable.flag_us, getString(R.string.country_usa)),
                new CountryItem(R.drawable.flag_zw, getString(R.string.country_zimbabwe))
        };

        /* Set up the RecyclerView */
        mRecyclerView = (RecyclerView) v.findViewById(R.id.countries_recycler_view);
        mRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CountriesAdapter(getContext(), countries, this);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onClick(String country) {
        Intent intent = new Intent(getContext(), CountryActivity.class);
        intent.putExtra("country", country);
        startActivity(intent);
    }
}
