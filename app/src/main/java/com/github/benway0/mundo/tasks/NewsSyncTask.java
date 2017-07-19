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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.github.benway0.mundo.R;
import com.github.benway0.mundo.data.DatabaseContract;
import com.github.benway0.mundo.fragments.CountryFragment;
import com.github.benway0.mundo.fragments.FeedFragment;
import com.github.benway0.mundo.utilities.DateUtils;
import com.github.benway0.mundo.utilities.NetworkUtils;
import com.github.benway0.mundo.utilities.PreferenceUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsSyncTask {

    /* Provider URLs */
    public static final String URL_ABC = "http://www.abc.net.au/news/feed/45910/rss.xml";
    public static final String URL_ALJAZEERA = "http://www.aljazeera.com/xml/rss/all.xml";
    public static final String URL_ANGOP = "http://www.angop.ao/angola/en_us/rss/58830be5-77c5-47b6-8e54-7566bba83104.xml";
    public static final String URL_ARABNEWS = "http://www.arabnews.com/rss.xml";
    public static final String URL_BBC = "http://feeds.bbci.co.uk/news/rss.xml";
    public static final String URL_BIANET = "http://bianet.org/english.rss";
    public static final String URL_BULAWAYO = "http://bulawayo24.com/feeds-rss-rss.rss";
    public static final String URL_BREITBART = "https://feeds.feedburner.com/breitbart?format=xml";
    public static final String URL_COLOMBIAREPORTS = "https://colombiareports.com/feed/";
    public static final String URL_COPENHAGENPOST = "http://cphpost.dk/rss-feed";
    public static final String URL_DAILYNATION = "http://www.nation.co.ke/latestrss.rss";
    public static final String URL_DAILYSABAH = "https://www.dailysabah.com/rss/homepage";
    public static final String URL_FRANCE24 = "http://www.france24.com/en/top-stories/rss";
    public static final String URL_GLOBALNEWS = "http://globalnews.ca/feed/";
    public static final String URL_HINDUSTANTIMES = "http://www.hindustantimes.com/rss/india/rssfeed.xml";
    public static final String URL_HURRIYET = "http://www.hurriyetdailynews.com/rss.aspx";
    public static final String URL_INDEPENDENT = "https://www.independent.co.uk/news/uk/rss";
    public static final String URL_INQUIRER = "http://www.inquirer.net/fullfeed";
    public static final String URL_IRISHTIMES = "https://www.irishtimes.com/cmlink/news-1.1319192";
    public static final String URL_JAPANTODAY = "https://japantoday.com/feed";
    public static final String URL_JERUSALEMPOST = "http://www.jpost.com/Rss/RssFeedsIsraelNews.aspx";
    public static final String URL_KHALEEJTIMES = "http://www.khaleejtimes.com/services/rss/uae/rss.xml";
    public static final String URL_KOREAHERALD = "http://biz.heraldcorp.com/common/rss_xml.php?ct=102";
    public static final String URL_MERCOPRESS = "http://en.mercopress.com/rss/";
    public static final String URL_NATIONALACCORD = "https://nationalaccordnewspaper.com/feed/";
    public static final String URL_NEWS24 = "http://feeds.news24.com/articles/news24/TopStories/rss";
    public static final String URL_NYPOST = "https://nypost.com/news/feed/";
    public static final String URL_NYTIMES = "http://rss.nytimes.com/services/xml/rss/nyt/World.xml";
    public static final String URL_NZHERALD = "http://rss.nzherald.co.nz/rss/xml/nzhrsscid_000000001.xml";
    public static final String URL_PEOPLESDAILY = "http://en.people.cn/rss/90777.xml";
    public static final String URL_PRAVDA = "http://www.pravdareport.com/world/export.xml";
    public static final String URL_PRESSTV = "http://www.presstv.ir/RSS/MRSS/1";
    public static final String URL_RADIOPOLAND = "http://www.thenews.pl/Rss/88cec208-19c8-4431-b2b9-5b1ecec7fc6e";
    public static final String URL_REUTERS = "http://feeds.reuters.com/reuters/UKTopNews";
    public static final String URL_RT = "https://www.rt.com/rss/";
    public static final String URL_RTE = "https://www.rte.ie/news/rss/news-headlines.xml";
    public static final String URL_SANA = "http://sana.sy/en/?feed=rss2";
    public static final String URL_SHANGHAIDAILY = "http://rss.shanghaidaily.com/Portal/mainSite/Handler.ashx?i=7";
    public static final String URL_SKYNEWS = "http://feeds.skynews.com/feeds/rss/home.xml";
    public static final String URL_SPIEGEL = "http://m.spiegel.de/international/index.rss";
    public static final String URL_SPUTNIK = "https://sputniknews.com/export/rss2/archive/index.xml";
    public static final String URL_STRAITSTIMES = "http://www.straitstimes.com/news/world/rss.xml";
    public static final String URL_TEHRANTIMES = "http://www.tehrantimes.com/rss/tp/698";
    public static final String URL_TELEGRAPH = "http://www.telegraph.co.uk/news/rss.xml";
    public static final String URL_THEGUARDIAN = "https://www.theguardian.com/uk/rss";
    public static final String URL_TIMESOFISRAEL = "http://www.timesofisrael.com/feed/";
    public static final String URL_UNIAN = "http://rss.unian.net/site/news_eng.rss";
    public static final String URL_WASHINGTONTIMES = "http://www.washingtontimes.com/rss/headlines/news/world/";

    /* Provider URL arrays */
    private static final String[] URLS_ANGOLA = {URL_ANGOP};
    private static final String[] URLS_AUSTRALIA = {URL_ABC};
    private static final String[] URLS_CANADA = {URL_GLOBALNEWS};
    private static final String[] URLS_CHINA = {URL_PEOPLESDAILY, URL_SHANGHAIDAILY};
    private static final String[] URLS_COLOMBIA = {URL_COLOMBIAREPORTS};
    private static final String[] URLS_DENMARK = {URL_COPENHAGENPOST};
    private static final String[] URLS_FRANCE = {URL_FRANCE24};
    private static final String[] URLS_GERMANY = {URL_SPIEGEL};
    private static final String[] URLS_INDIA = {URL_HINDUSTANTIMES};
    private static final String[] URLS_IRAN = {URL_PRESSTV, URL_TEHRANTIMES};
    private static final String[] URLS_IRELAND = {URL_IRISHTIMES, URL_RTE};
    private static final String[] URLS_ISRAEL = {URL_JERUSALEMPOST, URL_TIMESOFISRAEL};
    private static final String[] URLS_JAPAN = {URL_JAPANTODAY};
    private static final String[] URLS_KENYA = {URL_DAILYNATION};
    private static final String[] URLS_NEWZEALAND = {URL_NZHERALD};
    private static final String[] URLS_NIGERIA = {URL_NATIONALACCORD};
    private static final String[] URLS_PHILIPPINES = {URL_INQUIRER};
    private static final String[] URLS_POLAND = {URL_RADIOPOLAND};
    private static final String[] URLS_QATAR = {URL_ALJAZEERA};
    private static final String[] URLS_RUSSIA = {URL_RT, URL_SPUTNIK, URL_PRAVDA};
    private static final String[] URLS_SAUDIARABIA = {URL_ARABNEWS};
    private static final String[] URLS_SINGAPORE = {URL_STRAITSTIMES};
    private static final String[] URLS_SOUTHAFRICA = {URL_NEWS24};
    private static final String[] URLS_SOUTHKOREA = {URL_KOREAHERALD};
    private static final String[] URLS_SYRIA = {URL_SANA};
    private static final String[] URLS_TURKEY = {URL_BIANET, URL_DAILYSABAH, URL_HURRIYET};
    private static final String[] URLS_UAE = {URL_KHALEEJTIMES};
    private static final String[] URLS_UKRAINE = {URL_UNIAN};
    private static final String[] URLS_UNITEDKINGDOM = {URL_BBC, URL_INDEPENDENT, URL_REUTERS,
            URL_SKYNEWS, URL_TELEGRAPH, URL_THEGUARDIAN};
    private static final String[] URLS_URUGUAY = {URL_MERCOPRESS};
    private static final String[] URLS_USA = {URL_BREITBART, URL_NYPOST, URL_NYTIMES,
            URL_WASHINGTONTIMES};
    private static final String[] URLS_ZIMBABWE = {URL_BULAWAYO};

    /* Provider names */
    public static final String PROVIDER_ABC = "ABC.net.au";
    public static final String PROVIDER_ALJAZEERA = "Al Jazeera";
    public static final String PROVIDER_ANGOP = "ANGOP";
    public static final String PROVIDER_ARABNEWS = "Arab News";
    public static final String PROVIDER_BBC = "BBC";
    public static final String PROVIDER_BIANET = "Bianet";
    public static final String PROVIDER_BULAWAYO = "Bulawayo";
    public static final String PROVIDER_BREITBART = "Breitbart";
    public static final String PROVIDER_COLOMBIAREPORTS = "Colombia Reports";
    public static final String PROVIDER_COPENHAGENPOST = "Copenhagen Post";
    public static final String PROVIDER_DAILYNATION = "Daily Nation";
    public static final String PROVIDER_DAILYSABAH = "Daily Sabah";
    public static final String PROVIDER_FRANCE24 = "France 24";
    public static final String PROVIDER_GLOBALNEWS = "Global News";
    public static final String PROVIDER_HINDUSTANTIMES = "Hindustan Times";
    public static final String PROVIDER_HURRIYET = "Hurriyet";
    public static final String PROVIDER_INDEPENDENT = "Independent";
    public static final String PROVIDER_INQUIRER = "Inquirer";
    public static final String PROVIDER_IRISHTIMES = "Irish Times";
    public static final String PROVIDER_JAPANTODAY = "Japan Today";
    public static final String PROVIDER_JERUSALEMPOST = "Jerusalem Post";
    public static final String PROVIDER_KHALEEJTIMES = "Khaleej Times";
    public static final String PROVIDER_KOREAHERALD = "Korea Herald";
    public static final String PROVIDER_MERCOPRESS = "MercoPress";
    public static final String PROVIDER_NATIONALACCORD = "National Accord";
    public static final String PROVIDER_NEWS24 = "News 24";
    public static final String PROVIDER_NYPOST = "NY Post";
    public static final String PROVIDER_NYTIMES = "NY Times";
    public static final String PROVIDER_NZHERALD = "NZ Herald";
    public static final String PROVIDER_PEOPLESDAILY = "People's Daily";
    public static final String PROVIDER_PRAVDA = "Pravda";
    public static final String PROVIDER_PRESSTV = "PressTV";
    public static final String PROVIDER_RADIOPOLAND = "Radio Poland";
    public static final String PROVIDER_REUTERS = "Reuters";
    public static final String PROVIDER_RT = "RT";
    public static final String PROVIDER_RTE = "RTE";
    public static final String PROVIDER_SANA = "Syrian Arab News Agency";
    public static final String PROVIDER_SHANGHAIDAILY = "Shanghai Daily";
    public static final String PROVIDER_SKYNEWS = "Sky News";
    public static final String PROVIDER_SPIEGEL = "Spiegel";
    public static final String PROVIDER_SPUTNIK = "Sputnik";
    public static final String PROVIDER_STRAITSTIMES = "Straits Times";
    public static final String PROVIDER_TEHRANTIMES = "Tehran Times";
    public static final String PROVIDER_TELEGRAPH = "Telegraph";
    public static final String PROVIDER_THEGUARDIAN = "The Guardian";
    public static final String PROVIDER_TIMESOFISRAEL = "Times of Israel";
    public static final String PROVIDER_UNIAN = "Unian";
    public static final String PROVIDER_WASHINGTONTIMES = "Washington Times";

    /* Provider name arrays */
    private static final String[] PROVIDERS_ANGOLA = {PROVIDER_ANGOP};
    private static final String[] PROVIDERS_AUSTRALIA = {PROVIDER_ABC};
    private static final String[] PROVIDERS_CANADA = {PROVIDER_GLOBALNEWS};
    private static final String[] PROVIDERS_CHINA = {PROVIDER_PEOPLESDAILY, PROVIDER_SHANGHAIDAILY};
    private static final String[] PROVIDERS_COLOMBIA = {PROVIDER_COLOMBIAREPORTS};
    private static final String[] PROVIDERS_DENMARK = {PROVIDER_COPENHAGENPOST};
    private static final String[] PROVIDERS_FRANCE24 = {PROVIDER_FRANCE24};
    private static final String[] PROVIDERS_GERMANY = {PROVIDER_SPIEGEL};
    private static final String[] PROVIDERS_INDIA = {PROVIDER_HINDUSTANTIMES};
    private static final String[] PROVIDERS_IRAN = {PROVIDER_PRESSTV, PROVIDER_TEHRANTIMES};
    private static final String[] PROVIDERS_IRELAND = {PROVIDER_IRISHTIMES, PROVIDER_RTE};
    private static final String[] PROVIDERS_ISRAEL = {PROVIDER_JERUSALEMPOST,
            PROVIDER_TIMESOFISRAEL};
    private static final String[] PROVIDERS_JAPAN = {PROVIDER_JAPANTODAY};
    private static final String[] PROVIDERS_KENYA = {PROVIDER_DAILYNATION};
    private static final String[] PROVIDERS_NEWZEALAND = {PROVIDER_NZHERALD};
    private static final String[] PROVIDERS_NIGERIA = {PROVIDER_NATIONALACCORD};
    private static final String[] PROVIDERS_PHILIPPINES = {PROVIDER_INQUIRER};
    private static final String[] PROVIDERS_POLAND = {PROVIDER_RADIOPOLAND};
    private static final String[] PROVIDERS_QATAR = {PROVIDER_ALJAZEERA};
    private static final String[] PROVIDERS_RUSSIA = {PROVIDER_RT, PROVIDER_SPUTNIK,
            PROVIDER_PRAVDA};
    private static final String[] PROVIDERS_SAUDIARABIA = {PROVIDER_ARABNEWS};
    private static final String[] PROVIDERS_SINGAPORE = {PROVIDER_STRAITSTIMES};
    private static final String[] PROVIDERS_SOUTHAFRICA = {PROVIDER_NEWS24};
    private static final String[] PROVIDERS_SOUTHKOREA = {PROVIDER_KOREAHERALD};
    private static final String[] PROVIDERS_SYRIA = {PROVIDER_SANA};
    private static final String[] PROVIDERS_TURKEY = {PROVIDER_BIANET, PROVIDER_DAILYSABAH,
            PROVIDER_HURRIYET};
    private static final String[] PROVIDERS_UAE = {PROVIDER_KHALEEJTIMES};
    private static final String[] PROVIDERS_UKRAINE = {PROVIDER_UNIAN};
    private static final String[] PROVIDERS_UNITEDKINGDOM = {PROVIDER_BBC, PROVIDER_INDEPENDENT,
            PROVIDER_REUTERS, PROVIDER_SKYNEWS, PROVIDER_TELEGRAPH, PROVIDER_THEGUARDIAN};
    private static final String[] PROVIDERS_URUGUAY = {PROVIDER_MERCOPRESS};
    private static final String[] PROVIDERS_USA = {PROVIDER_BREITBART, PROVIDER_NYPOST,
            PROVIDER_NYTIMES, PROVIDER_WASHINGTONTIMES};
    private static final String[] PROVIDERS_ZIMBABWE = {PROVIDER_BULAWAYO};

    /* Country codes */
    public static final String CODE_ANGOLA = "ao";
    public static final String CODE_AUSTRALIA = "au";
    public static final String CODE_CANADA = "ca";
    public static final String CODE_CHINA = "cn";
    public static final String CODE_COLOMBIA = "co";
    public static final String CODE_DENMARK = "dk";
    public static final String CODE_FRANCE = "fr";
    public static final String CODE_GERMANY = "de";
    public static final String CODE_INDIA = "in";
    public static final String CODE_IRAN = "ir";
    public static final String CODE_IRELAND = "ie";
    public static final String CODE_ISRAEL = "il";
    public static final String CODE_JAPAN = "jp";
    public static final String CODE_KENYA = "ke";
    public static final String CODE_NEWZEALAND = "nz";
    public static final String CODE_NIGERIA = "ng";
    public static final String CODE_PHILIPPINES = "ph";
    public static final String CODE_POLAND = "pl";
    public static final String CODE_QATAR = "qa";
    public static final String CODE_RUSSIA = "ru";
    public static final String CODE_SAUDIARABIA = "sa";
    public static final String CODE_SINGAPORE = "sg";
    public static final String CODE_SOUTHAFRICA = "za";
    public static final String CODE_SOUTHKOREA = "kr";
    public static final String CODE_SYRIA = "sy";
    public static final String CODE_TURKEY = "tr";
    public static final String CODE_UAE = "ae";
    public static final String CODE_UKRAINE = "ua";
    public static final String CODE_UNITEDKINGDOM = "uk";
    public static final String CODE_URUGUAY = "uy";
    public static final String CODE_USA = "us";
    public static final String CODE_ZIMBABWE = "zw";

    synchronized public static void syncNews(Context context, boolean isMyFeed) {
        if (isMyFeed) {
            syncFeed(context);
        } else {
            syncCountries(context);
        }
    }

    /**
     * Method for syncing My Feed
     *
     * @param context the relevant context
     */
    private static void syncFeed(Context context) {
        List<XMLParser.Item> itemList = new ArrayList<>();

        syncSources(itemList, context);

        List<ContentValues> valueList = new ArrayList<>();
        ContentValues values;

        /* Store all of the found information in a value list */
        for (XMLParser.Item item : itemList) {
            values = new ContentValues();
            values.put(DatabaseContract.NewsEntry.COLUMN_TITLE, item.title);
            values.put(DatabaseContract.NewsEntry.COLUMN_LINK, item.link);
            values.put(DatabaseContract.NewsEntry.COLUMN_DESCRIPTION, item.description);
            values.put(DatabaseContract.NewsEntry.COLUMN_ENCLOSURE, item.enclosure);
            values.put(DatabaseContract.NewsEntry.COLUMN_MEDIACONTENT, item.mediaContent);
            long pubDateInMillis = DateUtils.convertDateToMillis(item.pubDate);
            values.put(DatabaseContract.NewsEntry.COLUMN_PUBDATE, pubDateInMillis);
            values.put(DatabaseContract.NewsEntry.COLUMN_PROVIDER, item.provider);
            values.put(DatabaseContract.NewsEntry.COLUMN_COUNTRY, item.country);
            valueList.add(values);
        }

        /* Delete all the existing values in the database */
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(DatabaseContract.NewsEntry.CONTENT_URI,
                null,
                null);

        /* Insert the values to the database */
        resolver.bulkInsert(DatabaseContract.NewsEntry.CONTENT_URI,
                valueList.toArray(new ContentValues[valueList.size()]));

        /* Hide the refreshing indicator in the fragment */
        if (FeedFragment.mSwipeRefreshLayout != null) {
            FeedFragment.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    FeedFragment.mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * Based on the user's preferences, grabs information from the relevant providers and inserts
     * them into the list.
     *
     * @param itemList list of XML items which will be displayed as news items
     * @param context the relevant context
     */
    private static void syncSources(List<XMLParser.Item> itemList, Context context) {
        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_abc), context))
            insert(itemList, URL_ABC, PROVIDER_ABC, CODE_AUSTRALIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_aljazeera), context))
            insert(itemList, URL_ALJAZEERA, PROVIDER_ALJAZEERA, CODE_QATAR);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_angop), context))
            insert(itemList, URL_ANGOP, PROVIDER_ANGOP, CODE_ANGOLA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_arabnews), context))
            insert(itemList, URL_ARABNEWS, PROVIDER_ARABNEWS, CODE_SAUDIARABIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_bbc), context))
            insert(itemList, URL_BBC, PROVIDER_BBC, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_bianet), context))
            insert(itemList, URL_BIANET, PROVIDER_BIANET, CODE_TURKEY);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_bulawayo), context))
            insert(itemList, URL_BULAWAYO, PROVIDER_BULAWAYO, CODE_ZIMBABWE);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_breitbart), context))
            insert(itemList, URL_BREITBART, PROVIDER_BREITBART, CODE_USA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_colombiareports), context))
            insert(itemList, URL_COLOMBIAREPORTS, PROVIDER_COLOMBIAREPORTS, CODE_COLOMBIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_copenhagenpost), context))
            insert(itemList, URL_COPENHAGENPOST, PROVIDER_COPENHAGENPOST, CODE_DENMARK);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_dailynation), context))
            insert(itemList, URL_DAILYNATION, PROVIDER_DAILYNATION, CODE_KENYA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_dailysabah), context))
            insert(itemList, URL_DAILYSABAH, PROVIDER_DAILYSABAH, CODE_TURKEY);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_france24), context))
            insert(itemList, URL_FRANCE24, PROVIDER_FRANCE24, CODE_FRANCE);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_globalnews), context))
            insert(itemList, URL_GLOBALNEWS, PROVIDER_GLOBALNEWS, CODE_CANADA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_hindustantimes), context))
            insert(itemList, URL_HINDUSTANTIMES, PROVIDER_HINDUSTANTIMES, CODE_INDIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_hurriyet), context))
            insert(itemList, URL_HURRIYET, PROVIDER_HURRIYET, CODE_TURKEY);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_independent), context))
            insert(itemList, URL_INDEPENDENT, PROVIDER_INDEPENDENT, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_inquirer), context))
            insert(itemList, URL_INQUIRER, PROVIDER_INQUIRER, CODE_PHILIPPINES);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_irishtimes), context))
            insert(itemList, URL_IRISHTIMES, PROVIDER_IRISHTIMES, CODE_IRELAND);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_japantoday), context))
            insert(itemList, URL_JAPANTODAY, PROVIDER_JAPANTODAY, CODE_JAPAN);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_jerusalempost), context))
            insert(itemList, URL_JERUSALEMPOST, PROVIDER_JERUSALEMPOST, CODE_ISRAEL);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_khaleejtimes), context))
            insert(itemList, URL_KHALEEJTIMES, PROVIDER_KHALEEJTIMES, CODE_UAE);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_koreaherald), context))
            insert(itemList, URL_KOREAHERALD, PROVIDER_KOREAHERALD, CODE_SOUTHKOREA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_mercopress), context))
            insert(itemList, URL_MERCOPRESS, PROVIDER_MERCOPRESS, CODE_URUGUAY);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_nationalaccord), context))
            insert(itemList, URL_NATIONALACCORD, PROVIDER_NATIONALACCORD, CODE_NIGERIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_news24), context))
            insert(itemList, URL_NEWS24, PROVIDER_NEWS24, CODE_SOUTHAFRICA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_nypost), context))
            insert(itemList, URL_NYPOST, PROVIDER_NYPOST, CODE_USA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_nytimes), context))
            insert(itemList, URL_NYTIMES, PROVIDER_NYTIMES, CODE_USA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_nzherald), context))
            insert(itemList, URL_NZHERALD, PROVIDER_NZHERALD, CODE_NEWZEALAND);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_peoplesdaily), context))
            insert(itemList, URL_PEOPLESDAILY, PROVIDER_PEOPLESDAILY, CODE_CHINA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_pravda), context))
            insert(itemList, URL_PRAVDA, PROVIDER_PRAVDA, CODE_RUSSIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_presstv), context))
            insert(itemList, URL_PRESSTV, PROVIDER_PRESSTV, CODE_IRAN);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_radiopoland), context))
            insert(itemList, URL_RADIOPOLAND, PROVIDER_RADIOPOLAND, CODE_POLAND);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_reuters), context))
            insert(itemList, URL_REUTERS, PROVIDER_REUTERS, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_rt), context))
            insert(itemList, URL_RT, PROVIDER_RT, CODE_RUSSIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_rte), context))
            insert(itemList, URL_RTE, PROVIDER_RTE, CODE_IRELAND);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_sana), context))
            insert(itemList, URL_SANA, PROVIDER_SANA, CODE_SYRIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_shanghaidaily), context))
            insert(itemList, URL_SHANGHAIDAILY, PROVIDER_SHANGHAIDAILY, CODE_CHINA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_skynews), context))
            insert(itemList, URL_SKYNEWS, PROVIDER_SKYNEWS, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_spiegel), context))
            insert(itemList, URL_SPIEGEL, PROVIDER_SPIEGEL, CODE_GERMANY);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_sputnik), context))
            insert(itemList, URL_SPUTNIK, PROVIDER_SPUTNIK, CODE_RUSSIA);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_straitstimes), context))
            insert(itemList, URL_STRAITSTIMES, PROVIDER_STRAITSTIMES, CODE_SINGAPORE);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_tehrantimes), context))
            insert(itemList, URL_TEHRANTIMES, PROVIDER_TEHRANTIMES, CODE_IRAN);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_telegraph), context))
            insert(itemList, URL_TELEGRAPH, PROVIDER_TELEGRAPH, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_theguardian), context))
            insert(itemList, URL_THEGUARDIAN, PROVIDER_THEGUARDIAN, CODE_UNITEDKINGDOM);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_timesofisrael), context))
            insert(itemList, URL_TIMESOFISRAEL, PROVIDER_TIMESOFISRAEL, CODE_ISRAEL);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_unian), context))
            insert(itemList, URL_UNIAN, PROVIDER_UNIAN, CODE_UKRAINE);

        if (PreferenceUtils.getSource(context.getString(R.string.pref_key_washingtontimes),
                context))
            insert(itemList, URL_WASHINGTONTIMES, PROVIDER_WASHINGTONTIMES, CODE_USA);
    }

    /**
     * Method for syncing each individual country's news feed
     *
     * @param context the relevant context
     */
    private static void syncCountries(Context context) {
        List<XMLParser.Item> itemList = new ArrayList<>();

        /* Load the relevant country data based on the chosen country */
        switch (CountryFragment.COUNTRY_CODE) {
            case CODE_ANGOLA:
                for (int i = 0; i < URLS_ANGOLA.length; i++) {
                    insert(itemList, URLS_ANGOLA[i], PROVIDERS_ANGOLA[i], CODE_ANGOLA);
                }
                break;
            case CODE_AUSTRALIA:
                for (int i = 0; i < URLS_AUSTRALIA.length; i++) {
                    insert(itemList, URLS_AUSTRALIA[i], PROVIDERS_AUSTRALIA[i], CODE_AUSTRALIA);
                }
                break;
            case CODE_CANADA:
                for (int i = 0; i < URLS_CANADA.length; i++) {
                    insert(itemList, URLS_CANADA[i], PROVIDERS_CANADA[i], CODE_CANADA);
                }
                break;
            case CODE_CHINA:
                for (int i = 0; i < URLS_CHINA.length; i++) {
                    insert(itemList, URLS_CHINA[i], PROVIDERS_CHINA[i], CODE_CHINA);
                }
                break;
            case CODE_COLOMBIA:
                for (int i = 0; i < URLS_COLOMBIA.length; i++) {
                    insert(itemList, URLS_COLOMBIA[i], PROVIDERS_COLOMBIA[i], CODE_COLOMBIA);
                }
                break;
            case CODE_DENMARK:
                for (int i = 0; i < URLS_DENMARK.length; i++) {
                    insert(itemList, URLS_DENMARK[i], PROVIDERS_DENMARK[i], CODE_DENMARK);
                }
                break;
            case CODE_FRANCE:
                for (int i = 0; i < URLS_FRANCE.length; i++) {
                    insert(itemList, URLS_FRANCE[i], PROVIDERS_FRANCE24[i], CODE_FRANCE);
                }
                break;
            case CODE_GERMANY:
                for (int i = 0; i < URLS_GERMANY.length; i++) {
                    insert(itemList, URLS_GERMANY[i], PROVIDERS_GERMANY[i], CODE_GERMANY);
                }
                break;
            case CODE_INDIA:
                for (int i = 0; i < URLS_INDIA.length; i++) {
                    insert(itemList, URLS_INDIA[i], PROVIDERS_INDIA[i], CODE_INDIA);
                }
                break;
            case CODE_IRAN:
                for (int i = 0; i < URLS_IRAN.length; i++) {
                    insert(itemList, URLS_IRAN[i], PROVIDERS_IRAN[i], CODE_IRAN);
                }
                break;
            case CODE_IRELAND:
                for (int i = 0; i < URLS_IRELAND.length; i++) {
                    insert(itemList, URLS_IRELAND[i], PROVIDERS_IRELAND[i], CODE_IRELAND);
                }
                break;
            case CODE_ISRAEL:
                for (int i = 0; i < URLS_ISRAEL.length; i++) {
                    insert(itemList, URLS_ISRAEL[i], PROVIDERS_ISRAEL[i], CODE_ISRAEL);
                }
                break;
            case CODE_JAPAN:
                for (int i = 0; i < URLS_JAPAN.length; i++) {
                    insert(itemList, URLS_JAPAN[i], PROVIDERS_JAPAN[i], CODE_JAPAN);
                }
                break;
            case CODE_KENYA:
                for (int i = 0; i < URLS_KENYA.length; i++) {
                    insert(itemList, URLS_KENYA[i], PROVIDERS_KENYA[i], CODE_KENYA);
                }
                break;
            case CODE_NEWZEALAND:
                for (int i = 0; i < URLS_NEWZEALAND.length; i++) {
                    insert(itemList, URLS_NEWZEALAND[i], PROVIDERS_NEWZEALAND[i], CODE_NEWZEALAND);
                }
                break;

            case CODE_NIGERIA:
                for (int i = 0; i < URLS_NIGERIA.length; i++) {
                    insert(itemList, URLS_NIGERIA[i], PROVIDERS_NIGERIA[i], CODE_NIGERIA);
                }
                break;
            case CODE_PHILIPPINES:
                for (int i = 0; i < URLS_PHILIPPINES.length; i++) {
                    insert(itemList, URLS_PHILIPPINES[i], PROVIDERS_PHILIPPINES[i],
                            CODE_PHILIPPINES);
                }
                break;
            case CODE_POLAND:
                for (int i = 0; i < URLS_POLAND.length; i++) {
                    insert(itemList, URLS_POLAND[i], PROVIDERS_POLAND[i], CODE_POLAND);
                }
                break;
            case CODE_QATAR:
                for (int i = 0; i < URLS_QATAR.length; i++) {
                    insert(itemList, URLS_QATAR[i], PROVIDERS_QATAR[i], CODE_QATAR);
                }
                break;
            case CODE_RUSSIA:
                for (int i = 0; i < URLS_RUSSIA.length; i++) {
                    insert(itemList, URLS_RUSSIA[i], PROVIDERS_RUSSIA[i], CODE_RUSSIA);
                }
                break;
            case CODE_SAUDIARABIA:
                for (int i = 0; i < URLS_SAUDIARABIA.length; i++) {
                    insert(itemList, URLS_SAUDIARABIA[i], PROVIDERS_SAUDIARABIA[i],
                            CODE_SAUDIARABIA);
                }
                break;
            case CODE_SINGAPORE:
                for (int i = 0; i < URLS_SINGAPORE.length; i++) {
                    insert(itemList, URLS_SINGAPORE[i], PROVIDERS_SINGAPORE[i], CODE_SINGAPORE);
                }
                break;
            case CODE_SOUTHAFRICA:
                for (int i = 0; i < URLS_SOUTHAFRICA.length; i++) {
                    insert(itemList, URLS_SOUTHAFRICA[i], PROVIDERS_SOUTHAFRICA[i],
                            CODE_SOUTHAFRICA);
                }
                break;
            case CODE_SOUTHKOREA:
                for (int i = 0; i < URLS_SOUTHKOREA.length; i++) {
                    insert(itemList, URLS_SOUTHKOREA[i], PROVIDERS_SOUTHKOREA[i], CODE_SOUTHKOREA);
                }
                break;
            case CODE_SYRIA:
                for (int i = 0; i < URLS_SYRIA.length; i++) {
                    insert(itemList, URLS_SYRIA[i], PROVIDERS_SYRIA[i], CODE_SYRIA);
                }
                break;
            case CODE_TURKEY:
                for (int i = 0; i < URLS_TURKEY.length; i++) {
                    insert(itemList, URLS_TURKEY[i], PROVIDERS_TURKEY[i], CODE_TURKEY);
                }
                break;
            case CODE_UAE:
                for (int i = 0; i < URLS_UAE.length; i++) {
                    insert(itemList, URLS_UAE[i], PROVIDERS_UAE[i], CODE_UAE);
                }
                break;
            case CODE_UKRAINE:
                for (int i = 0; i < URLS_UKRAINE.length; i++) {
                    insert(itemList, URLS_UKRAINE[i], PROVIDERS_UKRAINE[i], CODE_UKRAINE);
                }
                break;
            case CODE_UNITEDKINGDOM:
                for (int i = 0; i < URLS_UNITEDKINGDOM.length; i++) {
                    insert(itemList, URLS_UNITEDKINGDOM[i], PROVIDERS_UNITEDKINGDOM[i],
                            CODE_UNITEDKINGDOM);
                }
                break;
            case CODE_URUGUAY:
                for (int i = 0; i < URLS_URUGUAY.length; i++) {
                    insert(itemList, URLS_URUGUAY[i], PROVIDERS_URUGUAY[i], CODE_URUGUAY);
                }
                break;
            case CODE_USA:
                for (int i = 0; i < URLS_USA.length; i++) {
                    insert(itemList, URLS_USA[i], PROVIDERS_USA[i], CODE_USA);
                }
                break;
            case CODE_ZIMBABWE:
                for (int i = 0; i < URLS_ZIMBABWE.length; i++) {
                    insert(itemList, URLS_ZIMBABWE[i], PROVIDERS_ZIMBABWE[i], CODE_ZIMBABWE);
                }
                break;
            default:
                break;
        }

        /* Add the XML data into ContentValues */
        List<ContentValues> valueList = new ArrayList<>();
        ContentValues values;

        for (XMLParser.Item item : itemList) {
            values = new ContentValues();
            values.put(DatabaseContract.CountriesEntry.COLUMN_TITLE, item.title);
            values.put(DatabaseContract.CountriesEntry.COLUMN_LINK, item.link);
            values.put(DatabaseContract.CountriesEntry.COLUMN_DESCRIPTION, item.description);
            values.put(DatabaseContract.CountriesEntry.COLUMN_ENCLOSURE, item.enclosure);
            long pubDateInMillis = DateUtils.convertDateToMillis(item.pubDate);
            values.put(DatabaseContract.CountriesEntry.COLUMN_MEDIACONTENT, item.mediaContent);
            values.put(DatabaseContract.CountriesEntry.COLUMN_PUBDATE, pubDateInMillis);
            values.put(DatabaseContract.CountriesEntry.COLUMN_PROVIDER, item.provider);
            values.put(DatabaseContract.CountriesEntry.COLUMN_COUNTRY, item.country);
            valueList.add(values);
        }

        /* Delete the previously stored information about the current country */
        ContentResolver resolver = context.getContentResolver();
        String selection = DatabaseContract.CountriesEntry.COLUMN_COUNTRY + "=?";
        String[] selectionArgs = {CountryFragment.COUNTRY_CODE};
        resolver.delete(DatabaseContract.CountriesEntry.CONTENT_URI, selection, selectionArgs);

        /* Insert the new data for the country */
        resolver.bulkInsert(DatabaseContract.CountriesEntry.CONTENT_URI,
                valueList.toArray(new ContentValues[valueList.size()]));

        /* Hide the refreshing indicator in the fragment */
        if (CountryFragment.mSwipeRefreshLayout != null) {
            CountryFragment.mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    CountryFragment.mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * Send the relevant URLs to the XML Parser
     *
     * @param itemList the list of relevant providers
     * @param url the relevant URL
     * @param provider the relevant provider
     * @param country the relevant country
     */
    private static void insert(List<XMLParser.Item> itemList, String url, String provider,
                               String country) {
        try {
            itemList.addAll(NetworkUtils.loadXmlFromNetwork(url, provider, country));
        } catch (XmlPullParserException | IOException ex) {
            ex.getMessage();
        }
    }
}
