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

package com.github.benway0.mundo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.benway0.mundo.R;
import com.github.benway0.mundo.views.DrawerListItem;

public class DrawerListAdapter extends ArrayAdapter<DrawerListItem> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private DrawerListItem[] mData = null;

    private int mSelectedItem;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public DrawerListAdapter(Context context, int layoutResourceId, DrawerListItem[] data) {
        super(context, layoutResourceId, data);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
        mSelectedItem = 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        /* Do action depending on whether it's the drawer header or an item */
        if (getItemViewType(position) == TYPE_HEADER) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(R.layout.item_drawer_header, parent, false);
        } else {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(mLayoutResourceId, parent, false);

            /* Set the icon and title for each drawer item */
            ImageView imageView = (ImageView) view.findViewById(R.id.drawer_item_icon);
            TextView textView = (TextView) view.findViewById(R.id.drawer_item_text);

            DrawerListItem selection = mData[position-1];
            imageView.setImageResource(selection.getIcon());
            textView.setText(selection.getTitle());

            /* Set the color to pink if it's selected, otherwise the default */
            if (position == mSelectedItem) {
                textView.setTextColor(Color.parseColor("#D81B60"));
                imageView.setImageResource(selection.getPinkIcon());
            } else {
                textView.setTextColor(Color.parseColor("#424242"));
                imageView.setImageResource(selection.getIcon());
            }
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return mData.length + 1;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
        notifyDataSetChanged();
    }

    private boolean isHeader(int position) {
        return position == 0;
    }
}