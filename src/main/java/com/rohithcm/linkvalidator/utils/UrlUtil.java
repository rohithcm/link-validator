package com.rohithcm.linkvalidator.utils;

import com.rohithcm.linkvalidator.exceptions.LinkFormationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rohithcm on 21/01/16.
 * Utility to get URL instances from String input
 */
public class UrlUtil {
    private static Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    public static URL getBaseURLFromString(final String baseUrlString) {
        URL url;
        try {
            url = new URL(baseUrlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(baseUrlString + " forming invalid url");
            throw new LinkFormationException(baseUrlString + " forming invalid url");
        }
        return url;
    }

    public static URL getURLFromString(final String parentUrlString, final String urlString) {
        URL url;
        try {
            if (urlString.startsWith("http") || urlString.startsWith("https"))
                url = new URL(urlString);
            else
                url = new URL(parentUrlString + urlString);
        } catch (MalformedURLException e) {
            logger.error(urlString + " forming invalid url");
            throw new LinkFormationException(urlString + " forming invalid url");
        }
        return url;
    }
}
