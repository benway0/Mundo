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

package com.github.benway0.mundo.utilities;

import android.content.Context;
import android.util.Log;

import com.github.benway0.mundo.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /**
     * Convert the date from milliseconds to a "... ago" format for the news feed
     *
     * @param pubTime the time the article was published in milliseconds
     * @param context the relevant context
     * @return the correctly formatted string for the date
     */
    public static String getDateString(long pubTime, Context context) {
        long currentTime = System.currentTimeMillis();

        /* If a feed has an error with their dating and is published in the future, set to now */
        long diff = currentTime - pubTime;
        if (diff < 0) return context.getString(R.string.just_now);

        /* Number of milliseconds in a day, hour and minute */
        int diffDays = (int) diff / 86400000;
        int diffHours = (int) diff / 3600000;
        int diffMins = (int) diff / 60000;

        String daysAgo = context.getString(R.string.days_ago);
        String hoursAgo = context.getString(R.string.hours_ago);
        String minutesAgo = context.getString(R.string.min_ago);

        /* Work out the difference in time and set the correct string */
        if (diffHours == 0) {
            if (diffMins == 0) {
                return context.getString(R.string.just_now);
            }
            return Integer.toString(diffMins) + " " + minutesAgo;
        } else if (diffHours == 1) {
            hoursAgo = context.getString(R.string.hour_ago);
            return Integer.toString(diffHours) + " " + hoursAgo;
        } else if (diffHours >= 24) {
            if (diffHours < 48) {
                daysAgo = context.getString(R.string.day_ago);
                return Integer.toString(diffDays) + " " + daysAgo;
            } else {
                return Integer.toString(diffDays) + " " + daysAgo;
            }
        }
        return Integer.toString(diffHours) + " " + hoursAgo;
    }

    /**
     * Check whether the news item is within the time difference selected by the user in the
     * preferences
     *
     * @param pubTime the time the article was published in milliseconds
     * @param context the relevant context
     * @return whether the article is in the relevant time frame selected by the user
     */
    public static boolean inTime(long pubTime, Context context) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - pubTime;

        long prefTime = PreferenceUtils.getTime(context);

        if (diff > (prefTime * 60000))
            return false;

        return true;
    }

    /**
     * Convert date from String format into milliseconds
     *
     * @param date the date to format
     * @return the date parameter in milliseconds
     */
    public static long convertDateToMillis(String date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date pubDate = new Date();
        try {
            pubDate = dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.d(TAG, ex.getMessage());
        }

        return pubDate.getTime();
    }

    /**
     * Convert date from milliseconds to String format
     *
     * @param dateInMillis the relevant date in milliseconds
     * @return the date param in String format
     */
    public static String convertDateFromMillis(long dateInMillis) {
        Date date = new Date(dateInMillis);

        DateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM, HH:mm", Locale.ENGLISH);
        String dateString = dateFormat.format(date);

        return dateString;
    }
}
