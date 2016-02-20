package com.rohithcm.linkvalidator.beans;

import com.rohithcm.linkvalidator.enums.ValidationDepth;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Rohith on 20-02-2016.
 * Singleton class holding the property values
 */
public class PropertyBean {
    private static PropertyBean instance = new PropertyBean();
    public HashSet<URL> baseUrls;
    public ValidationDepth validationDepth;
    public ArrayList<String> errorStrings;

    public static PropertyBean getInstance() {
        return instance;
    }

    private PropertyBean() {
    }

    public HashSet<URL> getBaseUrls() {
        return baseUrls;
    }

    public void setBaseUrls(HashSet<URL> baseUrls) {
        this.baseUrls = baseUrls;
    }

    public ValidationDepth getValidationDepth() {
        return validationDepth;
    }

    public void setValidationDepth(ValidationDepth validationDepth) {
        this.validationDepth = validationDepth;
    }

    public ArrayList<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(ArrayList<String> errorStrings) {
        this.errorStrings = errorStrings;
    }
}
