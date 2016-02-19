package com.rohithcm.linkvalidator;

import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.exceptions.InvalidLevelException;
import com.rohithcm.linkvalidator.utils.ReportUtil;
import com.rohithcm.linkvalidator.utils.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Created by rohithcm on 21/01/16.
 */
public class LinkValidatorMain {
    private static Logger logger = LoggerFactory.getLogger(LinkValidatorMain.class);
    public static URL baseUrl;
    public static ValidationDepth validationDepth;

    public static void main(String[] args) {
        logger.info("========== Link Validation begins ===============");
        initSetup();
        TriggerValidation.trigger(baseUrl);
        ReportUtil.generateHtmlReport(TriggerValidation.getHtmlReportThreadSafe());
    }

    private static void initSetup() {
        final String baseUrlString = "http://www.google.co.in/";
        logger.info("Base URL identified : " + baseUrlString);
        final int validationLevelNumber = 1;
        baseUrl = UrlUtil.getBaseURLFromString(baseUrlString);
        validationDepth = ValidationDepth.getValidationLevel(validationLevelNumber);
        logger.info("Validation Depth identified : " + validationDepth);
        if (validationDepth.equals(ValidationDepth.INVALID))
            throw new InvalidLevelException("Validation Level " + validationLevelNumber + " is invalid");
    }
}
