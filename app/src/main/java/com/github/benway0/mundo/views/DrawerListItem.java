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

package com.github.benway0.mundo.views;

public class DrawerListItem {

    private String mTitle;
    private int mIcon;
    private int mPinkIcon;

    public DrawerListItem(String title, int icon, int pinkIcon) {
        mTitle = title;
        mIcon = icon;
        mPinkIcon = pinkIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public int getPinkIcon() {
        return mPinkIcon;
    }
}
