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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.preference.PreferenceManager;

import com.github.benway0.mundo.R;

public class PreferenceUtils {

    /** Check whether Display Images is turned on */
    public static boolean imagesOn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String images = sp.getString(context.getString(R.string.pref_key_display_images),
                context.getString(R.string.pref_title_display_images_on_value));
        return images.equals(context.getString(R.string.pref_title_display_images_on_value));
    }

    /** Check whether Display Images is on WiFi only */
    public static boolean imagesWifi(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String wifi = sp.getString(context.getString(R.string.pref_key_display_images),
                context.getString(R.string.pref_title_display_images_on_value));
        return wifi.equals(context.getString(R.string.pref_title_display_images_wifi_value));
    }

    /** Check if the device is connected to WiFi */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /** Check if the auto sync preference is turned on */
    public static String autoSync(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_key_automatic_sync),
                context.getString(R.string.pref_title_automatic_sync_on_value));
    }

    /** Check whether a certain source is set to favourite */
    public static boolean getSource(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    /** Check whether notifications are turned on */
    public static boolean notifications(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_key_notifications), true);
    }

    /** Check the preference for length of time to load */
    public static long getTime(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String timeString = sp.getString(context.getString(R.string.pref_key_show_articles_from),
                context.getString(R.string.pref_title_show_articles_from_12hours_value));
        return Long.parseLong(timeString);
    }
}
