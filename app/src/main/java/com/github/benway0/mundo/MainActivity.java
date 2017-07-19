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

package com.github.benway0.mundo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.benway0.mundo.adapters.DrawerListAdapter;
import com.github.benway0.mundo.fragments.BookmarksFragment;
import com.github.benway0.mundo.fragments.CountriesFragment;
import com.github.benway0.mundo.fragments.FeedFragment;
import com.github.benway0.mundo.tasks.NewsSyncUtils;
import com.github.benway0.mundo.views.DrawerListItem;

public class MainActivity extends AppCompatActivity {

    /* DrawerLayout variables */
    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mDrawerAdapter;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    /* Currently selected drawer item */
    private static int mSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set up the Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /* Decide which fragment to show depending on DrawerLayout selection */
        Fragment fragment;
        switch (mSelected) {
            case 1:
                fragment = FeedFragment.newInstance();
                getSupportActionBar().setTitle(getString(R.string.title_my_feed));
                break;
            case 2:
                fragment = CountriesFragment.newInstance();
                getSupportActionBar().setTitle(getString(R.string.title_browse_by_country));
                break;
            case 3:
                fragment = BookmarksFragment.newInstance();
                getSupportActionBar().setTitle(getString(R.string.title_bookmarks));
                break;
            default:
                fragment = FeedFragment.newInstance();
                break;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, fragment).commit();

        /* Set up the Drawer */
        setUpDrawer();

        /* Show welcome dialog if application is opened for the first time */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isFirstTime = sp.getBoolean(getString(R.string.pref_key_is_first_time), true);

        /* If app is open for the first time display the welcome dialog */
        if (isFirstTime) {
            mDrawerLayout.openDrawer(mDrawerList);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_action_bell)
                    .setTitle(getString(R.string.welcome_title))
                    .setMessage(getString(R.string.welcome_text))
                    .setPositiveButton(getString(R.string.welcome_ok),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            /* Make sure this isn't displayed again */
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(getString(R.string.pref_key_is_first_time), false);
            editor.commit();
        }

        /* Initialize the Firebase service */
        NewsSyncUtils.initialize(this);
    }

    /** Set up the drawer items */
    private void setUpDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navigation_drawer_listview);

        /* Store the items */
        DrawerListItem[] data = {
                new DrawerListItem(getString(R.string.drawer_item_my_feed),
                        R.drawable.ic_action_news, R.drawable.ic_action_news_pink),
                new DrawerListItem(getString(R.string.drawer_item_browse_by_country),
                        R.drawable.ic_action_globe, R.drawable.ic_action_globe_pink),
                new DrawerListItem(getString(R.string.drawer_item_bookmarks),
                        R.drawable.ic_action_bookmark, R.drawable.ic_action_bookmark_pink),
                new DrawerListItem(getString(R.string.drawer_item_settings),
                        R.drawable.ic_action_gear, R.drawable.ic_action_gear_pink),
                new DrawerListItem(getString(R.string.drawer_item_about),
                        R.drawable.ic_action_info, R.drawable.ic_action_info_pink)
        };
        mDrawerAdapter = new DrawerListAdapter(this, R.layout.item_drawer, data);

        /* Initialise the selected item */
        mDrawerAdapter.setSelectedItem(mSelected);

        mDrawerList.setAdapter(mDrawerAdapter);

        /* Set what happens when the drawer is opened and closed */
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new MainActivity.DrawerItemClickListener());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    /** What happens when the Drawer configuration is changed */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Set what happens when a drawer item is clicked */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            /* Set the selected item to the current position and close the drawer */
            if (position != 0 && position != 4 && position != 5) {
                mSelected = position;

                mDrawerAdapter.setSelectedItem(mSelected);
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setAdapter(mDrawerAdapter);
            }

            mDrawerLayout.closeDrawer(mDrawerList);

            Fragment fragment;

            switch (position) {
                case 0: // Header
                    break;
                case 1: // My Feed
                    fragment = FeedFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment).commit();
                    getSupportActionBar().setTitle(getString(R.string.title_my_feed));
                    break;
                case 2: // Browse by Country
                    fragment = CountriesFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment).commit();
                    getSupportActionBar().setTitle(getString(R.string.title_browse_by_country));
                    break;
                case 3: // Bookmarks
                    fragment = BookmarksFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment).commit();
                    getSupportActionBar().setTitle(getString(R.string.title_bookmarks));
                    break;
                case 4: // Settings
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);
                    break;
                case 5: // About
                    LayoutInflater inflater =
                            (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View aboutView = inflater.inflate(R.layout.view_about, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.drawable.ic_action_info_pink)
                            .setTitle(getString(R.string.about_title))
                            .setView(aboutView)
                            .setPositiveButton(getString(R.string.welcome_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    mDrawerLayout.closeDrawers();
                    break;
                default:
                    break;
            }
        }
    }
}
