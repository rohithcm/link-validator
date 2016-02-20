package com.rohithcm.linkvalidator;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.rohithcm.linkvalidator.beans.PropertyBean;
import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.URLStatus;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import com.rohithcm.linkvalidator.utils.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by rohithcm on 21/01/16.
 * Class containing functions to be handled by each thread.
 */
public class ValidatorWorkerThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ValidatorWorkerThread.class);

    private WebClient webClientObj = new WebClient();
    private final int PAGE_TIMEOUT = 5000, VALID_RESPONSE_CODE = 200;
    private String additionalComments = "";

    public ValidatorWorkerThread() {
        webClientObj.setJavaScriptEnabled(false);
        webClientObj.setThrowExceptionOnScriptError(false);
        webClientObj.setTimeout(PAGE_TIMEOUT);
    }

    private boolean isLinkValid(final HtmlPage htmlPage) {
        return (htmlPage.getWebResponse().getStatusCode() == VALID_RESPONSE_CODE) && !isErrorStringPresent(htmlPage);
    }

    private boolean isErrorStringPresent(final HtmlPage htmlPage) {
        for (String errorString : PropertyBean.getInstance().getErrorStrings())
            if (htmlPage.asText().contains(errorString)) {
                additionalComments = "Custom error string '" + errorString + "' found in the page";
                return true;
            }
        return false;
    }

    private void populateURLListWithChildren(final URL parentUrl, ValidationDepth newValidationDepth, HtmlPage htmlPage) {
        if (!newValidationDepth.equals(ValidationDepth.INVALID) && // if the level is invalid
                (newValidationDepth.getLevel() <= PropertyBean.getInstance().validationDepth.getLevel())) {  // If the level exceeds user input
            List<HtmlAnchor> childrenURL = htmlPage.getAnchors();
            logger.info("Number of child urls identified : " + childrenURL.size());
            for (HtmlAnchor child : childrenURL) {
                URL childUrl = UrlUtil.getURLFromString(parentUrl.toString(), child.getHrefAttribute());
                // Verify if child url is already identified earlier.
                if (ValidatorWorkerFactory.urlHistory.containsKey(childUrl))
                    continue;
                ValidatorWorkerFactory.urlHistory.put(childUrl, true);
                logger.info("Child url identified : " + child);
                ValidatorWorkerFactory.urlAggregator.offer(new URLWrapper(childUrl, parentUrl, newValidationDepth));
            }
        }
    }

    public void run() {
        if (ValidatorWorkerFactory.urlAggregator.peek() == null)   // No URLs to be validated
            return;

        final URLWrapper urlWrapper = ValidatorWorkerFactory.urlAggregator.poll();
        final URL urlToValidate = urlWrapper.getUrl();
        final URL parentUrl = urlWrapper.getParentUrl();
        final ValidationDepth validationDepth = urlWrapper.getValidationDepth();
        final ValidationDepth nextValidationDepth = ValidationDepth.getValidationLevel(validationDepth.getLevel() + 1);

        // initializing the status to fail
        URLStatus urlStatus = URLStatus.FAIL;
        int statusCode = 0;
        logger.info("Proceed to validate the URL : " + urlToValidate);
        try {
            final HtmlPage htmlPage = webClientObj.getPage(urlToValidate);
            statusCode = htmlPage.getWebResponse().getStatusCode();
            if (isLinkValid(htmlPage)) {
                urlStatus = URLStatus.PASS;
                logger.info(urlToValidate + " is a valid url");
                populateURLListWithChildren(parentUrl, nextValidationDepth, htmlPage);
            } else {
                logger.info(urlToValidate + " is an invalid url");
            }
            logger.info("Cleaning up this htmlpage");
            htmlPage.cleanUp();
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.info(urlToValidate + " is an invalid url");
        }
        appendPageStatusToReport(urlToValidate.toString(), parentUrl.toString(), validationDepth, urlStatus, statusCode);
    }

    private void appendPageStatusToReport(final String url, final String parentUrl, final ValidationDepth validationDepth, final URLStatus status, final int statusCode) {
        logger.info("Proceed to update URL status to the HTML Report");
        StringBuffer sb = new StringBuffer(ValidatorWorkerFactory.getHtmlReportThreadSafe());
        sb.append(ReportUtil.appendPageStatusToReport(url, parentUrl, validationDepth, status, statusCode, additionalComments));
        ValidatorWorkerFactory.setHtmlReportThreadSafe(sb.toString());
    }


}
