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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class ImageUtils {

    /* Bitmap to save the images into */
    private static Bitmap mBitmap;

    /* Used to keep count of how many images an article has */
    private static int mImageCode;

    private static Context mContext;

    /* Path to save the images in internal storage */
    public static final String IMAGE_PATH = "/altmediahub/images";

    /**
     * Download the article thumbnail for display in the bookmarks feed
     *
     * @param imageURLString the URL to download the image from in String format
     * @param fileString the location to save the downloaded file
     * @param context the relevant context
     */
    public static void downloadThumbnail(String imageURLString, String fileString,
                                         Context context) {
        mContext = context;

        /* Format the URL string into an URL object */
        URL imageURL = null;
        try {
            imageURL = new URL(imageURLString);
        } catch (MalformedURLException ex) {
            ex.getMessage();
        }

        if (imageURL != null) {
            try {

                /* Connect to the URL and save the image */
                HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                conn.disconnect();
                saveThumbnail(fileString, bitmap);
            } catch (IOException ex) {
                ex.getMessage();
            }
        }
    }

    /**
     * Save the downloaded thumbnail bitmap to the relevant file location
     *
     * @param fileString the location to save the file in
     * @param bitmap the downloaded bitmap
     */
    public static void saveThumbnail(String fileString, Bitmap bitmap) {
        File filename;
        try {
            String path = mContext.getFilesDir().toString();

            /* Create the directory */
            new File(path + IMAGE_PATH).mkdirs();

            /* Open an output stream in the appropriate location */
            filename = new File(path + IMAGE_PATH + "/" + fileString + ".jpg");
            FileOutputStream fos = new FileOutputStream(filename);

            /* Compress the image as a JPEG with 90% quality */
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

            /* Finish using the output stream */
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    /**
     * Retrieve every image from the relevant article text
     *
     * @param articleText the relevant article
     * @param title the headline for the article
     * @param context the relevant context
     * @return the new article text with the image srcs turned into local files
     */
    public static String getArticleImages(String articleText, String title, Context context) {
        mContext = context;

        /* Keep count of how many images in the article */
        mImageCode = 0;

        String fileString = title;

        /* Eliminate all characters that are not letters or digits */
        Pattern banned = Pattern.compile("([^A-Za-z0-9])", Pattern.CASE_INSENSITIVE);
        fileString = banned.matcher(fileString).replaceAll("");

        /* Set the filename based on the length of the title to avoid duplicates */
        if (fileString.length() > 30)
            fileString = fileString.toLowerCase().substring(0, 30);
        else
            fileString = fileString.toLowerCase().substring(0, fileString.length()-1);

        Document doc = Jsoup.parse(articleText);

        Elements images = doc.getElementsByTag("img");
        for (Element element : images) {
            if (element.hasAttr("src")) {

                /* Replace the img srcs in the article with their new local file names */
                articleText = articleText.replace(element.attr("src"),
                        "file://" + downloadArticleImage(element.attr("src"), fileString));
            }
        }
        return articleText;
    }

    /**
     * Download each image found in the article
     *
     * @param imageUrlString the URL of the relevant image in String format
     * @param fileString location to save the file
     * @return the name of the new file
     */
    private static String downloadArticleImage(String imageUrlString, String fileString) {

        /* Format the URL string into an URL object */
        URL imageURL = null;
        try {
            imageURL = new URL(imageUrlString);
        } catch (MalformedURLException ex) {
            ex.getMessage();
        }

        try {

            /* Abandon if the URL happens to be a FileURLConnection */
            if (imageURL != null && imageURL.openConnection() instanceof HttpURLConnection) {
                HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                conn.disconnect();
                return saveArticleImage(fileString, bitmap);
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
        return "";
    }

    /**
     * Save the downloaded image bitmap and return the name of the new file
     *
     * @param fileString the location of the file
     * @param bitmap the bitmap to be downloaded
     * @return the absolute path of the new file
     */
    private static String saveArticleImage(String fileString, Bitmap bitmap) {
        File filename;

        try {
            String path = mContext.getFilesDir().toString();

            /* Create the directory */
            new File(path + IMAGE_PATH).mkdirs();

            /* Open an output stream in the appropriate location */
            filename = new File(path + IMAGE_PATH + "/" + fileString + mImageCode++ + ".jpg");
            FileOutputStream fos = new FileOutputStream(filename);

            /* Compress the image as a JPEG with 90% quality */
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

            /* Finish using the output stream */
            fos.flush();
            fos.close();
            return filename.getAbsolutePath();
        } catch (Exception ex ) {
            ex.getMessage();
        }

        return "";
    }

    /**
     * Delete the saved thumbnail if it exists when the bookmark is deleted
     *
     * @param filename the location of the thumbnail
     */
    public static void deleteThumbnail(String filename) {

        /* Remove any characters which are not letters or digits */
        Pattern banned = Pattern.compile("([^A-Za-z0-9])", Pattern.CASE_INSENSITIVE);
        filename = banned.matcher(filename).replaceAll("");

        /* Set the filename based on the length of the title to avoid duplicates */
        if (filename.length() > 30)
            filename = filename.toLowerCase().substring(0, 30);
        else
            filename = filename.toLowerCase().substring(0, filename.length()-1);

        File file = new File(filename);
        if (file.exists()) file.delete();
    }

    /**
     * Delete the saved image files when the bookmark is deleted
     *
     * @param articleText the HTML for the bookmark
     */
    public static void deleteImages(String articleText) {
        Document doc = Jsoup.parse(articleText);

        Elements images = doc.getElementsByTag("img");
        for (Element image : images) {
            String filename = image.attr("src");
            File file = new File(filename);
            file.delete();
        }
    }
}
