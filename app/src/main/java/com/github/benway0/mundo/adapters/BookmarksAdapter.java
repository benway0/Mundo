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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.benway0.mundo.R;
import com.github.benway0.mundo.fragments.BookmarksFragment;
import com.github.benway0.mundo.tasks.NewsSyncTask;
import com.github.benway0.mundo.utilities.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarksViewHolder> {

    /* Cursor to store Bookmark database results */
    private Cursor mCursor;

    private Context mContext;

    private final BookmarksAdapterOnClickHandler mClickHandler;

    public interface BookmarksAdapterOnClickHandler {
        void onClick(String title, String provider, String articleText);
    }

    public BookmarksAdapter(Context context, BookmarksAdapterOnClickHandler handler) {
        mContext = context;
        mClickHandler = handler;
    }

    @Override
    public BookmarksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new BookmarksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookmarksViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        /* Get the title and provider from the Cursor object */
        String title = mCursor.getString(BookmarksFragment.INDEX_TITLE).replace("&apos;", "'");
        holder.headline.setText(title.trim());
        holder.provider.setText(mCursor.getString(BookmarksFragment.INDEX_PROVIDER));

        /* Find date from the article with regex and set it in the holder */
        Pattern datePattern = Pattern.compile("\\d{1,2}\\s\\w+,\\s\\d{2}:\\d{2}",
                Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(mCursor.getString(
                BookmarksFragment.INDEX_ARTICLETEXT));
        List<String> matches = new ArrayList<>();
        while (dateMatcher.find()) {
            matches.add(dateMatcher.group(0));
        }
        String dateArray[] = matches.get(0).split(" ");
        String dateString = dateArray[0] + " " + dateArray[1].substring(0, 3);
        holder.date.setText(dateString);

        /* Set the icon and thumbnail according to the provider rules */
        int iconID = R.drawable.ic_rt;
        switch (mCursor.getString(BookmarksFragment.INDEX_PROVIDER)) {
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
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_bianet, holder);
                break;
            case NewsSyncTask.PROVIDER_BULAWAYO:
                iconID = R.drawable.ic_bulawayo;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_bulawayo, holder);
                break;
            case NewsSyncTask.PROVIDER_BREITBART:
                iconID = R.drawable.ic_breitbart;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_breitbart, holder);
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
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_dailynation, holder);
                break;
            case NewsSyncTask.PROVIDER_DAILYSABAH:
                iconID = R.drawable.ic_dailysabah;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_dailysabah, holder);
                break;
            case NewsSyncTask.PROVIDER_FRANCE24:
                iconID = R.drawable.ic_france24;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_france24, holder);
                break;
            case NewsSyncTask.PROVIDER_GLOBALNEWS:
                iconID = R.drawable.ic_globalnews;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_globalnews, holder);
                break;
            case NewsSyncTask.PROVIDER_HINDUSTANTIMES:
                iconID = R.drawable.ic_hindustantimes;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_hindustantimes, holder);
                break;
            case NewsSyncTask.PROVIDER_HURRIYET:
                iconID = R.drawable.ic_hurriyet;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_hurriyet, holder);
                break;
            case NewsSyncTask.PROVIDER_INDEPENDENT:
                iconID = R.drawable.ic_independent;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_independent, holder);
                break;
            case NewsSyncTask.PROVIDER_INQUIRER:
                iconID = R.drawable.ic_inquirer;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_inquirer, holder);
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
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_khaleejtimes, holder);
                break;
            case NewsSyncTask.PROVIDER_KOREAHERALD:
                iconID = R.drawable.ic_koreaherald;
                insertLogo(R.drawable.logo_koreaherald, holder);
                break;
            case NewsSyncTask.PROVIDER_MERCOPRESS:
                iconID = R.drawable.ic_mercopress;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_mercopress, holder);
                break;
            case NewsSyncTask.PROVIDER_NATIONALACCORD:
                iconID = R.drawable.ic_nationalaccord;
                insertLogo(R.drawable.logo_nationalaccord, holder);
                break;
            case NewsSyncTask.PROVIDER_NEWS24:
                iconID = R.drawable.ic_news24;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_news24, holder);
                break;
            case NewsSyncTask.PROVIDER_NYPOST:
                iconID = R.drawable.ic_nypost;
                insertLogo(R.drawable.logo_nypost, holder);
                break;
            case NewsSyncTask.PROVIDER_NYTIMES:
                iconID = R.drawable.ic_nytimes;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_nytimes, holder);
                break;
            case NewsSyncTask.PROVIDER_NZHERALD:
                iconID = R.drawable.ic_nzherald;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_nzherald, holder);
                break;
            case NewsSyncTask.PROVIDER_PEOPLESDAILY:
                iconID = R.drawable.ic_peoplesdaily;
                insertLogo(R.drawable.logo_peoplesdaily, holder);
                break;
            case NewsSyncTask.PROVIDER_PRAVDA:
                iconID = R.drawable.ic_pravda;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_pravda, holder);
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
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_rt, holder);
                break;
            case NewsSyncTask.PROVIDER_RTE:
                iconID = R.drawable.ic_rte;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_rte, holder);
                break;
            case NewsSyncTask.PROVIDER_SANA:
                iconID = R.drawable.ic_sana;
                insertLogo(R.drawable.logo_sana, holder);
                break;
            case NewsSyncTask.PROVIDER_SHANGHAIDAILY:
                iconID = R.drawable.ic_shanghaidaily;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_shanghaidaily, holder);
                break;
            case NewsSyncTask.PROVIDER_SKYNEWS:
                iconID = R.drawable.ic_skynews;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_skynews, holder);
                break;
            case NewsSyncTask.PROVIDER_SPIEGEL:
                iconID = R.drawable.ic_spiegel;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_spiegel, holder);
                break;
            case NewsSyncTask.PROVIDER_SPUTNIK:
                iconID = R.drawable.ic_sputnik;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_sputnik, holder);
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
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_telegraph, holder);
                break;
            case NewsSyncTask.PROVIDER_THEGUARDIAN:
                iconID = R.drawable.ic_theguardian;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_theguardian, holder);
                break;
            case NewsSyncTask.PROVIDER_TIMESOFISRAEL:
                iconID = R.drawable.ic_timesofisrael;
                insertLogo(R.drawable.logo_timesofisrael, holder);
                break;
            case NewsSyncTask.PROVIDER_UNIAN:
                iconID = R.drawable.ic_unian;
                if (!getThumbnail(holder).exists())
                    insertLogo(R.drawable.logo_unian, holder);
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
     * Search the internal storage for the relevant thumbnail file based on the name it was saved
     * under when the bookmark was added. Returns the relevant file so we know whether it exists
     * and whether to use it for the thumbnail, otherwise use the default logo option.
     *
     * @param holder the relevant ViewHolder
     * @return the saved thumbnail file. If it doesn't exist, we will use the saved logo instead.
     */
    private File getThumbnail(BookmarksViewHolder holder) {
        String fileString = mCursor.getString(BookmarksFragment.INDEX_TITLE);

        /* Get rid of any character that isn't a letter or a digit */
        Pattern banned = Pattern.compile("([^A-Za-z0-9])", Pattern.CASE_INSENSITIVE);
        fileString = banned.matcher(fileString).replaceAll("");

        /* Set the name of the file based on the length of the title */
        if (fileString.length() > 30)
            fileString = fileString.toLowerCase().substring(0, 30);
        else
            fileString = fileString.toLowerCase().substring(0, fileString.length()-1);

        /* Load the file into the ImageView with Picasso */
        String path = mContext.getFilesDir().toString();
        File file = new File(path + ImageUtils.IMAGE_PATH + "/" + fileString + ".jpg");
        Picasso.with(mContext).load(file).fit().into(holder.imageView);

        return file;
    }

    /**
     * Insert the saved default logo based on the provider.
     *
     * @param id the resource file for the relevant logo
     * @param holder the relevant ViewHolder
     */
    private void insertLogo(int id, BookmarksViewHolder holder) {
        Picasso.with(mContext).load(id).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Inner class for the ViewHolder
     */
    public class BookmarksViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView headline;
        TextView provider;
        TextView date;
        ImageView imageView;
        ImageView icon;

        public BookmarksViewHolder(View itemView) {
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
            String title = mCursor.getString(BookmarksFragment.INDEX_TITLE);
            String provider = mCursor.getString(BookmarksFragment.INDEX_PROVIDER);
            String articleText = mCursor.getString(BookmarksFragment.INDEX_ARTICLETEXT);

            mClickHandler.onClick(title, provider, articleText);
        }
    }
}
