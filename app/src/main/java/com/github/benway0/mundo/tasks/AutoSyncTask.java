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

import com.github.benway0.mundo.utilities.NotificationUtils;

public class AutoSyncTask {

    public static final String SYNC_FEED = "syncfeed";
    public static final String NOTIFICATION = "notification";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {

        switch (action) {
            case SYNC_FEED:
                NewsSyncUtils.sync(context);
                break;
            case NOTIFICATION:
                NewsSyncUtils.sync(context);
                setNotification(context);
                break;
            case ACTION_DISMISS_NOTIFICATION:
                NotificationUtils.clearNotifications(context);
                break;
            default:
                break;
        }
    }

    private static void setNotification(Context context) {
        NotificationUtils.notify(context);
    }
}
