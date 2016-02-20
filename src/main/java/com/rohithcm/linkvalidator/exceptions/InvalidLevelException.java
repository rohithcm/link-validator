package com.rohithcm.linkvalidator.exceptions;

/**
 * Created by rohithcm on 21/01/16.
 * On the event of validation level input being an invalid entry.
 */
public class InvalidLevelException extends RuntimeException {

    public InvalidLevelException(final String errorMessage) {
        super(errorMessage);
    }

}
