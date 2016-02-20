Purpose :
JAVA application to validate html links..

User input :
src/main/resources/LinkValidatorProperties.yaml file holds the following properties
1. baseurlfile : File name holding urls to be validated.
2. validationlevel : Depth to validate the urls
    If validationlevel value is 1, the application will just verify if the urls specified in the baseurlfile are valid or not.
    If validationlevel value is 2, the application will first validate the base urls, then proceed to each urls and collects all of the
    html links in the pages (including hidden links). Then proceed to validate those sub urls as well.
    Level 3 will go one more leve deeper and so on..
3. errorstrings : custom error messages in a page.
    LinkValidator normally validates a url by verifying if the web status response code is 200 or not. In certain web applications, there may be
    pages which returns 200 code but still deemed as invalid pages. There may be custom error messages
    like 'This page is inactive for maintenance' may be present in the page. Based on the judgement of user, these pages can be categorized
    to valid or not.


Usage :
    Populate user inputs in the resource files. From base folder in command prompt, execute the following,
        mvn clean install exec:java

    Links will be validated and final report will be generated under the name link-validation-report.html in the base folder.
    linkvalidator.log holds the logs. Sample of these files can be seen here.