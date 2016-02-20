package com.rohithcm.linkvalidator.exceptions;

/**
 * Created by Rohith on 20-02-2016.
 * File Not found exception
 */
public class ResourceFileReadException  extends RuntimeException {

    public ResourceFileReadException(final String errorMessage) {
        super(errorMessage);
    }

}
