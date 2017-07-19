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

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.github.benway0.mundo.adapters.BookmarksAdapter;
import com.github.benway0.mundo.data.DatabaseContract;
import com.github.benway0.mundo.utilities.ImageUtils;
import com.github.benway0.mundo.views.MarginDecoration;

import java.io.File;

public class BookmarksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        BookmarksAdapter.BookmarksAdapterOnClickHandler, SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    private RecyclerView mRecyclerView;
    private BookmarksAdapter mAdapter;

    /* Text to display when there are no results */
    private TextView mNothingToLoad;

    private final String[] BOOKMARKS_PROJECTION = {
            DatabaseContract.BookmarksEntry.COLUMN_TITLE,
            DatabaseContract.BookmarksEntry.COLUMN_PROVIDER,
            DatabaseContract.BookmarksEntry.COLUMN_ARTICLETEXT
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_PROVIDER = 1;
    public static final int INDEX_ARTICLETEXT = 2;

    /* 808s and Heartbreak */
    private static final int BOOKMARKS_LOADER_ID = 808;

    /* Store the Uri and a copy of the original Uri for use in the search function */
    private Uri mUri, mUriCopy;

    public static Fragment newInstance() {
        BookmarksFragment fragment = new BookmarksFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        /* So we can change the toolbar */
        setHasOptionsMenu(true);

        mNothingToLoad = (TextView) v.findViewById(R.id.bookmarks_fragment_nothingtoload);

        /* Configure content provider Uris */
        mUri = DatabaseContract.BookmarksEntry.CONTENT_URI;
        mUriCopy = DatabaseContract.BookmarksEntry.CONTENT_URI;

        /* Set up RecyclerView and Adapter */
        mRecyclerView =
                (RecyclerView) v.findViewById(R.id.auto_fit_recycler_view_bookmarks_fragment);
        mRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new BookmarksAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(BOOKMARKS_LOADER_ID, null, this);

        return v;
    }

    @Override
    public void onClick(String title, String provider, String articleText) {
        Intent intent = new Intent(getContext(), ArticleActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("provider", provider);
        intent.putExtra("articleText", articleText);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_bookmarks, menu);

        /* Set up the search view in the menu */
        MenuItem searchItem = menu.findItem(R.id.action_search_bookmarks);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (DatabaseContract.BookmarksEntry.isSearchUri(mUri)) {
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
            case R.id.action_clear_bookmarks:
                new DeleteBookmarksTask().execute();
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

            /* If there's nothing in the search bar, show the original results */
            filter(mUriCopy, false);
        } else {

            /* Otherwise filter the results with the search term */
            filter(DatabaseContract.BookmarksEntry.SEARCH_URI(newText), true);
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
        getLoaderManager().restartLoader(BOOKMARKS_LOADER_ID, null, this);
    }

    /** Loader functions */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* Sort so that the most recently added bookmark appears first */
        String sortOrder = DatabaseContract.BookmarksEntry._ID + " DESC";

        return new CursorLoader(getContext(), mUri,
                BOOKMARKS_PROJECTION, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() <= 0) {
            mNothingToLoad.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNothingToLoad.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * AsyncTask to clear all bookmarks
     */
    public class DeleteBookmarksTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.delete(DatabaseContract.BookmarksEntry.CONTENT_URI, null, null);

            /* Delete all the images associated with the bookmarks */
            File dir = new File(getContext().getFilesDir().toString() + ImageUtils.IMAGE_PATH);
            deleteRecursive(dir);

            return null;
        }

        protected void deleteRecursive(File file) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    deleteRecursive(child);
                }
            }
            file.delete();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
