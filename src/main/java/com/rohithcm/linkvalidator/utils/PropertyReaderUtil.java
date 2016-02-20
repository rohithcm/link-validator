package com.rohithcm.linkvalidator.utils;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.rohithcm.linkvalidator.beans.PropertyBean;
import com.rohithcm.linkvalidator.enums.LVProperties;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.exceptions.InvalidLevelException;
import com.rohithcm.linkvalidator.exceptions.ResourceFileReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Rohith on 20-02-2016.
 * Utility to read and load property from external resource files
 */
public class PropertyReaderUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertyReaderUtil.class);
    private static PropertyBean propBean = PropertyBean.getInstance();
    private static final String PROPERTY_FILE = "LinkValidatorProperties.yaml";

    private static void loadBaseUrls(final String fileName) {
        logger.info("Loading base urls from file : "+fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(PropertyReaderUtil.class.getClassLoader().getResourceAsStream(fileName)));
        HashSet<URL> baseUrls = new HashSet<URL>();
        String thisLine;
        try {
            while (null != (thisLine = br.readLine())) {
                logger.info("BaseURL identified : "+thisLine);
                baseUrls.add(UrlUtil.getBaseURLFromString(thisLine));
            }
        } catch (IOException e) {
            logger.info(fileName+" file is not found");
            throw new ResourceFileReadException(e.getMessage());
        }
        propBean.setBaseUrls(baseUrls);
    }

    private static void populatePropertyBean(HashMap<String , Object> map) {
        loadBaseUrls(map.get(LVProperties.BASEURLFILE.toString().toLowerCase()).toString());

        final int validationLevelNumber = Integer.parseInt(map.get(LVProperties.VALIDATIONLEVEL.toString().toLowerCase()).toString());
        final ValidationDepth validationDepth = ValidationDepth.getValidationLevel(validationLevelNumber);
        if (validationDepth.equals(ValidationDepth.INVALID))
            throw new InvalidLevelException("Validation Level " + validationLevelNumber + " is invalid");
        logger.info("Validation Depth identified : " + validationDepth);
        propBean.setValidationDepth(validationDepth);

        ArrayList<String> errorStrings = (ArrayList<String>)map.get(LVProperties.ERRORSTRINGS.toString().toLowerCase());
        propBean.setErrorStrings(errorStrings);
        logger.info("Error Strings identified : "+errorStrings.toString());
    }

    public static void loadProperties(){
        try {
            logger.info("Reading properties from file : "+PROPERTY_FILE);
            //YamlReader reader = new YamlReader(new FileReader("CLASSPATH:"+PROPERTY_FILE));
            YamlReader reader = new YamlReader(new InputStreamReader(PropertyReaderUtil.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)));
            populatePropertyBean((HashMap)reader.read());
        } catch (YamlException e) {
            logger.info(PROPERTY_FILE+" file is not found");
            throw new ResourceFileReadException(e.getMessage());
        }
    }
}
