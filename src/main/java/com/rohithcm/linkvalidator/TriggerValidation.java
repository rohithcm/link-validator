package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.beans.HTMLTableRow;
import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
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
 */
public class TriggerValidation {

    public static ThreadLocal<HTMLTableRow> htmlTableRow;
    public static LinkedBlockingDeque<URLWrapper> urlAggregator = new LinkedBlockingDeque<URLWrapper>();
    public static ConcurrentHashMap<URL, Boolean> urlSet = new ConcurrentHashMap<URL, Boolean>();
    private static AtomicReference<String> htmlReport;
    private static Logger logger = LoggerFactory.getLogger(TriggerValidation.class);

    public static void trigger(final URL baseURL) {
        initSynchronizedResources();

        ExecutorService executor = Executors.newCachedThreadPool();
        executeThreads(executor);

        addHTMLReportTail();
    }

    private static void executeThreads(ExecutorService executor) {

        while (!urlAggregator.isEmpty()) {
            logger.info("URL list identified as not empty");
            if (executor.isTerminated())
                executor = Executors.newCachedThreadPool();
            for (int i = 0; i < urlAggregator.size(); i++) {
                Runnable validatorWorker = new ValidatorWorkerThread();
                executor.execute(validatorWorker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) ;
        }

        logger.info("Finished all threads");
    }

    public static void initSynchronizedResources() {
        logger.info("Adding Table Headers to HTML Report");
        htmlReport = new AtomicReference<String>(ReportUtil.getHtmlTableHead().toString());
        logger.info("Initializing the url list with the base url");
        urlAggregator.offer(new URLWrapper(LinkValidatorMain.baseUrl, LinkValidatorMain.baseUrl, ValidationDepth.ONE));
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
}
