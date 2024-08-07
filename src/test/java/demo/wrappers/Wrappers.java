package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.rowset.WebRowSet;


import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    static List<String> listOfDataWinPercentLessThan40;
    static SoftAssert softAssert = new SoftAssert();
    static Map<String, Object> map;
    static List<Map<String, String>> listOfMapsTopFiveMovies = new ArrayList<>();
    public static File testCase02File;
    public static File testCase01File;


    //Navigate to the desired homepage url
    public static boolean wrapper_navigateToUrl(ChromeDriver driver, WebDriverWait wait, String url) {
        try {
            if (!driver.getCurrentUrl().trim().equals(url))
                driver.get(url);

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                            .equals("complete"));
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    //Click on hyperlink
    public static boolean wrapper_clickATab(ChromeDriver driver, WebDriverWait wait, WebElement wePageTitle){
        try{

            wePageTitle.click();
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }    

    }

    //Iterate through the table
    public static boolean wrapper_parseATableWinPercentLessthan40(ChromeDriver driver, WebDriverWait wait){

        try{
            
            int pageNumber = 1;
            WebElement weNextPage;

            //Get the movie details from the 4 pages. looping thru the pageNumber from 1 to 4
            while(pageNumber <= 4){
                System.out.println("Page Number :"+pageNumber);
                
                weNextPage = driver.findElement(By.xpath("//li/a[@aria-label='Next']"));
                
                //scroll to the desired webelement
                JavascriptExecutor js = (JavascriptExecutor)driver;
                js.executeScript("arguments[0].scrollIntoView(true);",weNextPage);
                
                //Click the next page link
                weNextPage.click();
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
                        );
                
                
                //Get the List of webelements having the header names 
                List<WebElement> listOfHeaderNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@class='table']/tbody/tr/th")));
                int countOfColumns = listOfHeaderNames.size();

                List<WebElement> listOfRows = driver.findElements(By.xpath("//table[@class='table']/tbody/tr[@class='team']"));
                int countOfRows = listOfRows.size();
                
                System.out.println("Count of Columns : " + countOfColumns);
                System.out.println("Count of Rows(Excluding Headers) : " + countOfRows);
            
                Thread.sleep((new java.util.Random().nextInt(3)+2)*1000);
                
                //Get the movie details having the win % less than 0.40
                Wrappers.wrapper_getDataWinPercentLessthan40(driver, wait, listOfHeaderNames , listOfRows, countOfColumns, countOfRows);
        
                pageNumber++;
                  
            }
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }    

    }

    //Get the movie details having the win % less than 0.40
    public static boolean wrapper_getDataWinPercentLessthan40(ChromeDriver driver, WebDriverWait wait, List<WebElement> listOfHeaderNames , List<WebElement> listOfRows, int countOfColumns, int countOfRows){
        
        try{
            int teamNameIndex = 0;
            int yearIndex = 0;
            int winPercentageIndex = 0;

            //Iterate thru the list of headers to get the index of required columns : Team name, Year, Win %
            for(int i = 0; i < listOfHeaderNames.size(); i++){
  
                if(listOfHeaderNames.get(i).getText().trim().equals("Team Name"))
                    teamNameIndex = i+1;
                else if(listOfHeaderNames.get(i).getText().trim().equals("Year"))
                    yearIndex = i+1;
                else if(listOfHeaderNames.get(i).getText().trim().equals("Win %"))
                    winPercentageIndex = i+1;

            }

            System.out.println("teamNameIndex : " + teamNameIndex);
            System.out.println("yearIndex : " + yearIndex);
            System.out.println("winPercentageIndex : " + winPercentageIndex);

            for(int rowIndex = 1; rowIndex <= countOfRows; rowIndex++ ){

                WebElement weWinPercentage = driver.findElement(By.xpath("//table[@class='table']/tbody/tr[@class='team']["+rowIndex+"]/td["+winPercentageIndex+"]"));    
                String winPercenatageString = weWinPercentage.getText().trim();
                double winPercentageDouble = Double.parseDouble(winPercenatageString); 
                System.out.println("winPercentageDouble :" + winPercentageDouble);

                if(winPercentageDouble < 0.40){
                    
                    WebElement weTeamName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table']/tbody/tr[@class='team']["+rowIndex+"]/td["+teamNameIndex+"]")));
                    WebElement weYear = driver.findElement(By.xpath("//table[@class='table']/tbody/tr[@class='team']["+rowIndex+"]/td["+yearIndex+"]"));
                    System.out.println(weTeamName.getText().trim() + '|' + weYear.getText().trim() + '|' + weWinPercentage.getText().trim() );
                    Wrappers.wrapper_storeDataInArrayList(driver, wait, weTeamName, weYear, weWinPercentage);
                }
                else
                    System.out.println("The wincpercentage is >= 0.40");

            }

            return true;

        } 
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    //Wrapper method to store data into ArrayList
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean wrapper_storeDataInArrayList(ChromeDriver driver, WebDriverWait wait, WebElement weTeamName, WebElement weYear, WebElement weWinPercentage){

        try{
            listOfDataWinPercentLessThan40 = new ArrayList();
            listOfDataWinPercentLessThan40.add(weTeamName.getText());
            listOfDataWinPercentLessThan40.add(weYear.getText());
            listOfDataWinPercentLessThan40.add(weWinPercentage.getText());
            System.out.println(listOfDataWinPercentLessThan40);
            softAssert.assertTrue(Wrappers.wrapper_convertListToMap(driver, wait),"Conversion from list to map FAILED!!");
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }


    }

    //Wrapper method to convert from list to hashmap
    public static boolean wrapper_convertListToMap(ChromeDriver driver, WebDriverWait wait){

        try{

            map = new HashMap<>();
            
            map.put("Epoch Time of Scrape", System.currentTimeMillis()/1000);
            map.put("Team Name",listOfDataWinPercentLessThan40.get(0));
            map.put("Year", listOfDataWinPercentLessThan40.get(1));
            map.put("Win %",listOfDataWinPercentLessThan40.get(2));
            
            

            System.out.println(map);
            softAssert.assertTrue(Wrappers.wrapper_buildAndStoreJsonFile(driver, wait, testCase01File),"Printing of HashMap FAILED!!");

            return true;

        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }



    //Wrapper method to build and store the json file
    public static boolean wrapper_buildAndStoreJsonFile(ChromeDriver driver, WebDriverWait wait, File testCase01File){

        try{
            testCase01File = new File("/Users/garikimukkulakamalakar/dev/mjdeepika07-ME_QA_XSCRAPE_DATA/src/test/resources/hockey-team-data.json");
            
            map.forEach((key,value) -> {

                System.out.println(key + ":" + value);

            });

            ObjectMapper mapper = new ObjectMapper();
            String mapJsonAsString = mapper.writeValueAsString(map);
            String mapJsonAsPrettyString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            System.out.println(mapJsonAsString);
            System.out.println(mapJsonAsPrettyString);

            //Writing JSON on a file
            mapper.writerWithDefaultPrettyPrinter().writeValue(testCase01File,map);
            wrapper_isJsonFilePresentAndNotEmpty(driver, wait, testCase01File);
            
            return true;


        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    //wrapper method to store all the top 5 movie details for a given year in a hashmap and then move one by one to the ArrayList
    public static boolean wrapper_clickOnTheYearToViewOscarWinningFilms(ChromeDriver driver, WebDriverWait wait, WebElement weEachYearLink){

        
                try{
        
                    int count = 0;
                    List<WebElement> listOfRowsInFilmsTable = driver.findElements(By.xpath("//tr[@class='film']"));
                    List<String> mapKeyNames = new ArrayList<>();
                    
                    mapKeyNames.add("Epoch Time of Scrape");
                    mapKeyNames.add("Year");
                    mapKeyNames.add("Title");
                    mapKeyNames.add("Nomination");
                    mapKeyNames.add("Awards");
                    mapKeyNames.add("isWinner");

                    
                    for(int i = 0; i < 5; i++){
                        
                        Map<String,String> mapOfMovieDetails = new HashMap<>();

                        String epochTime = String.valueOf(System.currentTimeMillis()/1000);
                        mapOfMovieDetails.put(mapKeyNames.get(0),epochTime);
        
                        mapOfMovieDetails.put(mapKeyNames.get(1),weEachYearLink.getText());

                        WebElement weTitle =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-title']"));
                        mapOfMovieDetails.put(mapKeyNames.get(2),weTitle.getText());
                       
        
                        WebElement weNominations =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-nominations']"));
                        mapOfMovieDetails.put(mapKeyNames.get(3),weNominations.getText());
                       
        
                        WebElement weAwards =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-awards']"));
                        mapOfMovieDetails.put(mapKeyNames.get(4),weAwards.getText());
                     
        
                        Boolean isWinner = (count==0);
                        mapOfMovieDetails.put(mapKeyNames.get(5),String.valueOf(isWinner));

                        listOfMapsTopFiveMovies.add(mapOfMovieDetails);
                      
                        count++;
                    }
                    System.out.println("List Of hashmap objects : " + listOfMapsTopFiveMovies);
                  
                    return true;
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                    return false;
                    
                }
            }

    //Store List of hashmaps to jsonFile
    public static boolean wrapper_storeListToJsonFile(ChromeDriver driver, WebDriverWait wait){

        try{

            testCase02File = new File("/Users/garikimukkulakamalakar/dev/mjdeepika07-ME_QA_XSCRAPE_DATA/src/output folder/oscar-winner-data.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(testCase02File,listOfMapsTopFiveMovies);

            return true;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
            
        }
    }

    //Check if the json file is created successfully or not under the specified filepath and that the file contains some data  
    public static boolean wrapper_isJsonFilePresentAndNotEmpty(ChromeDriver driver, WebDriverWait wait, File file){

        try{

            Assert.assertTrue(file.exists(),"File does not exists in the specified file path.");
            Assert.assertFalse(file.length()==0, "The file is present and data is also present!");
            return true;
        }
        catch(Exception e){
            System.out.println("File does not exist or file is empty!! " + e.getMessage());
            return false;
            
        
    }
    


}
}

