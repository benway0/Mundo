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
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.benway0.mundo.R;
import com.github.benway0.mundo.data.DatabaseContract;
import com.github.benway0.mundo.tasks.NewsSyncTask;
import com.github.benway0.mundo.utilities.ArticleUtils;
import com.github.benway0.mundo.utilities.ImageUtils;
import com.github.benway0.mundo.utilities.PreferenceUtils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ArticleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private WebView mWebView;
    private String mHeadline;

    private String mTitle;
    private String mDescription;
    private String mEnclosure;
    private String mMediaContent;
    private String mProvider;
    private String mArticleText;
    private String mLink;

    private boolean mIsBookmark;

    public static Fragment newInstance() {
        ArticleFragment fragment = new ArticleFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        /* Set up the SwipeRefreshLayout */
        mSwipeRefreshLayout =
                (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_article_fragment);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        /* Retrieve information from the adapter */
        Bundle args = getArguments();
        mTitle = args.getString("title");
        mDescription = args.getString("description");
        mEnclosure = args.getString("enclosure");
        mMediaContent = args.getString("mediaContent");
        mProvider = args.getString("provider");
        mLink = args.getString("link");

        /* If the article doesn't already have text it must not be a bookmark */
        mIsBookmark = !args.getString("articleText").equals("");

        /* Set the title according to provider */
        getActivity().setTitle(mProvider + " " + getString(R.string.title_article));

        /* Set up the WebView */
        mWebView = (WebView) view.findViewById(R.id.article_web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setDefaultFontSize(18);

        /* Set up the FloatingActionButtons */
        setUpFab(view);

        /* Load the article */
        onRefresh();

        return view;
    }

    /**
     * Set up the floating action buttons for the font size, sharing and bookmark functions.
     *
     * @param view
     */
    private void setUpFab(View view) {

        /* Set up font size changer */
        FloatingActionButton actionFontSize =
                (FloatingActionButton) view.findViewById(R.id.floating_action_font_size);

        actionFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View seekView = inflater.inflate(R.layout.view_seekbar, null);

                /* Set up the seek bar for changing the font size */
                AppCompatSeekBar seekBar =
                        (AppCompatSeekBar) seekView.findViewById(R.id.dialog_seekbar);
                final TextView seekText = (TextView) seekView.findViewById(R.id.dialog_seektext);

                /* Set initial size in accordance with the default WebView font size */
                seekBar.setProgress(18);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekText.setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                /* Show the dialog for changing the font size */
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(R.string.font_dialog_title)
                        .setView(seekView)
                        .setPositiveButton(R.string.font_dialog_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        /* Set WebView font size to seek bar selection */
                                        String fontString = seekText.getText().toString();
                                        int fontSize = Integer.parseInt(fontString);
                                        mWebView.getSettings().setDefaultFontSize(fontSize);
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });

        /* Set up share button */
        FloatingActionButton actionShare =
                (FloatingActionButton) view.findViewById(R.id.floating_action_share);
        actionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Send the article link to the share intent */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mLink);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
            }
        });

        /* Set up bookmark button */
        final FloatingActionButton actionBookmark =
                (FloatingActionButton) view.findViewById(R.id.floating_action_bookmark);
        if (mIsBookmark) actionBookmark.setIcon(R.drawable.ic_action_star_10);
        actionBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsBookmark) {
                    new BookmarkTask().execute();
                    actionBookmark.setIcon(R.drawable.ic_action_star_10);
                    mIsBookmark = true;
                } else {
                    new DeleteBookmarkTask().execute();
                    actionBookmark.setIcon(R.drawable.ic_action_star_0);
                    mIsBookmark = false;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        if (!mIsBookmark) {

            /* If not a bookmark, apply the CSS, headline and date and get article text from web */
            mHeadline = ArticleUtils.CSS + "<h2>" + getArguments().getString("title") + "</h2>" +
                    "<p class=\"date\">" + getArguments().getString("date") + "</p>";
            new ArticleTask().execute(getArguments().getString("link"));
        } else {

            /* If it's a bookmark then load the previously saved article text */
            mArticleText = getArguments().getString("articleText");
            mWebView.loadDataWithBaseURL(null, mArticleText, "text/html", null,
                    null);
            mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * AsyncTask for retrieving the article text from the link.
     */
    public class ArticleTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) return null;

            String result;

            try {

                /* Extract and clean the article text */
                result = ArticleUtils.extract(params[0], getContext());
                result = ArticleUtils.clean(result, mLink);
                return result;
            } catch (Exception ex) {
                ex.getMessage();
            }

            /* Display placeholder text if there's a problem */
            return getString(R.string.problem_loading_article);
        }

        @Override
        protected void onPostExecute(String s) {
            mArticleText = mHeadline + s;
            mWebView.loadData(mArticleText, "text/html; charset=utf-8", "UTF-8");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * AsyncTask for saving a bookmark
     */
    public class BookmarkTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* Check the image preference settings */
            boolean imagesOn = PreferenceUtils.imagesOn(getContext());
            boolean imagesWifi = PreferenceUtils.imagesWifi(getContext());
            boolean wifiConnected = PreferenceUtils.isWifiConnected(getContext());

            /* If images are on, download any images in the article */
            if (imagesOn || (imagesWifi && wifiConnected)) {
                getArticleImages();

                /* Grab the thumbnail based on the method used in the XML */
                switch (mProvider) {
                    case NewsSyncTask.PROVIDER_BIANET:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_BULAWAYO:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_BREITBART:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_DAILYNATION:
                        getThumbnail(mDescription);
                        break;
                    case NewsSyncTask.PROVIDER_DAILYSABAH:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_FRANCE24:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_GLOBALNEWS:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_HINDUSTANTIMES:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_HURRIYET:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_INDEPENDENT:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_INQUIRER:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_KHALEEJTIMES:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_MERCOPRESS:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_NEWS24:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_NYTIMES:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_NZHERALD:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_PEOPLESDAILY:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_RT:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_RTE:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_SHANGHAIDAILY:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_SKYNEWS:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_SPIEGEL:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_SPUTNIK:
                        getThumbnail(mEnclosure);
                        break;
                    case NewsSyncTask.PROVIDER_THEGUARDIAN:
                        getThumbnail(mMediaContent);
                        break;
                    case NewsSyncTask.PROVIDER_UNIAN:
                        getThumbnail(mEnclosure);
                        break;
                    default:
                        break;
                }
            }

            /* Insert the data into the bookmarks database */
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.BookmarksEntry.COLUMN_TITLE, mTitle);
            values.put(DatabaseContract.BookmarksEntry.COLUMN_PROVIDER, mProvider);
            values.put(DatabaseContract.BookmarksEntry.COLUMN_ARTICLETEXT, mArticleText);

            ContentResolver resolver = getContext().getContentResolver();
            resolver.insert(DatabaseContract.BookmarksEntry.CONTENT_URI, values);

            return null;
        }

        /**
         * If the article contains a thumbnail from the XML, create a filename for it and send it
         * to be downloaded.
         *
         * @param method the column in which the thumbnail is stored
         */
        private void getThumbnail(String method) {
            String fileString = mTitle;

            /* Remove any characters which are not letters or digits */
            Pattern banned = Pattern.compile("([^A-Za-z0-9])", Pattern.CASE_INSENSITIVE);
            fileString = banned.matcher(fileString).replaceAll("");

            /* Set the filename based on the length of the title to avoid duplicates */
            if (fileString.length() > 30)
                fileString = fileString.toLowerCase().substring(0, 30);
            else
                fileString = fileString.toLowerCase().substring(0, fileString.length()-1);

            /* Send the image to be downloaded under the chosen filename */
            ImageUtils.downloadThumbnail(method, fileString, getContext());
        }

        /**
         * Download any images included within the article text.
         */
        private void getArticleImages() {
            mArticleText = ImageUtils.getArticleImages(mArticleText, mTitle, getContext());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ArticleFragment.this.getContext(),
                    getString(R.string.toast_bookmark_added), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * AsyncTask for deleting a bookmark
     */
    public class DeleteBookmarkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContentResolver resolver = getContext().getContentResolver();

            /* Find and delete the bookmark where the title matches this article */
            String selection = DatabaseContract.BookmarksEntry.COLUMN_TITLE + "=?";
            String[] selectionArgs = {mTitle};
            resolver.delete(DatabaseContract.BookmarksEntry.CONTENT_URI, selection, selectionArgs);

            /* Remove any images that might have been included in the article */
            ImageUtils.deleteThumbnail(mTitle);
            ImageUtils.deleteImages(mArticleText);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ArticleFragment.this.getContext(),
                    getString(R.string.toast_bookmark_deleted), Toast.LENGTH_LONG).show();

            /* Go back to the previous activity to prevent user trying to save a deleted bookmark */
            getActivity().onBackPressed();
        }
    }
}
