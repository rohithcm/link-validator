package com.rohithcm.linkvalidator.utils;

import com.rohithcm.linkvalidator.beans.HTMLTableRow;
import com.rohithcm.linkvalidator.enums.URLStatus;
import com.rohithcm.linkvalidator.enums.ValidationDepth;
import com.rohithcm.linkvalidator.exceptions.ReportGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by rohithcm on 21/01/16.
 */
public class ReportUtil {
    private static Logger logger = LoggerFactory.getLogger(ReportUtil.class);

    public static String getHtmlTableHead() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("</head>");
        sb.append("<table>");
        sb.append("<th style = \"background: #333; color: white; font-weight: bold; padding: 6px; border: 1px solid #ccc; text-align: left;\"> Validated Url");
        sb.append("</th>");
        sb.append("<th style = \"background: #333; color: white; font-weight: bold; padding: 6px; border: 1px solid #ccc; text-align: left;\"> Parent Url");
        sb.append("</th>");
        sb.append("<th style = \"background: #333; color: white; font-weight: bold; padding: 6px; border: 1px solid #ccc; text-align: left;\"> Validation Level");
        sb.append("</th>");
        sb.append("<th style = \"background: #333; color: white; font-weight: bold; padding: 6px; border: 1px solid #ccc; text-align: left;\"> Status");
        sb.append("</th>");
        sb.append("<th style = \"background: #333; color: white; font-weight: bold; padding: 6px; border: 1px solid #ccc; text-align: left;\"> Comment");
        sb.append("</th>");
        return sb.toString();
    }

    public static String getHtmlTableTail() {
        StringBuilder sb = new StringBuilder();
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public static String appendPageStatusToReport(final HTMLTableRow htmlTableRow) {
        logger.info("==========================URL : " + htmlTableRow.getUrl());
        logger.info("==========================URL : " + htmlTableRow.getStatus());
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + htmlTableRow.getUrl());
        sb.append("</td>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + htmlTableRow.getParentUrl());
        sb.append("</td>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + htmlTableRow.getValidationDepth());
        sb.append("</td>");
        if (htmlTableRow.getStatus().equals(URLStatus.FAIL))
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> <font color=\"#8B0000\">" + htmlTableRow.getStatus() + "</font>");
        else
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + htmlTableRow.getStatus());
        sb.append("</td>");
        if (!htmlTableRow.getComments().equals(""))
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> <font color=\"#8B0000\">" + htmlTableRow.getComments() + "</font>");
        else
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + htmlTableRow.getComments());
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    public static String appendPageStatusToReport(final String url, final String parentUrl, final ValidationDepth validationDepth, final URLStatus status, final String comment) {
        logger.info("==========================URL : " + url);
        logger.info("==========================URL : " + status);
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + url);
        sb.append("</td>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + parentUrl);
        sb.append("</td>");
        sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + validationDepth);
        sb.append("</td>");
        if (status.equals(URLStatus.FAIL))
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> <font color=\"#8B0000\">" + status + "</font>");
        else
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + status);
        sb.append("</td>");
        if (!comment.equals(""))
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> <font color=\"#8B0000\">" + comment + "</font>");
        else
            sb.append("<td style = \"padding: 6px; border: 1px solid #ccc; text-align: left;\"> " + comment);
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    public static void generateHtmlReport(final String reportAsString) {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(new File("link-validation-report.html")));
            bw.write(reportAsString);
            bw.close();
        } catch (IOException e) {
            logger.error("Generating html report file failed");
            throw new ReportGenerationException("Generating html report file failed");
        }
    }
}
