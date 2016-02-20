package com.rohithcm.linkvalidator.beans;

import com.rohithcm.linkvalidator.enums.URLStatus;
import com.rohithcm.linkvalidator.enums.ValidationDepth;

/**
 * Created by rohithcm on 21/01/16.
 * Identifies HTML Table Row in the report
 */
public class HTMLTableRow {
    private String url;
    private String parentUrl;
    private ValidationDepth validationDepth;
    private URLStatus status;
    private String comments;

    public HTMLTableRow(final String url, final String parentUrl, final ValidationDepth validationDepth, final URLStatus status, final String comments) {
        this.url = url;
        this.parentUrl = parentUrl;
        this.validationDepth = validationDepth;
        this.status = status;
        this.comments = comments;
    }

    public ValidationDepth getValidationDepth() {
        return validationDepth;
    }

    public void setValidationDepth(ValidationDepth validationDepth) {
        this.validationDepth = validationDepth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public URLStatus getStatus() {
        return status;
    }

    public void setStatus(URLStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
