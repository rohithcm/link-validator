package com.rohithcm.linkvalidator;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
 */
public class ValidatorWorkerThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ValidatorWorkerThread.class);

    private WebClient webClientObj = new WebClient();
    private final int PAGE_TIMEOUT = 5000, VALID_RESPONSE_CODE = 200;

    public ValidatorWorkerThread() {
        webClientObj.setJavaScriptEnabled(false);
        webClientObj.setThrowExceptionOnScriptError(false);
        webClientObj.setTimeout(PAGE_TIMEOUT);
    }

    private boolean isLinkValid(final HtmlPage htmlPage) {
        return (htmlPage.getWebResponse().getStatusCode() == VALID_RESPONSE_CODE) && !isErrorStringPresent(htmlPage);
    }

    private boolean isErrorStringPresent(final HtmlPage htmlPage) {
        final String errorString = "ERRORVALUE";
        if (htmlPage.asText().contains(errorString)) {
            return true;
        }
        return false;
    }

    private void populateURLListWithChildren(final URL parentUrl, ValidationDepth newValidationDepth, HtmlPage htmlPage) {
        if (!newValidationDepth.equals(ValidationDepth.INVALID) && // if the level is invalid
                (newValidationDepth.getLevel() <= LinkValidatorMain.validationDepth.getLevel())) {  // If the level exceeds user input
            List<HtmlAnchor> childrenURL = htmlPage.getAnchors();
            logger.info("Number of child urls identified : " + childrenURL.size());
            for (HtmlAnchor child : childrenURL) {
                URL childUrl = UrlUtil.getURLFromString(child.getHrefAttribute());
                if (TriggerValidation.urlSet.containsKey(childUrl))
                    continue;
                //urls.add(new URLWrapper(childUrl, parentUrl, newValidationDepth));
                TriggerValidation.urlSet.put(childUrl, true);
                logger.info("Child url identified : " + child);
                TriggerValidation.urlAggregator.offer(new URLWrapper(childUrl, parentUrl, newValidationDepth));
            }
        }
    }

    @Override
    public void run() {
        if (TriggerValidation.urlAggregator.peek() == null)   // No URLs to be validated
            return;

        final URLWrapper urlWrapper = TriggerValidation.urlAggregator.poll();
        final URL urlToValidate = urlWrapper.getUrl();
        final URL parentUrl = urlWrapper.getParentUrl();
        final ValidationDepth validationDepth = urlWrapper.getValidationDepth();
        final ValidationDepth nextValidationDepth = ValidationDepth.getValidationLevel(validationDepth.getLevel() + 1);

        // initializing the status to fail
        URLStatus urlStatus = URLStatus.FAIL;

        logger.info("Proceed to validate the URL : " + urlToValidate);
        try {
            final HtmlPage htmlPage = webClientObj.getPage(urlToValidate);
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
        appendPageStatusToReport(urlToValidate.toString(), parentUrl.toString(), validationDepth, urlStatus, "");
    }

    private void appendPageStatusToReport(final String url, final String parentUrl, final ValidationDepth validationDepth, final URLStatus status, final String comment) {
        logger.info("Proceed to append HTML status");
        StringBuilder sb = new StringBuilder(TriggerValidation.getHtmlReportThreadSafe());
        //sb.append(ReportUtil.appendPageStatusToReport(htmlTableRow.get()));
        sb.append(ReportUtil.appendPageStatusToReport(url, parentUrl, validationDepth, status, comment));
        TriggerValidation.setHtmlReportThreadSafe(sb.toString());
        logger.info("Appended HTML status");
    }


}
