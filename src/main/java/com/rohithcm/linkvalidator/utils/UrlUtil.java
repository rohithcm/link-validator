package com.rohithcm.linkvalidator.utils;

import com.rohithcm.linkvalidator.LinkValidatorMain;
import com.rohithcm.linkvalidator.exceptions.LinkFormationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rohithcm on 21/01/16.
 */
public class UrlUtil {
    private static Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    public static URL getBaseURLFromString(final String baseUrlString) {
        URL url;
        try {
                url = new URL(baseUrlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(baseUrlString+" forming invalid url");
            throw new LinkFormationException(baseUrlString+" forming invalid url");
        }
        return url;
    }

    public static URL getURLFromString(final String urlString) {
        URL url;
        try {
            String baseUrlString = LinkValidatorMain.baseUrl.toString();
            if(urlString.startsWith(baseUrlString))
                url = new URL(urlString);
            else
                url = new URL(baseUrlString + urlString);
        } catch (MalformedURLException e) {
            logger.error(urlString+" forming invalid url");
            throw new LinkFormationException(urlString+" forming invalid url");
        }
        return url;
    }
}
