package ua.foxminded.javastreamsapi;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaStreamsAPI {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("m:ss.SSS");

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
    }

	private static Map<String, String> readAbbreviations(String filePath) throws IOException {
	    return Files.lines(Paths.get(filePath))
	            .map(line -> line.split("_", 3))  
	            .filter(parts -> parts.length == 3)  
	            .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1] + "_" + arr[2]));  
	}

	private static List<Racer> parseData(List<String> startData, List<String> endData,
			Map<String, String> abbreviations) {
		Map<String, LocalDateTime> startTimes = startData.stream().collect(Collectors
				.toMap(line -> line.substring(0, 3), line -> LocalDateTime.parse(line.substring(3), Racer.formatter)));

		Map<String, LocalDateTime> endTimes = endData.stream().collect(Collectors.toMap(line -> line.substring(0, 3),
				line -> LocalDateTime.parse(line.substring(3), Racer.formatter)));

		return startTimes.entrySet().stream().map(entry -> {
			String abbreviation = entry.getKey();
			LocalDateTime startTime = entry.getValue();
			LocalDateTime endTime = endTimes.get(abbreviation);
			String racerInfo = abbreviations.get(abbreviation);

			return new Racer(abbreviation, racerInfo, startTime, endTime);
		}).collect(Collectors.toList());
	}

	private static void printRacers(List<Racer> racers) {
		System.out.println("Top 15 Racers:");

		racers.subList(0, 15).forEach(racer -> {
			System.out.printf("%d. %-20s | %-30s | %s\n", racers.indexOf(racer) + 1, racer.getName(), racer.getTeam(),
					racer.getLapTime());
		});

		System.out.println("------------------------------------------------------------------------");

		System.out.println("Rest of the Racers:");

		racers.subList(15, racers.size()).forEach(racer -> {
			System.out.printf("%d. %-20s | %-30s | %s\n", racers.indexOf(racer) + 1, racer.getName(), racer.getTeam(),
					racer.getLapTime());
		});
	}
}

class Racer {
	static final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
			.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS");

	private String abbreviation;
	private String name;
	private String team;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	public Racer(String abbreviation, String racerInfo, LocalDateTime startTime, LocalDateTime endTime) {
	    this.abbreviation = abbreviation;

	    String[] infoParts = racerInfo.split("_");
	    if (infoParts.length >= 3) {
	        this.name = infoParts[1];
	        this.team = infoParts[2];
	    } else {
	        System.out.println("Warning: Invalid format in abbreviations.txt for " + abbreviation + ": " + racerInfo);
	        this.name = abbreviation; 
	        this.team = "Unknown";
	    }

	    this.startTime = startTime;
	    this.endTime = endTime;
	}


	public String getName() {
		return name;
	}

	public String getTeam() {
		return team;
	}

	public String getLapTime() {
	    Duration lapTime = Duration.between(startTime, endTime);


	    long totalSeconds = lapTime.getSeconds();
	    int millisPart = lapTime.getNano() / 1_000_000; 

	    long minutes = totalSeconds / 60;
	    long seconds = totalSeconds % 60;

	    return String.format("%d:%02d.%03d", minutes, seconds, millisPart);
	}

}
