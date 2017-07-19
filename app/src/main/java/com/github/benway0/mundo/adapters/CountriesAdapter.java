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

package com.github.benway0.mundo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.benway0.mundo.R;
import com.github.benway0.mundo.tasks.NewsSyncTask;
import com.github.benway0.mundo.views.CountryItem;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {

    private Context mContext;

    private CountryItem[] mCountries;

    private final CountriesAdapterOnClickHandler mClickHandler;

    public interface CountriesAdapterOnClickHandler {
        void onClick(String country);
    }

    public CountriesAdapter(Context context, CountryItem[] countries,
                            CountriesAdapterOnClickHandler handler) {
        mContext = context;
        mCountries = countries;
        mClickHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_countries, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setImageResource(mCountries[position].getFlag());

        holder.text.setText(mCountries[position].getName());
    }

    @Override
    public int getItemCount() {
        return mCountries.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;
        TextView text;

        public ViewHolder(View view) {
            super(view);

            icon = (ImageView) view.findViewById(R.id.country_icon);
            text = (TextView) view.findViewById(R.id.country_text);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String textString = text.getText().toString();
            String country = "";

            /* Set the country code so we know which results to display from the database. */
            if (textString.equals(mContext.getString(R.string.country_angola)))
                country = NewsSyncTask.CODE_ANGOLA;
            else if (textString.equals(mContext.getString(R.string.country_australia)))
                country = NewsSyncTask.CODE_AUSTRALIA;
            else if (textString.equals(mContext.getString(R.string.country_canada)))
                country = NewsSyncTask.CODE_CANADA;
            else if (textString.equals(mContext.getString(R.string.country_china)))
                country = NewsSyncTask.CODE_CHINA;
            else if (textString.equals(mContext.getString(R.string.country_colombia)))
                country = NewsSyncTask.CODE_COLOMBIA;
            else if (textString.equals(mContext.getString(R.string.country_denmark)))
                country = NewsSyncTask.CODE_DENMARK;
            else if (textString.equals(mContext.getString(R.string.country_france)))
                country = NewsSyncTask.CODE_FRANCE;
            else if (textString.equals(mContext.getString(R.string.country_germany)))
                country = NewsSyncTask.CODE_GERMANY;
            else if (textString.equals(mContext.getString(R.string.country_india)))
                country = NewsSyncTask.CODE_INDIA;
            else if (textString.equals(mContext.getString(R.string.country_iran)))
                country = NewsSyncTask.CODE_IRAN;
            else if (textString.equals(mContext.getString(R.string.country_ireland)))
                country = NewsSyncTask.CODE_IRELAND;
            else if (textString.equals(mContext.getString(R.string.country_israel)))
                country = NewsSyncTask.CODE_ISRAEL;
            else if (textString.equals(mContext.getString(R.string.country_japan)))
                country = NewsSyncTask.CODE_JAPAN;
            else if (textString.equals(mContext.getString(R.string.country_kenya)))
                country = NewsSyncTask.CODE_KENYA;
            else if (textString.equals(mContext.getString(R.string.country_newzealand)))
                country = NewsSyncTask.CODE_NEWZEALAND;
            else if (textString.equals(mContext.getString(R.string.country_nigeria)))
                country = NewsSyncTask.CODE_NIGERIA;
            else if (textString.equals(mContext.getString(R.string.country_philippines)))
                country = NewsSyncTask.CODE_PHILIPPINES;
            else if (textString.equals(mContext.getString(R.string.country_poland)))
                country = NewsSyncTask.CODE_POLAND;
            else if (textString.equals(mContext.getString(R.string.country_qatar)))
                country = NewsSyncTask.CODE_QATAR;
            else if (textString.equals(mContext.getString(R.string.country_russia)))
                country = NewsSyncTask.CODE_RUSSIA;
            else if (textString.equals(mContext.getString(R.string.country_saudiarabia)))
                country = NewsSyncTask.CODE_SAUDIARABIA;
            else if (textString.equals(mContext.getString(R.string.country_singapore)))
                country = NewsSyncTask.CODE_SINGAPORE;
            else if (textString.equals(mContext.getString(R.string.country_southafrica)))
                country = NewsSyncTask.CODE_SOUTHAFRICA;
            else if (textString.equals(mContext.getString(R.string.country_southkorea)))
                country = NewsSyncTask.CODE_SOUTHKOREA;
            else if (textString.equals(mContext.getString(R.string.country_syria)))
                country = NewsSyncTask.CODE_SYRIA;
            else if (textString.equals(mContext.getString(R.string.country_turkey)))
                country = NewsSyncTask.CODE_TURKEY;
            else if (textString.equals(mContext.getString(R.string.country_uae)))
                country = NewsSyncTask.CODE_UAE;
            else if (textString.equals(mContext.getString(R.string.country_ukraine)))
                country = NewsSyncTask.CODE_UKRAINE;
            else if (textString.equals(mContext.getString(R.string.country_unitedkingdom)))
                country = NewsSyncTask.CODE_UNITEDKINGDOM;
            else if (textString.equals(mContext.getString(R.string.country_uruguay)))
                country = NewsSyncTask.CODE_URUGUAY;
            else if (textString.equals(mContext.getString(R.string.country_usa)))
                country = NewsSyncTask.CODE_USA;
            else if (textString.equals(mContext.getString(R.string.country_zimbabwe)))
                country = NewsSyncTask.CODE_ZIMBABWE;

            mClickHandler.onClick(country);
        }
    }
}
