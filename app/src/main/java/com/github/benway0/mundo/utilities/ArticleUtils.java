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

import com.github.benway0.mundo.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

public class ArticleUtils {

    /* Article descriptions/introductions featured on some news sites */
    private static final String[] ARTICLE_DESC = {"article__summary summary ", "b-article__lead",
        "box-titulo", "bLead"};

    /* Classes in which the article text is contained for each source */
    private static final String[] ARTICLE_TEXT = {"article__text text  js-mediator-article",
        "b-article__text", "entry-content", "article-body", "arcticle-content",
        "article-content", "body-copy", "wb_12 wb_12b clear", "column-7 post", "entry",
        "content-page", "txtInWrapper", "NewsDetail", "detail_content detail_content_news",
        "hide-for-small-only", "item-text", "inner-post-entry", "story-details",
        "article section", "full-text-noticia", "td-post-content", "articleStyle", "bArt",
        "article-section clearfix", "post_container", "article-long", "odd field-item",
        "text-large mb-40", "content_view", "article_align", "article_bodycopy",
        "articlepage_content_zz", "article-text", "gnca-article-story-txt", "story-body__inner",
        "sdc-news-story-article", "text-wrapper", "story theme-main", "content__article-body",
        "story", "article__content", "ArticleBody_body_2ECha"};

    /* IDs in which the article text is contained for some sources */
    private static final String[] ARTICLE_TEXT_IDS = {"article-text", "article-body", "story",
        "article-content", "article"};

    /* Strip articles of any unnecessary classes that clog up the reading experience */
    private static String[] BLACKLIST = {"related-item", "post-thumbnail", "block-head",
        "ShareIt ShareItTop tmz", "yeniShare top", "tw", "g-plusone", "pin", "tofb", "pv-gallery",
        "slider lazy slidephotovideo detailslider", "special-block", "show-for-small-only",
        "inline-content photo full", "inline-content story left", "inline-content map left",
        "inline-caption", "td-post-featured-image", "sharedaddy", "jp-relatedposts",
        "img-responsive", "imgWithMargin", "expt1", "smallPhoto",
        "article-function-social-media", "post_img", "article-function-box", "nl2go",
        "article-action", "sharebar-social-part", "sharebar-internal-part", "nlsubscribe-block",
        "wp-image-233596", "size-large", "swiper_most_shared", "wp-caption", "pb-ad-container",
        "news_listing_detail", "story-meta", "media-viewer-candidate", "caption-text",
        "visually-hidden", "error", "hidden", "image-and-copyright-container", "sharetools",
        "media-caption", "share", "story-body__h1", "container grid-mod-gallery",
        "full-gallery", "content-body story", "article__socials", "social-block",
        "social-block__share", "facebookLike section", "comments-block__init",
        "comments-block__main", "sp-lazyload", "sp-media-asset__caption", "related-articles",
        "comments-section", "most-commented", "pb-f-article-related-articles",
        "sdc-article-video__inner", "mode-embed_horizontal_related_story_q",
        "articleBodyImage section", "headline__heading", "media-with-caption"};

    /* Stylesheet code for the article */
    public static String CSS =  "<!DOCTYPE html>" +
            "<head><style type=\"text/css\">" +
            "body { background-color: #FFFFFF; max-width: 100%; }" +
            "img.imageclass { width: 100%; max-width: 100%; height: auto; }" +
            "a { color: #D81B60; }" +
            "a:visited { color: #A00037; } " +
            "p.date { color: #424242; }" +
            "iframe { width: 100%; }" +
            "button { background-color: #D81B60; border: none; color: #FFFFFF; text-align: center; }" +
            "blockquote { border-left: thick solid #B0B0B0; background-color: #E0E0E0; padding: 0.5em; }" +
            "</style><meta name=\"viewport\" content=\"width=device-width\"/></head><body>";

    private static Context mContext;

    public static String extract(String url, Context context) throws IOException {
        mContext = context;
        return extract(Jsoup.connect(url).get());
    }

    /**
     * Extract and return the article text from the document.
     *
     * @param doc the relevant article HTML
     * @return the part of the document which contains the article
     */
    private static String extract(Document doc) {
        if (doc == null) throw new NullPointerException("No document found");

        /* Strip the document of all unnecessary tags */
        strip(doc);

        /* Append the CSS information */
        String result = CSS;

        /* Find out the display image preference */
        boolean imagesOn = PreferenceUtils.imagesOn(mContext);
        boolean imagesWifi = PreferenceUtils.imagesWifi(mContext);
        boolean wifiOn = PreferenceUtils.isWifiConnected(mContext);

        if (!imagesOn || (imagesWifi && !wifiOn)) {

            /* If images are set to off or set to WiFi with no available connection, strip them */
            Elements elements = doc.getElementsByTag("img");
            for (Element element : elements) {
                element.remove();
            }

            /* Strip iframe components such as videos, facebook posts etc. */
            elements = doc.getElementsByTag("iframe");
            for (Element element : elements) {
                element.remove();
            }
        } else {

            /* Append the article image if it has one */
            if (getImage(doc) != null) {
                result = result + "<img src=\"" + getImage(doc) + "\" width=100%><br />";
            }
            cleanImages(doc);
        }

        return getArticle(doc, result);
    }

    /**
     * Strip the document of all irrelevant tags and classes
     *
     * @param doc the relevant article HTML
     * @return the HTML stripped of unnecessary tags and classes
     */
    private static Document strip(Document doc) {

        /* Find and remove all scripts */
        Elements scripts = doc.getElementsByTag("script");
        for (Element element : scripts)
            element.remove();

        /* Cycle through blacklisted classes and remove them */
        for (String s : BLACKLIST) {
            Elements elements = doc.getElementsByClass(s);
            for (Element element : elements) {
                element.remove();
            }
        }

        return doc;
    }

    /**
     * Find the featured article image from the document metas if it exists
     *
     * @param doc the relevant article HTML
     * @return the featured article image
     */
    private static String getImage(Document doc) {
        Elements metas = doc.head().select("meta");
        for (Element element : metas) {
            if (element.hasAttr("property") && element.attr("property").equals("og:image")) {
                return element.attr("content").toString();
            }
        }
        return null;
    }

    /**
     * Find all images in the article and apply our own custom class so they display clean
     *
     * @param doc the relevant article HTML
     * @return the cleaned up HTML
     */
    private static Document cleanImages(Document doc) {
        Elements images = doc.getElementsByTag("img");
        for (Element element : images) {
            element.addClass("imageclass");
        }
        return doc;
    }

    /**
     * Cycle through the article and grab the relevant text from the document
     *
     * @param doc the relevant article HTML
     * @param result the resulting article text
     * @return result
     */
    private static String getArticle(Document doc, String result) {
        Elements elements = doc.getAllElements();

        /* Cycle through to see whether the article has introductory text */
        for (String s : ARTICLE_DESC) {
            if (elements.hasClass(s)) {
                elements = doc.getElementsByClass(s);

                /* Apply bold style and a new line */
                result = result + "<br /><b>" + elements.get(0).toString() + "</b>";
            }
        }

        /* Whether we have found the article text or not */
        boolean found = false;

        elements = doc.getAllElements();

        /* Cycle through all the possible classes and see if we can find the article text */
        for (String s : ARTICLE_TEXT) {
            if (elements.hasClass(s)) {
                elements = doc.getElementsByClass(s);
                result = result + elements.get(0).toString();

                /* No need to search IDs if we already found it */
                found = true;
                break;
            }
        }

        /* Sometimes the website uses an id instead of class to identify the article section */
        if (!found) {
            for (String s : ARTICLE_TEXT_IDS) {
                if (doc.getElementById(s) != null) {
                    Element element = doc.getElementById(s);
                    result = result + element.toString();
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Add some final cleanup to the article text
     * @param content
     * @return
     */
    public static String clean(String content, String link) {

        /* Find broken links */
        Pattern brokenLinks = Pattern.compile("\\s+(href|src)=(\"|')//", Pattern.CASE_INSENSITIVE);

        /* Find unnecessary white space */
        Pattern trim = Pattern.compile("(\\\\s*<br\\\\s*[/]*>\\\\s*){3,}",
                Pattern.CASE_INSENSITIVE);

        /* Fix the broken links */
        if (content != null) {
            content = brokenLinks.matcher(content).replaceAll(" $1=$2http://");
            content = trim.matcher(content).replaceAll("<br /><br />");
        }

        /* Add a button to send the user to the original website */
        String goToWebsite =
                "<br /><br /><p align=\"center\"><a href=\"" + link + "\" target=\"_blank\">" +
                mContext.getString(R.string.go_to_website) + "</a></p>";

        /* Add white space to the end of the article so it doesn't conflict with the fab */
        return content + goToWebsite + "<br /><br /><br /><br /></body></html>";
    }
}
