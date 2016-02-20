package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.utils.PropertyReaderUtil;
import com.rohithcm.linkvalidator.utils.ReportUtil;

/**
 * Created by rohithcm on 21/01/16.
 * Main program to init the application
 */
public class LinkValidatorMain {

    public static void main(String[] args) {
        initSetup();
        ValidatorWorkerFactory.trigger();
        ReportUtil.generateHtmlReport(ValidatorWorkerFactory.getHtmlReportThreadSafe());
    }

    private static void initSetup() {
        PropertyReaderUtil.loadProperties();
    }
}
