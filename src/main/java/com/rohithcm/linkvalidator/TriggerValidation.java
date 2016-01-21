package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import com.rohithcm.linkvalidator.utils.UrlUtil;
import sun.net.util.URLUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rohithcm on 21/01/16.
 */
public class TriggerValidation {

    private static ThreadLocal<List<URLWrapper>> urlAggregator;
    private static ThreadLocal<String> htmlReport;

    public static void trigger(final URL baseURL) {
        initThreadLocals();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        while (!urlAggregator.get().isEmpty()) {
            Runnable validatorWorker = new ValidatorWorkerThread();
            executor.execute(validatorWorker);
        }
        addHTMLReportTail();
    }

    public static void initThreadLocals() {
        urlAggregator = new ThreadLocal<List<URLWrapper>>() {
            @Override
            protected List<URLWrapper> initialValue() {
                List<URLWrapper> list = new ArrayList<URLWrapper>();
                list.add(new URLWrapper(LinkValidatorMain.baseUrl, LinkValidatorMain.baseUrl, ValidationDepth.ONE));
                return list;
            }
        };

        htmlReport = new ThreadLocal<String>() {
            @Override
            protected String initialValue() {
                return ReportUtil.getHtmlTableHead().toString();
            }
        };
    }

    public static List<URLWrapper> getURLsThreadSafe() {
        return urlAggregator.get();
    }

    public static String getHtmlReportThreadSafe() {
        return htmlReport.get();
    }

    public static void setHtmlReportThreadSafe(final String value) {
        htmlReport.set(value);
    }

    public static void addHTMLReportTail() {
        StringBuilder sb = new StringBuilder(htmlReport.get());
        sb.append(ReportUtil.getHtmlTableTail());
        htmlReport.set(sb.toString());
    }
}
