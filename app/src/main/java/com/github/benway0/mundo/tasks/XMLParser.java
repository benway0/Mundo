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

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    /* Name space */
    private static final String ns = null;

    private String mProvider;
    private String mCountry;

    public XMLParser(String provider, String country) {
        mProvider = provider;
        mCountry = country;
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        /* Go into the rss and then the channel tag found at the start of rss feeds */
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("channel")) {
                return readChannel(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    private List readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        List items = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {

            /* If not an opening tag keep looking */
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            /* Find each individual rss item in the feed */
            String name = parser.getName();
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String link = null;
        String description = null;
        String enclosure = null;
        String mediaContent = null;
        String pubDate = null;

        while (parser.next() != XmlPullParser.END_TAG) {

            /* If not an opening tag keep looking */
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            /* Name of the current tag */
            String name = parser.getName();

            /* Read the relevant info based on the current tag */
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("enclosure") &&
                    (parser.getAttributeValue(null, "type").equals("image/jpeg") ||
                            parser.getAttributeValue(null, "type").equals("image/jpg"))) {
                enclosure = readEnclosure(parser);
            } else if (name.equals("media:content")) {
                mediaContent = readMediaContent(parser);
            } else if (name.equals("pubDate")) {
                pubDate = readPubDate(parser);
            } else {
                skip(parser);
            }
        }
        return new Item(title, link, description, enclosure, mediaContent, pubDate, mProvider,
                mCountry);
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private String readEnclosure(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "enclosure");
        String enclosure = "";
        String tag = parser.getName();
        if (tag.equals("enclosure")) {
            enclosure = parser.getAttributeValue(null, "url");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "enclosure");
        return enclosure;
    }

    private String readMediaContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "media:content");
        String mediaContent = "";
        String tag = parser.getName();
        if (tag.equals("media:content")) {
            mediaContent = parser.getAttributeValue(null, "url");

            /* Skip if there is more content enclosed within the media:content tag */
            while (parser.getEventType() == XmlPullParser.START_TAG) {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "media:content");
        return mediaContent;
    }

    private String readPubDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return pubDate;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException("Illegal state exception");
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /** Inner class for items */
    public static class Item {
        public final String title;
        public final String link;
        public final String description;
        public final String enclosure;
        public final String mediaContent;
        public final String pubDate;
        public final String provider;
        public final String country;

        private Item(String title, String link, String description, String enclosure,
                     String mediaContent, String pubDate, String provider, String country) {
            this.title = title;
            this.link = link;
            this.description = description;
            this.enclosure = enclosure;
            this.mediaContent = mediaContent;
            this.pubDate = pubDate;
            this.provider = provider;
            this.country = country;
        }
    }
}
