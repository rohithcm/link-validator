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
        initSetup();
        TriggerValidation.trigger(baseUrl);
        ReportUtil.generateHtmlReport(TriggerValidation.getHtmlReportThreadSafe());
    }

    private static void initSetup() {
        final String baseUrlString = "http://www.google.com";
        logger.debug("Base URL identified as " + baseUrlString);
        logger.info("Base URL identified as " + baseUrlString);
        final int validationLevelNumber = 1;
        baseUrl = UrlUtil.getURLFromString(baseUrlString);
        validationDepth = ValidationDepth.getValidationLevel(validationLevelNumber);
        if (validationDepth.equals(ValidationDepth.INVALID))
            throw new InvalidLevelException("Validation Level " + validationLevelNumber + " is invalid");
    }
}
