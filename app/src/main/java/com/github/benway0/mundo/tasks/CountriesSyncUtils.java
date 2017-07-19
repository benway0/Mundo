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
import android.support.annotation.NonNull;

public class CountriesSyncUtils {

    public static void sync(@NonNull final Context context) {
        Intent intent = new Intent(context, NewsSyncIntentService.class);

        /* Syncing countries instead of My Feed so set to false */
        intent.putExtra("myfeed", false);

        context.startService(intent);
    }
}
