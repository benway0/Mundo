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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.benway0.mundo.utilities.DateUtils;

public class NewsProvider extends ContentProvider {

    /* Provider codes */
    private static final int CODE_NEWS = 100;
    private static final int CODE_COUNTRIES = 101;
    private static final int CODE_SEARCH = 102;
    private static final int CODE_BOOKMARKS = 103;
    private static final int CODE_COUNTRIES_SEARCH = 104;
    private static final int CODE_BOOKMARKS_SEARCH = 105;

    /* Provider paths */
    private static final String PATH_SEARCH = "news/search/*";
    private static final String PATH_COUNTRIES_SEARCH = "countries/search/*";
    private static final String PATH_BOOKMARKS_SEARCH = "bookmarks/search/*";

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper mHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_NEWS, CODE_NEWS);
        matcher.addURI(authority, DatabaseContract.PATH_COUNTRIES, CODE_COUNTRIES);
        matcher.addURI(authority, PATH_SEARCH, CODE_SEARCH);
        matcher.addURI(authority, DatabaseContract.PATH_BOOKMARKS, CODE_BOOKMARKS);
        matcher.addURI(authority, PATH_COUNTRIES_SEARCH, CODE_COUNTRIES_SEARCH);
        matcher.addURI(authority, PATH_BOOKMARKS_SEARCH, CODE_BOOKMARKS_SEARCH);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();

        int rowsInserted;
        switch (sUriMatcher.match(uri)) {
            case CODE_NEWS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long pubDate = value.getAsLong(DatabaseContract.NewsEntry.COLUMN_PUBDATE);
                        if (DateUtils.inTime(pubDate, getContext())) {
                            long _id = db.insert(DatabaseContract.NewsEntry.TABLE_NAME, null, value);
                            if (_id != -1) rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            case CODE_COUNTRIES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long pubDate =
                                value.getAsLong(DatabaseContract.CountriesEntry.COLUMN_PUBDATE);
                        if (DateUtils.inTime(pubDate, getContext())) {
                            long _id = db.insert(DatabaseContract.CountriesEntry.TABLE_NAME, null,
                                    value);
                            if (_id != -1) rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = mHelper.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case CODE_NEWS:
                builder.setTables(DatabaseContract.NewsEntry.TABLE_NAME);
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case CODE_COUNTRIES:
                builder.setTables(DatabaseContract.CountriesEntry.TABLE_NAME);
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case CODE_SEARCH:
                builder.setTables(DatabaseContract.NewsEntry.TABLE_NAME);
                builder.appendWhere(getWhere(uri.getPathSegments().get(2)));
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case CODE_BOOKMARKS:
                builder.setTables(DatabaseContract.BookmarksEntry.TABLE_NAME);
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case CODE_COUNTRIES_SEARCH:
                builder.setTables(DatabaseContract.CountriesEntry.TABLE_NAME);
                builder.appendWhere(getWhere(uri.getPathSegments().get(2)));
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case CODE_BOOKMARKS_SEARCH:
                builder.setTables(DatabaseContract.BookmarksEntry.TABLE_NAME);
                builder.appendWhere(getWhere(uri.getPathSegments().get(2)));
                cursor = builder.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        int numRowsDeleted;

        if (selection == null) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_NEWS:
                numRowsDeleted = db.delete(DatabaseContract.NewsEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CODE_COUNTRIES:
                numRowsDeleted = db.delete(DatabaseContract.CountriesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CODE_BOOKMARKS:
                numRowsDeleted = db.delete(DatabaseContract.BookmarksEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI");
        }

        if (numRowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return numRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        long _id = -1;

        switch (sUriMatcher.match(uri)) {
            case CODE_BOOKMARKS:
                try {
                    database.beginTransaction();
                    _id = database.insert(DatabaseContract.BookmarksEntry.TABLE_NAME, null, values);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;
        }

        if (_id > -1)
            return ContentUris.withAppendedId(uri, _id);
        else
            return uri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    private static String getWhere(String search) {
        search = Uri.decode(search).trim();
        if (!search.isEmpty()) {
            search = DatabaseUtils.sqlEscapeString("%" + Uri.decode(search) + "%");
            return DatabaseContract.NewsEntry.COLUMN_TITLE + " LIKE " + search;
        } else {
            return "2 + 2 = 5";
        }
    }
}
