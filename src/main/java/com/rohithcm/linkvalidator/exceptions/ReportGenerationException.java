package com.rohithcm.linkvalidator.exceptions;

/**
 * Created by rohithcm on 21/01/16.
 * On the event of error in report generation.
 */
public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(final String errorMessage) {
        super(errorMessage);
    }

}

