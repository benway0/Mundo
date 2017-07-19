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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
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
import com.github.benway0.mundo.tasks.NewsSyncUtils;
import com.github.benway0.mundo.utilities.PreferenceUtils;
import com.github.benway0.mundo.views.MarginDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>, NewsAdapter.NewsAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;

    /* Text to display when there are no results */
    private TextView mNothingToLoad;
    private TextView mAddMoreSources;

    public static final String[] NEWS_PROJECTION = {
            DatabaseContract.NewsEntry.COLUMN_TITLE,
            DatabaseContract.NewsEntry.COLUMN_LINK,
            DatabaseContract.NewsEntry.COLUMN_DESCRIPTION,
            DatabaseContract.NewsEntry.COLUMN_ENCLOSURE,
            DatabaseContract.NewsEntry.COLUMN_MEDIACONTENT,
            DatabaseContract.NewsEntry.COLUMN_PUBDATE,
            DatabaseContract.NewsEntry.COLUMN_PROVIDER,
            DatabaseContract.NewsEntry.COLUMN_COUNTRY
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_LINK = 1;
    public static final int INDEX_DESCRIPTION = 2;
    public static final int INDEX_ENCLOSURE = 3;
    public static final int INDEX_MEDIACONTENT = 4;
    public static final int INDEX_PUBDATE = 5;
    public static final int INDEX_PROVIDER = 6;
    public static final int INDEX_COUNTRY = 7;
    public static final int INDEX_ARTICLETEXT = 8;

    /* One after 909 */
    private static final int NEWS_LOADER_ID = 910;

    private static boolean PREFERENCES_UPDATED = false;

    /* Store the Uri and a copy of the original Uri for use in the search function */
    private Uri mUri, mUriCopy;

    /* AdMob */
    private final String APP_ID = "ca-app-pub-5718123476814582~8993404952";
    private final String AD_UNIT = "ca-app-pub-5718123476814582/2946871356";
    private final String TEST_AD_UNIT = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd mInterstitialAd;

    public static Fragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        /* Enabled so we can change the menu */
        setHasOptionsMenu(true);

        /* Configure content provider Uris */
        mUri = DatabaseContract.NewsEntry.CONTENT_URI;
        mUriCopy = DatabaseContract.NewsEntry.CONTENT_URI;

        /* Set up SwipeRefreshLayout */
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_feed_fragment);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        /* Set up RecyclerView and Adapter */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.auto_fit_recycler_view_feed_fragment);
        mRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mNewsAdapter = new NewsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mNewsAdapter);

        mNothingToLoad = (TextView) view.findViewById(R.id.feed_fragment_nothingtoload);
        mAddMoreSources = (TextView) view.findViewById(R.id.feed_fragment_addsources);

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

        /* Listen for changes to the settings */
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);

        /* Initialize article data and start the loader */
        getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /* If the preferences have changed, then refresh feed */
        if (PREFERENCES_UPDATED) {
            onRefresh();

            /* Check whether the user has the autosync feature enabled */
            String autoSync = PreferenceUtils.autoSync(getContext());

            /* If autosync is on then schedule a job with the correct settings */
            if (autoSync.equals(getString(R.string.pref_title_automatic_sync_on_value))) {
                NewsSyncUtils.scheduleSync(getContext());
            } else if (autoSync.equals(getString(R.string.pref_title_automatic_sync_wifi_value))) {
                if (PreferenceUtils.isWifiConnected(getContext())) {
                    NewsSyncUtils.scheduleSync(getContext());
                }
            }
            PREFERENCES_UPDATED = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /** Action when the SwipeRefreshLayout is refreshing */
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        NewsSyncUtils.sync(getContext());
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);

        /* Set up the search view in the menu */
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (DatabaseContract.NewsEntry.isSearchUri(mUri)) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefresh();
        }
        return super.onOptionsItemSelected(item);
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
            filter(mUriCopy, false);
        } else {
            filter(DatabaseContract.NewsEntry.SEARCH_URI(newText), true);
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
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_UPDATED = true;
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

    /** Loader functions */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Sort by newest first */
        String sortOrder = DatabaseContract.NewsEntry.COLUMN_PUBDATE + " DESC";

        return new CursorLoader(getContext(),
                mUri,
                NEWS_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() <= 0) {
            mNothingToLoad.setVisibility(View.VISIBLE);
            mAddMoreSources.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNothingToLoad.setVisibility(View.GONE);
            mAddMoreSources.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mNewsAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }
}
