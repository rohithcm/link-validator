package com.rohithcm.linkvalidator.beans;

import com.rohithcm.linkvalidator.enums.ValidationDepth;

import java.net.URL;

/**
 * Created by rohithcm on 21/01/16.
 * Wrapper to URL component having subcomponents url, parentUrl and validationdepth
 */
public class URLWrapper {
    private URL url;
    private URL parentUrl;
    private ValidationDepth validationDepth;

    public URLWrapper(final URL url, final URL parentUrl, final ValidationDepth validationDepth) {
        this.url = url;
        this.parentUrl = parentUrl;
        this.validationDepth = validationDepth;
    }

    public URL getUrl() {
        return url;
    }

    public URL getParentUrl() {
        return parentUrl;
    }

    public ValidationDepth getValidationDepth() {
        return validationDepth;
    }
}
