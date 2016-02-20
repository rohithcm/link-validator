package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.beans.PropertyBean;
import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rohithcm on 21/01/16.
 * Class containing executor to trigger ValidationWorkerThreads
 */
public class ValidatorWorkerFactory {

    public static LinkedBlockingDeque<URLWrapper> urlAggregator = new LinkedBlockingDeque<URLWrapper>();
    public static ConcurrentHashMap<URL, Boolean> urlHistory = new ConcurrentHashMap<URL, Boolean>();
    private static AtomicReference<String> htmlReport;
    private static Logger logger = LoggerFactory.getLogger(ValidatorWorkerFactory.class);

    /**
     * Triggering point for ValidationWorkerThreads
     */
    public static void trigger() {
        initSynchronizedResources();

        ExecutorService executor = Executors.newCachedThreadPool();
        executeValidationThreads(executor);

        afterValidation();
    }

    private static void afterValidation() {
        MultiThreadedHttpConnectionManager.shutdownAll();
        addTotalLinksValidatedToReport();
        addHTMLReportTail();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private static void executeValidationThreads(ExecutorService executor) {
        logger.info("========== Link Validation begins ===============");

        // Thread execution until the URL to Validate list is empty
        while (!urlAggregator.isEmpty()) {
            logger.info("URL list identified as not empty");
            if (executor.isTerminated())
                executor = Executors.newCachedThreadPool();
            for (int i = 0; i < urlAggregator.size(); i++) {
                Runnable validatorWorker = new ValidatorWorkerThread();
                executor.execute(validatorWorker);
            }
            executor.shutdown();
            // Wait till executor is terminated.
            while (!executor.isTerminated()) ;
            // Loop is repeated if url list is populated with new entries
        }

        logger.info("Finished all validation threads");
    }

    public static void initSynchronizedResources() {
        logger.info("Adding Table Headers to HTML Report");
        htmlReport = new AtomicReference<String>(ReportUtil.getHtmlTableHead());
        logger.info("Initializing the url list with the base urls");
        for (URL baseUrl : PropertyBean.getInstance().getBaseUrls())
            urlAggregator.offer(new URLWrapper(baseUrl, baseUrl, ValidationDepth.ONE));
    }

    public static String getHtmlReportThreadSafe() {
        return htmlReport.get();
    }

    public static void setHtmlReportThreadSafe(final String value) {
        htmlReport.set(value);
    }

    public static void addHTMLReportTail() {
        logger.info("Finished appending to the HTML Report");
        htmlReport.set(htmlReport.get() + ReportUtil.getHtmlTableTail());
    }

    public static void addTotalLinksValidatedToReport() {
        final int totalURLcount = urlHistory.size() + PropertyBean.getInstance().getBaseUrls().size();
        logger.info("Total links validated : " + totalURLcount);
        htmlReport.set(ReportUtil.addURLsCountToReport(totalURLcount) + htmlReport.get());
    }
}
