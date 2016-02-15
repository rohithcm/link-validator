package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.beans.URLWrapper;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import org.apache.commons.collections.CollectionUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by rohithcm on 21/01/16.
 */
public class TriggerValidation {

    //private static ThreadLocal<List<URLWrapper>> urlAggregator;
    //private static AtomicReference<List<URLWrapper>> urlAggregator;
    //public static LinkedList<URLWrapper> urlAggregator = Collections.synchronizedList(new LinkedList<URLWrapper>());
    public static LinkedBlockingDeque<URLWrapper> urlAggregator = new LinkedBlockingDeque<URLWrapper>();
    private static ThreadLocal<String> htmlReport;

    public static void trigger(final URL baseURL) {
        initThreadLocals();
        List<URLWrapper> baseURLWrapper = new ArrayList<URLWrapper>();
        baseURLWrapper.add(new URLWrapper(LinkValidatorMain.baseUrl, LinkValidatorMain.baseUrl, ValidationDepth.ONE));
        //urlAggregator = new AtomicReference<List<URLWrapper>>(baseURLWrapper);
        urlAggregator.offer(new URLWrapper(LinkValidatorMain.baseUrl, LinkValidatorMain.baseUrl, ValidationDepth.ONE));

        ExecutorService executor = Executors.newFixedThreadPool(10);

        //while (!urlAggregator.get().isEmpty()) {
        while (!urlAggregator.isEmpty()) {
            Runnable validatorWorker = new ValidatorWorkerThread();
            executor.execute(validatorWorker);
        }
        addHTMLReportTail();
    }

    public static void initThreadLocals() {
        /*urlAggregator = new ThreadLocal<List<URLWrapper>>() {
            @Override
            protected List<URLWrapper> initialValue() {
                List<URLWrapper> list = new ArrayList<URLWrapper>();
                list.add(new URLWrapper(LinkValidatorMain.baseUrl, LinkValidatorMain.baseUrl, ValidationDepth.ONE));
                return list;
            }
        };*/

        htmlReport = new ThreadLocal<String>() {
            @Override
            protected String initialValue() {
                return ReportUtil.getHtmlTableHead().toString();
            }
        };
    }

    /*public static List<URLWrapper> getURLsThreadSafe() {
        //return urlAggregator.get();
        return urlAggregator;
    }*/
    public static void addURLsThreadSafe(List<URLWrapper> updatedList) {
        //urlAggregator.getAndSet(updatedList);
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
