package com.rohithcm.linkvalidator.utils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Rohith on 20-01-2016.
 */
public class PageUtil {
    private static Logger logger = LogManager.getLogger(PageUtil.class);

    public List<WebElement> getURLTags(final URL url){
        WebClient webClientObj = new WebClient();
        HtmlPage htmlPageObj;
        try {
            htmlPageObj = webClientObj.getPage(url);
            htmlPageObj.getPage().
            htmlPageObj.getAllHtmlChildElements().
            int htmlResponseCode = htmlPageObj.getWebResponse().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
