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
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.github.benway0.mundo.R;
import com.github.benway0.mundo.utilities.PreferenceUtils;

public class FirebaseJobService extends JobService {

    private static final String TAG = FirebaseJobService.class.getSimpleName();

    private AsyncTask mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mAsyncTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = FirebaseJobService.this;

                /* Check user preferences */
                String autoSync = PreferenceUtils.autoSync(context);
                String autoSyncOn = getString(R.string.pref_title_automatic_sync_on_value);
                String autoSyncWifi = getString(R.string.pref_title_automatic_sync_wifi_value);

                boolean wifiConnected = PreferenceUtils.isWifiConnected(context);
                boolean notifications = PreferenceUtils.notifications(context);

                /* Decide which task to schedule based on preferences */
                if (autoSync.equals(autoSyncOn)) {
                    if (notifications) {
                        AutoSyncTask.executeTask(context, AutoSyncTask.NOTIFICATION);
                    } else {
                        AutoSyncTask.executeTask(context, AutoSyncTask.SYNC_FEED);
                    }
                } else if (autoSync.equals(autoSyncWifi)) {
                    if (wifiConnected) {
                        if (notifications) {
                            AutoSyncTask.executeTask(context, AutoSyncTask.NOTIFICATION);
                        } else {
                            AutoSyncTask.executeTask(context, AutoSyncTask.SYNC_FEED);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };
        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mAsyncTask != null) mAsyncTask.cancel(true);
        return true;
    }
}
