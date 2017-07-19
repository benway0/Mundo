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

package com.github.benway0.mundo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "news.db";

    private static final int DATABASE_VERSION = 23;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* Create table for My Feed data */
        final String SQL_CREATE_NEWS_TABLE =
                "CREATE TABLE " + DatabaseContract.NewsEntry.TABLE_NAME + " (" +
                        DatabaseContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.NewsEntry.COLUMN_TITLE + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.NewsEntry.COLUMN_LINK + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.NewsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                        DatabaseContract.NewsEntry.COLUMN_ENCLOSURE + " VARCHAR(200), " +
                        DatabaseContract.NewsEntry.COLUMN_MEDIACONTENT + " VARCHAR(200), " +
                        DatabaseContract.NewsEntry.COLUMN_PUBDATE + " BIGINT NOT NULL, " +
                        DatabaseContract.NewsEntry.COLUMN_PROVIDER + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.NewsEntry.COLUMN_COUNTRY + " VARCHAR(5) NOT NULL);";
        db.execSQL(SQL_CREATE_NEWS_TABLE);

        /* Create table for countries news data */
        final String SQL_CREATE_COUNTRIES_TABLE =
                "CREATE TABLE " + DatabaseContract.CountriesEntry.TABLE_NAME + " (" +
                        DatabaseContract.CountriesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.CountriesEntry.COLUMN_TITLE + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.CountriesEntry.COLUMN_LINK + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.CountriesEntry.COLUMN_DESCRIPTION + " TEXT, " +
                        DatabaseContract.CountriesEntry.COLUMN_ENCLOSURE + " VARCHAR(200), " +
                        DatabaseContract.CountriesEntry.COLUMN_MEDIACONTENT + " VARCHAR(200), " +
                        DatabaseContract.CountriesEntry.COLUMN_PUBDATE + " BIGINT NOT NULL, " +
                        DatabaseContract.CountriesEntry.COLUMN_PROVIDER + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.CountriesEntry.COLUMN_COUNTRY + " VARCHAR(5) NOT NULL);";
        db.execSQL(SQL_CREATE_COUNTRIES_TABLE);

        /* Create table for bookmarks data */
        final String SQL_CREATE_BOOKMARKS_TABLE =
                "CREATE TABLE " + DatabaseContract.BookmarksEntry.TABLE_NAME + " (" +
                        DatabaseContract.BookmarksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.BookmarksEntry.COLUMN_TITLE + " VARCHAR(200) UNIQUE NOT NULL, " +
                        DatabaseContract.BookmarksEntry.COLUMN_PROVIDER + " VARCHAR(200) NOT NULL, " +
                        DatabaseContract.BookmarksEntry.COLUMN_ARTICLETEXT + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.NewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CountriesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BookmarksEntry.TABLE_NAME);
        onCreate(db);
    }
}
