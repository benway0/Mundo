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

package com.github.benway0.mundo.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.github.benway0.mundo.R;

import java.util.concurrent.TimeUnit;

public class NewsSyncUtils {

    private static boolean sInitialized;

    private static final String REMINDER_JOB_TAG = "feed_update_tag";

    /**
     * This method will only run once per app lifecycle
     *
     * @param context the relevant context
     */
    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;

        scheduleSync(context);
    }

    /**
     * Schedule the Firebase Job Service
     *
     * @param context the relevant context
     */
    public static void scheduleSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Get the user preferences */
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String intervalString =
                sharedPreferences.getString(context.getString(R.string.pref_key_hourly_sync),
                        context.getString(R.string.pref_title_hourly_sync_12hours_value));

        /* Set the time for the job service based on preferences */
        int intervalMinutes = Integer.parseInt(intervalString);
        int intervalSeconds = (int) (TimeUnit.MINUTES.toSeconds(intervalMinutes));
        int syncSeconds = intervalSeconds;

        /* Schedule the job service */
        Job job = dispatcher.newJobBuilder()
                .setService(FirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(intervalSeconds,
                        intervalSeconds + syncSeconds))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(job);
    }

    public static void sync(@NonNull final Context context) {
        Intent intent = new Intent(context, NewsSyncIntentService.class);
        intent.putExtra("myfeed", true);
        context.startService(intent);
    }
}
