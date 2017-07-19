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

import com.github.benway0.mundo.tasks.XMLParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtils {

    /* So we can delete the cookies and prevent cached versions of the feed from showing up */
    private static final CookieManager COOKIE_MANAGER = new CookieManager() {{
        CookieHandler.setDefault(this);
    }};

    /**
     * Interpret XML items from the relevant RSS feed
     *
     * @param url the URL for the rss feed
     * @param provider the name of the source
     * @param country the country of the source
     * @return a list of XML news items
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static List<XMLParser.Item> loadXmlFromNetwork(String url, String provider,
                                                          String country)
            throws XmlPullParserException, IOException {
        InputStream stream = null;

        XMLParser xmlParser = new XMLParser(provider, country);
        List<XMLParser.Item> items = null;

        try {
            stream = downloadUrl(url);
            items = xmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return items;
    }

    /**
     * Open up a connection to the RSS feed
     *
     * @param urlString the URL of the feed in String format
     * @return the InputStream of the URL connection
     * @throws IOException
     */
    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        /* So we don't get cached versions of the news feeds */
        conn.setUseCaches(false);
        COOKIE_MANAGER.getCookieStore().removeAll();

        conn.connect();

        return conn.getInputStream();
    }

    /**
     * Grab the image URL from the description used in a few sources
     *
     * @param description the information from the description tag in the RSS
     * @return the URL of the image
     */
    public static String getImageFromDescription(String description) {
        Pattern image = Pattern.compile("http.*?.(jpg|png|gif|jpeg)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = image.matcher(description);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
