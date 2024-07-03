package demo;

import org.checkerframework.checker.units.qual.radians;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Level;

import javax.sql.rowset.WebRowSet;

// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCases {
    ChromeDriver driver;
    WebDriverWait wait;
    String url = "https://www.scrapethissite.com/pages/";
    SoftAssert softAssert = new SoftAssert();
    String pageName;
    WebElement wePageTitle;
    List<List<String>> listOfListTopFiveMoviesForAllYears;
    Map<String, String> topMoviesMap = new HashMap<>();

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    }

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */
    @Test
    public void testCase01() {

        /*
         * testCase01: Go to this website and click on
         * "Hockey Teams: Forms, Searching and Pagination"
         * Iterate through the table and collect the Team Name, Year and Win % for the
         * teams with Win % less than 40% (0.40)
         * Iterate through 4 pages of this data and store it in a ArrayList
         * Convert the ArrayList object to a JSON file named hockey-team-data.json. Feel
         * free to use Jackson library. (In the example, Maven is used, but you can
         * resolve dependencies similarly by copying Gradle import from Maven Central).
         * Each Hashmap object should contain:
         * Epoch Time of Scrape
         * Team Name
         * Year
         * Win %
         */
        try {

            //Navigate to the desired url
            softAssert.assertTrue(Wrappers.wrapper_navigateToUrl(driver, wait, url),
                    "Navigation to the desired url failed!");

            
            // Save the desired link's keyword and pass that into xpath
            pageName = "forms";

            //Get the webelement for pagetitle
            wePageTitle = driver
                    .findElement(By.xpath("//div[@class='page']/h3/a[contains(@href,'/pages/" + pageName + "/')]"));

            //Click on the desired hyperlink : "Hockey Teams: Forms, Searching and Pagination"
            softAssert.assertTrue(Wrappers.wrapper_clickATab(driver, wait, wePageTitle),
                    "click on \"Hockey Teams: Forms, Searching and Pagination\" link failed!");

            //Parse thru the table
            softAssert.assertTrue(Wrappers.wrapper_parseATableWinPercentLessthan40(driver, wait));

            softAssert.assertAll();

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

    }

    @Test
    public void testCase02() {
        try {

            //Navigate to the home page url
            softAssert.assertTrue(Wrappers.wrapper_navigateToUrl(driver, wait, url),
                    "Navigation to the desired url failed!");

            //Save the hyperlink keyword in a variable and use the same in pageTitle xpath construction
            pageName = "ajax-javascript";

            wePageTitle = driver
                    .findElement(By.xpath("//div[@class='page']/h3/a[contains(@href,'/pages/" + pageName + "/')]"));
            
            //click on the desired hyperlink
            softAssert.assertTrue(Wrappers.wrapper_clickATab(driver, wait, wePageTitle),
                    "click on \"Oscar Winning Films: AJAX and Javascript\" link failed!");

            //On the newly landed page, get the list of all webelements of the year displayed
            List<WebElement> weYearLink = driver.findElements(By.xpath("//a[@class='year-link']"));
            
            // Iterate for all the years
            for (int i = 0; i < 6; i++) {

                //click the year
                weYearLink.get(i).click();

                //wait till the document ready state is 'complete' for all the details to be displayed
                new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                        webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                                .equals("complete"));

                //stop the webdriver from executing for some time           
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);
                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);

                //Display the year clicked on the console
                System.out.println("\nYear Clicked : " + weYearLink.get(i).getText());

                //Call the wrapper method to store all the top 5 movie details for a given year in a hashmap and then move one by one to the ArrayList
                softAssert.assertTrue(
                        Wrappers.wrapper_clickOnTheYearToViewOscarWinningFilms(driver, wait, weYearLink.get(i)),
                        "Storing all the top 5 movie details for a given year in a hashmap and then move one by one to the ArrayList - Failed!!");

                Thread.sleep((new java.util.Random().nextInt(3) + 2) * 1000);

            }

            //Store List of hashmaps to jsonFile
            softAssert.assertTrue((Wrappers.wrapper_storeListToJsonFile(driver, wait)),
                    "Storing the data from list(of hashmaps) to JSON file Failed!!");

            //Check if the json file is created successfully or not under the specified filepath and that the file contains some data
            softAssert.assertTrue(
                    (Wrappers.wrapper_isJsonFilePresentAndNotEmpty(driver, wait, Wrappers.testCase02File)),
                    "Json file is absent or empty!!");

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }
}