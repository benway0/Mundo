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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.benway0.mundo.ArticleActivity;
import com.github.benway0.mundo.R;
import com.github.benway0.mundo.adapters.NewsAdapter;
import com.github.benway0.mundo.data.DatabaseContract;
import com.github.benway0.mundo.tasks.CountriesSyncUtils;
import com.github.benway0.mundo.tasks.NewsSyncTask;
import com.github.benway0.mundo.views.MarginDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class CountryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>, NewsAdapter.NewsAdapterOnClickHandler,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;

    /* Text to display when there are no results */
    private TextView mNoLoadTextView;

    public static final String[] COUNTRIES_PROJECTION = {
            DatabaseContract.CountriesEntry.COLUMN_TITLE,
            DatabaseContract.CountriesEntry.COLUMN_LINK,
            DatabaseContract.CountriesEntry.COLUMN_DESCRIPTION,
            DatabaseContract.CountriesEntry.COLUMN_ENCLOSURE,
            DatabaseContract.CountriesEntry.COLUMN_MEDIACONTENT,
            DatabaseContract.CountriesEntry.COLUMN_PUBDATE,
            DatabaseContract.CountriesEntry.COLUMN_PROVIDER,
            DatabaseContract.CountriesEntry.COLUMN_COUNTRY
    };

    /* John 3:16 */
    private static final int COUNTRIES_LOADER_ID = 316;

    public static String COUNTRY_CODE;

    private Cursor mCursor;

    /* Store the Uri and a copy of the original Uri for use in the search function */
    private Uri mUri, mUriCopy;

    /* AdMob */
    private final String APP_ID = "ca-app-pub-5718123476814582~8993404952";
    private final String AD_UNIT = "ca-app-pub-5718123476814582/2946871356";
    private final String TEST_AD_UNIT = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd mInterstitialAd;

    public static Fragment newInstance() {
        CountryFragment fragment = new CountryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country, container, false);

        /* Enabled so we can change the menu */
        setHasOptionsMenu(true);

        /* Configure content provider Uris */
        mUri = DatabaseContract.CountriesEntry.CONTENT_URI;
        mUriCopy = DatabaseContract.CountriesEntry.CONTENT_URI;

        /* Set up SwipeRefreshLayout */
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_country_fragment);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        /* Set up RecyclerView and Adapter */
        mRecyclerView =
                (RecyclerView) view.findViewById(R.id.auto_fit_recycler_view_country_fragment);
        mRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new NewsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        mNoLoadTextView = (TextView) view.findViewById(R.id.country_fragment_nothingtoload);

        /* Set the country code */
        COUNTRY_CODE = getArguments().getString("country");

        /* Set the title */
        setFragmentTitle();

        /* Initial refresh */
        onRefresh();
        getLoaderManager().restartLoader(COUNTRIES_LOADER_ID, null, this);

        /* Set up the ads to show after reading articles */
        MobileAds.initialize(getContext(), APP_ID);
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(AD_UNIT);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        return view;
    }

    /**
     * Set the title of the fragment based on the country currently being shown
     */
    private void setFragmentTitle() {
        String country;

        switch (COUNTRY_CODE) {
            case NewsSyncTask.CODE_ANGOLA:
                country = getString(R.string.country_angola);
                break;
            case NewsSyncTask.CODE_AUSTRALIA:
                country = getString(R.string.country_australia);
                break;
            case NewsSyncTask.CODE_CANADA:
                country = getString(R.string.country_canada);
                break;
            case NewsSyncTask.CODE_CHINA:
                country = getString(R.string.country_china);
                break;
            case NewsSyncTask.CODE_COLOMBIA:
                country = getString(R.string.country_colombia);
                break;
            case NewsSyncTask.CODE_DENMARK:
                country = getString(R.string.country_denmark);
                break;
            case NewsSyncTask.CODE_FRANCE:
                country = getString(R.string.country_france);
                break;
            case NewsSyncTask.CODE_GERMANY:
                country = getString(R.string.country_germany);
                break;
            case NewsSyncTask.CODE_INDIA:
                country = getString(R.string.country_india);
                break;
            case NewsSyncTask.CODE_IRAN:
                country = getString(R.string.country_iran);
                break;
            case NewsSyncTask.CODE_IRELAND:
                country = getString(R.string.country_ireland);
                break;
            case NewsSyncTask.CODE_ISRAEL:
                country = getString(R.string.country_israel);
                break;
            case NewsSyncTask.CODE_JAPAN:
                country = getString(R.string.country_japan);
                break;
            case NewsSyncTask.CODE_KENYA:
                country = getString(R.string.country_kenya);
                break;
            case NewsSyncTask.CODE_NEWZEALAND:
                country = getString(R.string.country_newzealand);
                break;
            case NewsSyncTask.CODE_NIGERIA:
                country = getString(R.string.country_nigeria);
                break;
            case NewsSyncTask.CODE_PHILIPPINES:
                country = getString(R.string.country_philippines);
                break;
            case NewsSyncTask.CODE_POLAND:
                country = getString(R.string.country_poland);
                break;
            case NewsSyncTask.CODE_QATAR:
                country = getString(R.string.country_qatar);
                break;
            case NewsSyncTask.CODE_RUSSIA:
                country = getString(R.string.country_russia);
                break;
            case NewsSyncTask.CODE_SAUDIARABIA:
                country = getString(R.string.country_saudiarabia);
                break;
            case NewsSyncTask.CODE_SINGAPORE:
                country = getString(R.string.country_singapore);
                break;
            case NewsSyncTask.CODE_SOUTHAFRICA:
                country = getString(R.string.country_southafrica);
                break;
            case NewsSyncTask.CODE_SOUTHKOREA:
                country = getString(R.string.country_southkorea);
                break;
            case NewsSyncTask.CODE_SYRIA:
                country = getString(R.string.country_syria);
                break;
            case NewsSyncTask.CODE_TURKEY:
                country = getString(R.string.country_turkey);
                break;
            case NewsSyncTask.CODE_UAE:
                country = getString(R.string.country_uae);
                break;
            case NewsSyncTask.CODE_UKRAINE:
                country = getString(R.string.country_ukraine);
                break;
            case NewsSyncTask.CODE_UNITEDKINGDOM:
                country = getString(R.string.country_unitedkingdom);
                break;
            case NewsSyncTask.CODE_URUGUAY:
                country = getString(R.string.country_uruguay);
                break;
            case NewsSyncTask.CODE_USA:
                country = getString(R.string.country_usa);
                break;
            case NewsSyncTask.CODE_ZIMBABWE:
                country = getString(R.string.country_zimbabwe);
                break;
            default:
                country = "";
                break;
        }
        getActivity().setTitle(country + " " + getString(R.string.country_news));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);

        /* Set up the search view in the menu */
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (DatabaseContract.CountriesEntry.isSearchUri(mUri)) {
            searchItem.expandActionView();
            searchView.post(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery(mUri.getLastPathSegment(), false);
                    searchView.clearFocus();
                }
            });
        }
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /** Search view submit function (not utilised) */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * What to do when the text is changed in the search view
     *
     * @param newText the currently entered search term
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {

            /* If there's nothing in the search bar, show the original results */
            filter(mUriCopy, false);
        } else {

            /* Otherwise filter the results with the search term */
            filter(DatabaseContract.CountriesEntry.SEARCH_URI(newText), true);
        }
        return false;
    }

    /** What to do when the search view is closed */
    @Override
    public boolean onClose() {
        filter(mUriCopy, false);
        return false;
    }

    /**
     * Filter the results based on the text entered into the search view
     *
     * @param uri which loader uri should be filtered
     * @param isSearchUri find out whether the uri is the search uri
     */
    public void filter(Uri uri, boolean isSearchUri) {
        mUri = uri;
        if (!isSearchUri) {
            mUriCopy = mUri;
        }
        getLoaderManager().restartLoader(COUNTRIES_LOADER_ID, null, this);
    }

    /** Action when the SwipeRefreshLayout is refreshing */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        CountriesSyncUtils.sync(getContext());
        getLoaderManager().restartLoader(COUNTRIES_LOADER_ID, null, this);
    }

    @Override
    public void onClick(String title, String link, String description, String enclosure,
                        String mediaContent, String provider, String date, String articleText) {

        Intent intent = new Intent(getContext(), ArticleActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("description", description);
        intent.putExtra("enclosure", enclosure);
        intent.putExtra("mediaContent", mediaContent);
        intent.putExtra("provider", provider);
        intent.putExtra("date", date);
        intent.putExtra("articleText", articleText);

        /* Show the ad if it has been initialised */
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    /** Loader functions */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Find only articles relevant for the current country and sort by newest first */
        String selection = DatabaseContract.CountriesEntry.COLUMN_COUNTRY + "=?";
        String[] selectionArgs = { getArguments().getString("country") };
        String sortOrder = DatabaseContract.CountriesEntry.COLUMN_PUBDATE + " DESC";

        return new CursorLoader(getContext(),
                mUri,
                COUNTRIES_PROJECTION,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        if (data == null || data.getCount() <= 0) {
            mRecyclerView.setVisibility(View.GONE);
            mNoLoadTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoLoadTextView.setVisibility(View.GONE);
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
