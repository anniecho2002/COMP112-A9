/* Code for COMP-102-112 - 2021T1, Assignment 9
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Color;

/**
 * AirPollutionAnalyser analyses hourly data about PM2.5 concentration of five
 * cities in China in October, November and December in 2015.
 * Each line of the file "pollution.txt" has information about the pollution
 * level and weather in a city at a particular hour and date.
 * Data is from https://archive.ics.uci.edu/ml/datasets.php.
 *
 * Core: two methods:
 *   loadData
 *      Loads the data from a file into a field containing an ArrayList of
 *      AirPollution objects.
 *      Hint: read all the lines from the file, and then take each line
 *            apart into the values to pass to the AirPollution constructor.
 *   findHazardousLevels
 *      Prints out all the records in the ArrayList that have a
 *      PM2.5 concentration 300 and over.
 *      Hint: see the methods in the AirPollution class, especially getPM and toString
 *
 * Completion: one method:
 *   findContrastingCities
 *      Compares each record in the list with every other record and finds
 *      every pair of cities that having a difference of PM2.5 concentration
 *      larger than 400, at the same hour on the same day.
 *      For each pair, it should print cityA, cityB, hour, date, difference
 *
 * Challenge: two methods
 *   findDailyAverage(String city)
 *      -Prints the average daily PM2.5 value for each day for the given city.
 *      -Finds the longest sequence of days there the average PM2.5 is always
 *       above 200 ("Very unhealthy").
 *      Hint: Use an array where the index corresponds to the day of the year.
 *   plotPollutionLevels
 *      Makes a line plot for the daily average PM2.5 concentration data of
 *      the five cities in the same figure. You may choose different colours
 *      to represent different cities.
 *      Hint: Make the findDailyAverage(String city) method return the array
 *      for a given city.
 */

public class AirPollutionAnalyser {

    private ArrayList<AirPollution> pollutions = new ArrayList<AirPollution>();

    /**
     * CORE
     *
     * Load data from the data file into the pollutions field:
     * clear the pollutions field.
     * Read lines from file
     * For each line, use Scanner to break up each line and make an AirPollution
     *  adding it to the pollutions field.
     */
    public void loadData() {
        pollutions.clear();
        String filename = "pollution.txt";
        try {
            List <String> allLines = Files.readAllLines(Path.of(filename));
            for (String line: allLines){
                Scanner sc = new Scanner(line);
                while (sc.hasNext()){
                    String city = sc.next();
                    String date = sc.next();
                    int hour = sc.nextInt();
                    double PM = sc.nextDouble();
                    double humidity = sc.nextDouble();
                    double temperature = sc.nextDouble();
                    pollutions.add(new AirPollution(city, date, hour, PM, humidity, temperature));
                }
            }
            UI.printf("Loaded %d pollution information into list\n", this.pollutions.size());
            UI.println("----------------------------");
        } catch(IOException e){UI.println("File reading failed");}  
    }

    /**
     * CORE
     *
     * Prints out all the records in the ArrayList that have a PM2.5 concentration
     * 300 and over
     */
    public void findHazardousLevels() {
        UI.clearText();
        UI.println("PM2.5 Concentration 300 and above:");
        for (int i=0; i<pollutions.size(); i++){
            if (pollutions.get(i).getPM() >= 300){
                UI.println(pollutions.get(i).toString());
            }
        }

        UI.println("------------------------");
    }

    /**
     * COMPLETION
     * 
     * Finds every pair of cities that have at the same hour on the same day 
     * a difference of PM2.5 concentration larger than 400.
     * You need to compare each record in the list with every other record
     * For each pair, it should print
     * cityA, cityB, hour, date, difference
     */
    public void findContrastingCities() {
        UI.clearText();
        UI.println("Pairs of cities whose PM2.5 differs by more than 400 at the same time");
        
        // for each value in pollutions size, checks against every other value
        for (int i=0; i<pollutions.size(); i++){
            ArrayList<AirPollution> otherPollutions = new ArrayList<AirPollution>();
            for (int j=0; j<pollutions.size(); j++){
                otherPollutions.add(pollutions.get(j));
            }
            otherPollutions.remove(i);
            for (int k=0; k<otherPollutions.size(); k++){
                if ((pollutions.get(i).diffPM(otherPollutions.get(k)) >= 400) && 
                    (pollutions.get(i).getHour() == otherPollutions.get(k).getHour()) &&
                    (pollutions.get(i).getDate().equals(otherPollutions.get(k).getDate()))){
                        UI.println(pollutions.get(i).getCity() + " " +
                                    otherPollutions.get(k).getCity() + " " +
                                    pollutions.get(i).getHour() + " " +
                                    pollutions.get(i).getDate() + " " +
                                    pollutions.get(i).diffPM(otherPollutions.get(k)));
                }
            }
        }
        UI.println("----------------------------");
    }
 
    // didn't finish lol
    public void findDailyAverage(){
        String city = UI.askString("City: ");
        ArrayList<AirPollution> beijing = new ArrayList<AirPollution>();
        ArrayList<String> beijingDates = new ArrayList<String>();
        ArrayList<AirPollution> chengdu = new ArrayList<AirPollution>();
        ArrayList<AirPollution> guangzhou = new ArrayList<AirPollution>();
        ArrayList<AirPollution> shanghai = new ArrayList<AirPollution>();
        ArrayList<AirPollution> shenyang = new ArrayList<AirPollution>();
    
        // sorts all the entries into respective arrays 
        for (int i=0; i<pollutions.size(); i++){
            if ((pollutions.get(i)).getCity().equals("beijing")){
                beijing.add(pollutions.get(i));
            }
            else if ((pollutions.get(i)).getCity().equals("chengdu")){
                chengdu.add(pollutions.get(i));
            }
            else if ((pollutions.get(i)).getCity().equals("guangzhou")){
                guangzhou.add(pollutions.get(i));
            }
            else if ((pollutions.get(i)).getCity().equals("shanghai")){
                shanghai.add(pollutions.get(i));
            }
            else if ((pollutions.get(i)).getCity().equals("shenyang")){
                shenyang.add(pollutions.get(i));
            }
        }
        
        double sum = 0;
        if (city.equals("beijing")){
            for (int j=0; j<beijing.size(); j++){
                sum = sum + beijing.get(j).getPM();
            }
            UI.println("BeiJing's average PM: " + sum/beijing.size());
            for (int k=0; k<beijing.size(); k++){
                if (beijing.get(k).getPM() < 200){
                    beijing.remove(k);
                }
                else{
                    beijingDates.add(beijing.get(k).getDate());
                }
            }
            Collections.sort(beijingDates); 
        }
        else if (city.equals("chengdu")){
            for (int j=0; j<chengdu.size(); j++){
                sum = sum + chengdu.get(j).getPM();
            }
            UI.println("ChengDu's average PM: " + sum/chengdu.size());
        }
        else if (city.equals("guangzhou")){
            for (int j=0; j<guangzhou.size(); j++){
                sum = sum + guangzhou.get(j).getPM();
            }
            UI.println("GuangZhou's average PM: " + sum/guangzhou.size());
        }
        else if (city.equals("shanghai")){
            for (int j=0; j<shanghai.size(); j++){
                sum = sum + shanghai.get(j).getPM();
            }
            UI.println("ShangHai's average PM: " + sum/shanghai.size());
        }
        else if (city.equals("shenyang")){
            for (int j=0; j<shenyang.size(); j++){
                sum = sum + shenyang.get(j).getPM();
            }
            UI.println("ShenYang's average PM: " + sum/shenyang.size());
        }


        
        // to find the longest sequence of days
        for (int j=1; j<365; j++){
        }
        
    }


    // ------------------ Set up the GUI (buttons) ------------------------
    /** Make buttons to let the user run the methods */
    public void setupGUI() {
        UI.initialise();
        UI.addButton("Load", this::loadData);
        UI.addButton("Hazardous Levels", this::findHazardousLevels);
        UI.addButton("Contrasting Cities", this::findContrastingCities);
        UI.addButton("Daily Average", this::findDailyAverage);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0); // text pane only
    }

    public static void main(String[] arguments) {
        AirPollutionAnalyser obj = new AirPollutionAnalyser();
        obj.setupGUI();
    }

}
