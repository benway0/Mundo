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

import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.github.benway0.mundo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NEWS = "news";
    public static final String PATH_COUNTRIES = "countries";
    public static final String PATH_SEARCH = "search";
    public static final String PATH_BOOKMARKS = "bookmarks";

    public static final class NewsEntry implements BaseColumns {

        /* Build the URI for the My Feed provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS)
                .build();

        /* Build the URI for searching My Feed */
        public static Uri SEARCH_URI(String search) {
            if (TextUtils.isEmpty(search)) search = " ";
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).appendPath(PATH_SEARCH)
                    .appendPath(search).build();
        }

        /* My Feed table name */
        public static final String TABLE_NAME = "news";

        /* My Feed table columns */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ENCLOSURE = "enclosure";
        public static final String COLUMN_MEDIACONTENT = "mediacontent";
        public static final String COLUMN_PUBDATE = "pubdate";
        public static final String COLUMN_PROVIDER = "provider";
        public static final String COLUMN_COUNTRY = "country";

        public static boolean isSearchUri(Uri uri) {
            return uri.toString().startsWith(CONTENT_AUTHORITY + "/news/search/");
        }
    }

    public static final class CountriesEntry implements BaseColumns {

        /* Build the URI for the individual country provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_COUNTRIES).build();

        /* Build the URI for searching country news */
        public static Uri SEARCH_URI(String search) {
            if (TextUtils.isEmpty(search)) search = " ";
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTRIES).appendPath(PATH_SEARCH)
                    .appendPath(search).build();
        }

        /* Individual country table name */
        public static final String TABLE_NAME = "countries";

        /* Individual country table columns */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ENCLOSURE = "enclosure";
        public static final String COLUMN_MEDIACONTENT = "mediacontent";
        public static final String COLUMN_PUBDATE = "pubdate";
        public static final String COLUMN_PROVIDER = "provider";
        public static final String COLUMN_COUNTRY = "country";

        public static boolean isSearchUri(Uri uri) {
            return uri.toString().startsWith(CONTENT_AUTHORITY + "/countries/search/");
        }
    }

    public static final class BookmarksEntry implements BaseColumns {

        /* Build the URI for the bookmarks provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_BOOKMARKS).build();

        /* Build the URI for searching the bookmarks provider */
        public static Uri SEARCH_URI(String search) {
            if (TextUtils.isEmpty(search)) search = " ";
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKMARKS).appendPath(PATH_SEARCH)
                    .appendPath(search).build();
        }

        /* Bookmarks table name */
        public static final String TABLE_NAME = "bookmarks";

        /* Bookmarks table columns */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PROVIDER = "provider";
        public static final String COLUMN_ARTICLETEXT = "articletext";

        public static boolean isSearchUri(Uri uri) {
            return uri.toString().startsWith(CONTENT_AUTHORITY + "/bookmarks/search/");
        }
    }
}
