package com.rohithcm.linkvalidator;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.rohithcm.linkvalidator.beans.HTMLTableRow;
import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.URLStatus;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import com.rohithcm.linkvalidator.utils.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by rohithcm on 21/01/16.
 */
public class ValidatorWorkerThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(ValidatorWorkerThread.class);
    private static HTMLTableRow htmlTableRow;
    private WebClient webClientObj = new WebClient(BrowserVersion.INTERNET_EXPLORER_7);

    public ValidatorWorkerThread() {
        webClientObj.setThrowExceptionOnScriptError(false);
        webClientObj.setTimeout(1000);
    }

    private boolean isLinkValid(final HtmlPage htmlPage) {
        return (htmlPage.getWebResponse().getStatusCode() == 200) && !isErrorStringPresent(htmlPage);
    }

    private boolean isErrorStringPresent(final HtmlPage htmlPage) {
        final String errorString = "ERRORVALUE";
        if (htmlPage.asText().contains(errorString)) {
            htmlTableRow.setComments("Error String " + errorString + " is found in the Page");
            return true;
        }
        return false;
    }

    //private void populateURLListWithChildren(List<URLWrapper> urls, final URL parentUrl, ValidationDepth newValidationDepth, HtmlPage htmlPage) {
    private void populateURLListWithChildren(final URL parentUrl, ValidationDepth newValidationDepth, HtmlPage htmlPage) {
        if (!newValidationDepth.equals(ValidationDepth.INVALID) && // if the level is invalid
                (newValidationDepth.getLevel() > LinkValidatorMain.validationDepth.getLevel())) {  // If the level exceeds user input
            List<HtmlAnchor> childrenURL = htmlPage.getAnchors();
            for (HtmlAnchor child : childrenURL) {
                URL childUrl = UrlUtil.getURLFromString(child.getHrefAttribute());
                //urls.add(new URLWrapper(childUrl, parentUrl, newValidationDepth));
                TriggerValidation.urlAggregator.offer(new URLWrapper(childUrl, parentUrl, newValidationDepth));
            }
        }
    }

    @Override
    public void run() {
        /*List<URLWrapper> threadSafeUrlList = TriggerValidation.getURLsThreadSafe();
        URLWrapper urlWrapper = threadSafeUrlList.get(0);
        threadSafeUrlList.remove(urlWrapper);*/
        if(TriggerValidation.urlAggregator.peek() == null)
            return;
        URLWrapper urlWrapper = TriggerValidation.urlAggregator.poll();
        final URL urlToValidate = urlWrapper.getUrl();
        final URL parentUrl = urlWrapper.getParentUrl();
        final ValidationDepth validationDepth = urlWrapper.getValidationDepth();
        final ValidationDepth newValidationDepth = ValidationDepth.getValidationLevel(validationDepth.getLevel() + 1);
        URLStatus urlStatus = URLStatus.FAIL;

        logger.debug(urlToValidate + " is being validated now");
        htmlTableRow = new HTMLTableRow(urlToValidate.toString(), parentUrl.toString(), validationDepth, URLStatus.FAIL, "");

        HtmlPage htmlPage;
        try {
            htmlPage = webClientObj.getPage(urlToValidate);
            if (isLinkValid(htmlPage)) {
                urlStatus = URLStatus.PASS;
                htmlTableRow.setStatus(URLStatus.PASS);
                logger.debug(urlToValidate + " is a valid url");
                //populateURLListWithChildren(threadSafeUrlList, parentUrl, newValidationDepth, htmlPage);
                populateURLListWithChildren(parentUrl, newValidationDepth, htmlPage);
            } else
                logger.debug(urlToValidate + " is an invalid url");
            htmlPage.cleanUp();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        appendPageStatusToReport();
    }

    private void appendPageStatusToReport() {
        StringBuilder sb = new StringBuilder(TriggerValidation.getHtmlReportThreadSafe());
        sb.append(ReportUtil.appendPageStatusToReport(htmlTableRow));
        TriggerValidation.setHtmlReportThreadSafe(sb.toString());
    }


}
