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
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.benway0.mundo.R;
import com.github.benway0.mundo.fragments.FeedFragment;
import com.github.benway0.mundo.tasks.NewsSyncTask;
import com.github.benway0.mundo.utilities.DateUtils;
import com.github.benway0.mundo.utilities.NetworkUtils;
import com.github.benway0.mundo.utilities.PreferenceUtils;
import com.squareup.picasso.Picasso;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Cursor mCursor;

    private Context mContext;

    private final NewsAdapterOnClickHandler mClickHandler;

    public interface NewsAdapterOnClickHandler {
        void onClick(String title, String link, String description, String enclosure,
                     String mediaContent, String provider, String date, String articleText);
    }

    public NewsAdapter(Context context, NewsAdapterOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        /* Set the title, provider and date information from the Cursor */
        String title = mCursor.getString(FeedFragment.INDEX_TITLE).replace("&apos;", "'");
        holder.headline.setText(title.trim());
        holder.provider.setText(mCursor.getString(FeedFragment.INDEX_PROVIDER));
        holder.date.setText(DateUtils.getDateString(mCursor.getLong(FeedFragment.INDEX_PUBDATE),
                mContext));

        /* Default icon */
        int iconID = R.drawable.ic_rt;

        /* Check the image preference */
        boolean imagesOn = PreferenceUtils.imagesOn(mContext);
        boolean imagesWifi = PreferenceUtils.imagesWifi(mContext);
        boolean wifiConnected = PreferenceUtils.isWifiConnected(mContext);

        /* Set the icon and thumbnail based on image preference and Cursor information.
         * Some providers do not provide images. The ones that do are contained either in the
         * enclosure tag, the media:content tag, or (rarely) the description tag.
        */
        switch (mCursor.getString(FeedFragment.INDEX_PROVIDER)) {
            case NewsSyncTask.PROVIDER_ABC:
                iconID = R.drawable.ic_abc;
                insertLogo(R.drawable.logo_abc, holder);
                break;
            case NewsSyncTask.PROVIDER_ALJAZEERA:
                iconID = R.drawable.ic_aljazeera;
                insertLogo(R.drawable.logo_aljazeera, holder);
                break;
            case NewsSyncTask.PROVIDER_ANGOP:
                iconID = R.drawable.ic_angop;
                insertLogo(R.drawable.logo_angop, holder);
                break;
            case NewsSyncTask.PROVIDER_ARABNEWS:
                iconID = R.drawable.ic_arabnews;
                insertLogo(R.drawable.logo_arabnews, holder);
                break;
            case NewsSyncTask.PROVIDER_BBC:
                iconID = R.drawable.ic_bbc;
                insertLogo(R.drawable.logo_bbc, holder);
                break;
            case NewsSyncTask.PROVIDER_BIANET:
                iconID = R.drawable.ic_bianet;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_bianet, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_BULAWAYO:
                iconID = R.drawable.ic_bulawayo;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_bulawayo, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_BREITBART:
                iconID = R.drawable.ic_breitbart;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_breitbart, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_COLOMBIAREPORTS:
                iconID = R.drawable.ic_colombiareports;
                insertLogo(R.drawable.logo_colombiareports, holder);
                break;
            case NewsSyncTask.PROVIDER_COPENHAGENPOST:
                iconID = R.drawable.ic_copenhagenpost;
                insertLogo(R.drawable.logo_copenhagenpost, holder);
                break;
            case NewsSyncTask.PROVIDER_DAILYNATION:
                iconID = R.drawable.ic_dailynation;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromDescription(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_dailynation, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_DAILYSABAH:
                iconID = R.drawable.ic_dailysabah;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_dailysabah, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_FRANCE24:
                iconID = R.drawable.ic_france24;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_france24, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_GLOBALNEWS:
                iconID = R.drawable.ic_globalnews;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_globalnews, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_HINDUSTANTIMES:
                iconID = R.drawable.ic_hindustantimes;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_hindustantimes, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_HURRIYET:
                iconID = R.drawable.ic_hurriyet;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_hurriyet, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_INDEPENDENT:
                iconID = R.drawable.ic_independent;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_independent, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_INQUIRER:
                iconID = R.drawable.ic_inquirer;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_inquirer, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_IRISHTIMES:
                iconID = R.drawable.ic_irishtimes;
                insertLogo(R.drawable.logo_irishtimes, holder);
                break;
            case NewsSyncTask.PROVIDER_JAPANTODAY:
                iconID = R.drawable.ic_japantoday;
                insertLogo(R.drawable.logo_japantoday, holder);
                break;
            case NewsSyncTask.PROVIDER_JERUSALEMPOST:
                iconID = R.drawable.ic_jerusalempost;
                insertLogo(R.drawable.logo_jerusalempost, holder);
                break;
            case NewsSyncTask.PROVIDER_KHALEEJTIMES:
                iconID = R.drawable.ic_khaleejtimes;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_khaleejtimes, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_KOREAHERALD:
                iconID = R.drawable.ic_koreaherald;
                insertLogo(R.drawable.logo_koreaherald, holder);
                break;
            case NewsSyncTask.PROVIDER_MERCOPRESS:
                iconID = R.drawable.ic_mercopress;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_mercopress, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_NATIONALACCORD:
                iconID = R.drawable.ic_nationalaccord;
                insertLogo(R.drawable.logo_nationalaccord, holder);
                break;
            case NewsSyncTask.PROVIDER_NEWS24:
                iconID = R.drawable.ic_news24;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_news24, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_NYPOST:
                iconID = R.drawable.ic_nypost;
                insertLogo(R.drawable.logo_nypost, holder);
                break;
            case NewsSyncTask.PROVIDER_NYTIMES:
                iconID = R.drawable.ic_nytimes;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_nytimes, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_NZHERALD:
                iconID = R.drawable.ic_nzherald;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_nzherald, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_PEOPLESDAILY:
                iconID = R.drawable.ic_peoplesdaily;
                insertLogo(R.drawable.logo_peoplesdaily, holder);
                break;
            case NewsSyncTask.PROVIDER_PRAVDA:
                iconID = R.drawable.ic_pravda;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_pravda, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_PRESSTV:
                iconID = R.drawable.ic_presstv;
                insertLogo(R.drawable.logo_presstv, holder);
                break;
            case NewsSyncTask.PROVIDER_RADIOPOLAND:
                iconID = R.drawable.ic_radiopoland;
                insertLogo(R.drawable.logo_radiopoland, holder);
                break;
            case NewsSyncTask.PROVIDER_REUTERS:
                iconID = R.drawable.ic_reuters;
                insertLogo(R.drawable.logo_reuters, holder);
                break;
            case NewsSyncTask.PROVIDER_RT:
                iconID = R.drawable.ic_rt;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_rt, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_RTE:
                iconID = R.drawable.ic_rte;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_rte, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_SANA:
                iconID = R.drawable.ic_sana;
                insertLogo(R.drawable.logo_sana, holder);
                break;
            case NewsSyncTask.PROVIDER_SHANGHAIDAILY:
                iconID = R.drawable.ic_shanghaidaily;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_shanghaidaily, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_SKYNEWS:
                iconID = R.drawable.ic_skynews;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_skynews, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_SPIEGEL:
                iconID = R.drawable.ic_spiegel;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_spiegel, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_SPUTNIK:
                iconID = R.drawable.ic_sputnik;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_sputnik, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_STRAITSTIMES:
                iconID = R.drawable.ic_straitstimes;
                insertLogo(R.drawable.logo_straitstimes, holder);
                break;
            case NewsSyncTask.PROVIDER_TEHRANTIMES:
                iconID = R.drawable.ic_tehrantimes;
                insertLogo(R.drawable.logo_tehrantimes, holder);
                break;
            case NewsSyncTask.PROVIDER_TELEGRAPH:
                iconID = R.drawable.ic_telegraph;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_telegraph, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_THEGUARDIAN:
                iconID = R.drawable.ic_theguardian;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromMediaContent(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_theguardian, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_TIMESOFISRAEL:
                iconID = R.drawable.ic_timesofisrael;
                insertLogo(R.drawable.logo_timesofisrael, holder);
                break;
            case NewsSyncTask.PROVIDER_UNIAN:
                iconID = R.drawable.ic_unian;
                if ((imagesOn || (imagesWifi && wifiConnected)) &&
                        insertImageFromEnclosure(holder)) {
                    break;
                } else {
                    insertLogo(R.drawable.logo_unian, holder);
                }
                break;
            case NewsSyncTask.PROVIDER_WASHINGTONTIMES:
                iconID = R.drawable.ic_washingtontimes;
                insertLogo(R.drawable.logo_washingtontimes, holder);
                break;
            default:
                break;
        }
        Picasso.with(mContext).load(iconID).into(holder.icon);
    }

    /**
     * Insert the saved default logo based on the provider.
     *
     * @param id the resource file for the relevant logo
     * @param holder the relevant ViewHolder
     */
    private void insertLogo(int id, NewsViewHolder holder) {
        Picasso.with(mContext).load(id).fit().into(holder.imageView);
    }

    /**
     * Insert the image from the enclosure field in the Cursor. If there is a problem in the XML
     * and it returns null, we simply use the default thumbnail.
     *
     * @param holder the relevant ViewHolder
     * @return whether or not the image was successfully found and applied
     */
    private boolean insertImageFromEnclosure(NewsViewHolder holder) {
        if (mCursor.getString(FeedFragment.INDEX_ENCLOSURE) != null) {
            Picasso.with(mContext).load(mCursor.getString(FeedFragment.INDEX_ENCLOSURE)).fit()
                    .into(holder.imageView);
            return true;
        }
        return false;
    }

    /**
     * Insert the image from the description field in the Cursor. If there is a problem in the XML
     * and it returns null, we simply use the default thumbnail.
     *
     * @param holder the relevant ViewHolder
     * @return whether or not the image was successfully found and applied
     */
    private boolean insertImageFromDescription(NewsViewHolder holder) {
        if (mCursor.getString(FeedFragment.INDEX_DESCRIPTION) != null) {
            String image = NetworkUtils.getImageFromDescription(
                    mCursor.getString(FeedFragment.INDEX_DESCRIPTION));
            Picasso.with(mContext).load(image).fit().into(holder.imageView);
            return true;
        }
        return false;
    }

    /**
     * Insert the image from the media:content field in the Cursor. If there is a problem in the XML
     * and it returns null, we simply use the default thumbnail.
     *
     * @param holder the relevant ViewHolder
     * @return whether or not the image was successfully found and applied
     */
    private boolean insertImageFromMediaContent(NewsViewHolder holder) {
        if (mCursor.getString(FeedFragment.INDEX_MEDIACONTENT) != null &&
                !TextUtils.isEmpty(mCursor.getString(FeedFragment.INDEX_MEDIACONTENT))) {
            Picasso.with(mContext).load(mCursor.getString(FeedFragment.INDEX_MEDIACONTENT)).fit()
                    .into(holder.imageView);
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;

        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Inner class holding the BookmarksViewHolder for the Adapter
     */
    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView headline;
        TextView provider;
        TextView date;
        ImageView imageView;
        ImageView icon;

        public NewsViewHolder(View itemView) {
            super(itemView);

            headline = (TextView) itemView.findViewById(R.id.news_headline);
            provider = (TextView) itemView.findViewById(R.id.news_provider);
            date = (TextView) itemView.findViewById(R.id.news_date);
            imageView = (ImageView) itemView.findViewById(R.id.news_thumbnail_card);
            icon = (ImageView) itemView.findViewById(R.id.news_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            String title = mCursor.getString(FeedFragment.INDEX_TITLE);
            String link = mCursor.getString(FeedFragment.INDEX_LINK);
            String description = mCursor.getString(FeedFragment.INDEX_DESCRIPTION);
            String enclosure = mCursor.getString(FeedFragment.INDEX_ENCLOSURE);
            String mediaContent = mCursor.getString(FeedFragment.INDEX_MEDIACONTENT);
            String provider = mCursor.getString(FeedFragment.INDEX_PROVIDER);
            String date =
                    DateUtils.convertDateFromMillis(mCursor.getLong(FeedFragment.INDEX_PUBDATE));
            String articleText = "";

            mClickHandler.onClick(title, link, description, enclosure, mediaContent, provider, date,
                    articleText);
        }
    }
}
