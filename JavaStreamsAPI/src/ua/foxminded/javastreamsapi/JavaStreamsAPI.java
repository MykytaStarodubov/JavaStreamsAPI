package ua.foxminded.javastreamsapi;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaStreamsAPI {
	  public static void main(String[] args) {
	        try {
	           
	        	Map<String, String> abbreviations = readAbbreviations("C:\\Users\\mdgxv\\Downloads\\abbreviations.txt");
	        	List<String> startData = Files.readAllLines(Paths.get("C:\\Users\\mdgxv\\Downloads\\start.log"));
	        	List<String> endData = Files.readAllLines(Paths.get("C:\\Users\\mdgxv\\Downloads\\end.log"));
	           
	            List<Racer> racers = parseData(startData, endData, abbreviations);

	     
	            List<Racer> sortedRacers = racers.stream()
	                    .sorted(Comparator.comparing(Racer::getLapTime))
	                    .collect(Collectors.toList());

	          
	            printRacers(sortedRacers);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	  }}

	    