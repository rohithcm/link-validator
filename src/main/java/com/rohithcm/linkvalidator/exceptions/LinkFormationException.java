package com.rohithcm.linkvalidator.exceptions;

/**
 * Created by rohithcm on 21/01/16.
 * On the event of link identified is of unidentified format.
 */
public class LinkFormationException extends RuntimeException{

    public LinkFormationException(final String errorMessage) {
        super(errorMessage);
    }

}
