//**********THIS FILE WAS USED AS BACKUP >>CAN BE DELETED PERMANENTLY****************

package demo.wrappers;

public class WrappersNew {
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
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    static List<String> listOfDataWinPercentLessThan40;
    static SoftAssert softAssert = new SoftAssert();
    static Map<String, Object> map;
    static boolean isWinner = false;
    static List<List<String>> listOfListTopFiveMoviesForAllYears = new ArrayList<>();
    static Map<String,String> topMoviesMap = new HashMap<>();



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

    public static boolean wrapper_clickATab(ChromeDriver driver, WebDriverWait wait, WebElement wePageTitle){
        try{

            wePageTitle.click();
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }    

    }

    public static boolean wrapper_parseATableWinPercentLessthan40(ChromeDriver driver, WebDriverWait wait){

        try{

            //Iterate through the table and 
            //collect the Team Name, Year and Win % for the teams with Win % less than 40% (0.40)
            
            int pageNumber = 1;
            WebElement weNextPage;
            while(pageNumber <= 4){
                System.out.println("Page Number :"+pageNumber);
                
                weNextPage = driver.findElement(By.xpath("//li/a[@aria-label='Next']"));
     
                JavascriptExecutor js = (JavascriptExecutor)driver;
                js.executeScript("arguments[0].scrollIntoView(true);",weNextPage);
                
                weNextPage.click();
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
                        );
                
                
                List<WebElement> listOfHeaderNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@class='table']/tbody/tr/th")));
                int countOfColumns = listOfHeaderNames.size();

                List<WebElement> listOfRows = driver.findElements(By.xpath("//table[@class='table']/tbody/tr[@class='team']"));
                int countOfRows = listOfRows.size();
                
                System.out.println("Count of Columns : " + countOfColumns);
                System.out.println("Count of Rows(Excluding Headers) : " + countOfRows);
            
                Thread.sleep((new java.util.Random().nextInt(3)+2)*1000);
                
                Wrappers.wrapper_getDataWinPercentLessthan40(driver, wait, listOfHeaderNames , listOfRows, countOfColumns, countOfRows);
        
                pageNumber++;
                  
            }
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }    

    }

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

    public static boolean wrapper_convertListToMap(ChromeDriver driver, WebDriverWait wait){

        try{

            map = new HashMap<>();
            
            map.put("Epoch Time of Scrape", System.currentTimeMillis()/1000);
            map.put("Team Name",listOfDataWinPercentLessThan40.get(0));
            map.put("Year", listOfDataWinPercentLessThan40.get(1));
            map.put("Win %",listOfDataWinPercentLessThan40.get(2));
            
            

            System.out.println(map);
            softAssert.assertTrue(Wrappers.wrapper_buildAndStoreJsonFile(driver, wait),"Printing of HashMap FAILED!!");

            return true;

        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }




    public static boolean wrapper_buildAndStoreJsonFile(ChromeDriver driver, WebDriverWait wait){

        try{

            map.forEach((key,value) -> {

                System.out.println(key + ":" + value);

            });
            ObjectMapper mapper = new ObjectMapper();
            String mapJsonAsString = mapper.writeValueAsString(map);
            String mapJsonAsPrettyString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            System.out.println(mapJsonAsString);
            System.out.println(mapJsonAsPrettyString);

            //String userDir = System.getProperty("user.dir");
            //Writing JSON on a file
            File f = new File("/Users/garikimukkulakamalakar/dev/mjdeepika07-ME_QA_XSCRAPE_DATA/src/test/resources/hockey-team-data.json");
            
            mapper.writerWithDefaultPrettyPrinter().writeValue(f,map);
            
            return true;


        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    public static List<List<String>> wrapper_clickOnTheYearToViewOscarWinningFilms(ChromeDriver driver, WebDriverWait wait, WebElement weEachYearLink){

        
        try{

            int count = 0;
            List<WebElement> listOfRowsInFilmsTable = driver.findElements(By.xpath("//tr[@class='film']"));
            List<List<String>> listOfListTopFiveMovies = new ArrayList<>();

            for(int i = 0; i < 5; i++){
                List<String> listOfTopFiveMovies = new ArrayList<>();
                Boolean isWinner = (count==0);

                String epochTime = String.valueOf(System.currentTimeMillis()/1000);
                //System.out.println("Epoch Time of Scrape : " + epochTime);
                listOfTopFiveMovies.add(epochTime);

                //System.out.println("Year : " + weEachYearLink.getText());
                listOfTopFiveMovies.add(weEachYearLink.getText());

                WebElement weTitle =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-title']"));
                //System.out.println(weTitle.getText());
                listOfTopFiveMovies.add(weTitle.getText());

                WebElement weNominations =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-nominations']"));
                //System.out.println(weNominations.getText());
                listOfTopFiveMovies.add(weNominations.getText());

                WebElement weAwards =  listOfRowsInFilmsTable.get(i).findElement(By.xpath("./td[@class='film-awards']"));
                //System.out.println(weAwards.getText());
                listOfTopFiveMovies.add(weAwards.getText());

                //System.out.println("IsWinner : " + isWinner);
                listOfTopFiveMovies.add(String.valueOf(isWinner));

                //System.out.println("List of Strings : " + listOfTopFiveMovies);

                listOfListTopFiveMovies.add(listOfTopFiveMovies);

                count++;
            }

            System.out.println("List of List of Strings : " + listOfListTopFiveMovies);
            
            for(List<String> list : listOfListTopFiveMovies)
                listOfListTopFiveMoviesForAllYears.add(list);

            //System.out.println(listOfListTopFiveMoviesForFiveYears);
            return listOfListTopFiveMoviesForAllYears;
            //return true;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return listOfListTopFiveMoviesForAllYears;
            
        }
    }

    public static Map<String,String> wrapper_convertHashmapToArrayList(ChromeDriver driver, WebDriverWait wait, List<List<String>> listOfListTopFiveMoviesForAllYears){
        
        List<String> mapKeyNames = new ArrayList<>();
        mapKeyNames.add("Epoch Time of Scrape");
        mapKeyNames.add("Year");
        mapKeyNames.add("Title");
        mapKeyNames.add("Nomination");
        mapKeyNames.add("Awards");
        mapKeyNames.add("isWinner");

        for(List<String> list : listOfListTopFiveMoviesForAllYears){
            System.out.println("listOfListTopFiveMoviesForAllYears Size : "+ listOfListTopFiveMoviesForAllYears.size());
            for(int i = 0; i < list.size(); i++){
                System.out.println("Size of single list : "+list.size());
                
                topMoviesMap.put(list.get(i),mapKeyNames.get(i));
            
            }
            
        }

        topMoviesMap.forEach((key,value) -> {

            System.out.println(key + " : " + value);

        });

        return topMoviesMap;
    }


    public static Map<String,String> wrapper_convertArraylistToHashmap(ChromeDriver driver, WebDriverWait wait, List<List<String>> listOfListTopFiveMoviesForAllYears){

        List<String> mapKeyNames = new ArrayList<>();
        mapKeyNames.add("Epoch Time of Scrape");
        mapKeyNames.add("Year");
        mapKeyNames.add("Title");
        mapKeyNames.add("Nomination");
        mapKeyNames.add("Awards");
        mapKeyNames.add("isWinner");

        for(List<String> list : listOfListTopFiveMoviesForAllYears){
            System.out.println("listOfListTopFiveMoviesForAllYears Size : "+ listOfListTopFiveMoviesForAllYears.size());
            for(int i = 0; i < list.size(); i++){
                System.out.println("Size of single list : "+list.size());
                
                topMoviesMap.put(list.get(i),mapKeyNames.get(i));
            
            }
            
        }

        topMoviesMap.forEach((key,value) -> {

            System.out.println(key + " : " + value);

        });

        return topMoviesMap;
    }


    public static boolean wrapper_clickOnTheYearToViewOscarWinningFilmsOld(ChromeDriver driver, WebDriverWait wait, WebElement weEachYearLink){

        try{

            int bestPictureHeaderIndex=0;

            wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class='table']/thead/tr/th"))));
            
            List<WebElement> listOfHeaderNames = driver.findElements(By.xpath("//table[@class='table']/thead/tr/th"));
            for(int i = 0; i < listOfHeaderNames.size() ; i++){
                if(listOfHeaderNames.get(i).getText().trim().contains("Best Picture")){
                    bestPictureHeaderIndex = i+1;
                    System.out.println("The Index of the 'Best Picture' column header is : "+bestPictureHeaderIndex);
                    break;
                }
            }
        

            //Get the list of webelements for all the movies and store them in a List 'weListOfMovieNames' 
            List<WebElement> weListOfMovieNames = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@class='table']/tbody/tr[@class='film']/td[1]")));
            
            
            //Create a list 'listOfTopFiveMovieNames' of strings to store top five movie names 
            List<String> listOfTopFiveMovieNames = new ArrayList<>();

            //Get a webelement from the List of webelements using get() method and then get the exact movie name using getText() method
            //Iterate for 5 times, since we need top '5' movie names 
            for(int i = 0; i < 5; i++){
                
                listOfTopFiveMovieNames.add(weListOfMovieNames.get(i).getText());
            
            }

            //Iterate thru the listOfMovieNames to print the top 5 movie names
            Iterator<String> it = listOfTopFiveMovieNames.iterator();
            while(it.hasNext()){
                System.out.println(it.next());

            }


            return true;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
            
        }
    }
    
    public static boolean wrapper_isWinner(ChromeDriver driver, WebDriverWait wait, String movieName, int bestPictureHeaderIndex){
        try{
            
            WebElement weIsWinner = driver.findElement(By.xpath("//tbody/tr/td[contains(text(),'"+movieName+"')]/following-sibling::td/i"));
            if(weIsWinner.getAttribute("class").contains("glyphicon glyphicon-flag"))
                {
                    isWinner=true;
                    System.out.println("The movie "+movieName+" is the 'Best Picture' movie");
                }
            return true;
        }
        catch(NoSuchElementException e){
            System.out.println("This movie "+movieName+"is not the best picture" + e.getMessage());
            return false;
            
        }

    }


    

}

}
