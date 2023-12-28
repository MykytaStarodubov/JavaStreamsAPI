package ua.foxminded.javastreamsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;


public class JavaStreamsAPI {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("m:ss.SSS");

    public static void main(String[] args) throws IOException {
        Map<String, String> abbreviations = readAbbreviations("C:\\Users\\mdgxv\\Downloads\\abbreviations.txt");
        List<String> startData = readLinesFromFile("C:\\Users\\mdgxv\\Downloads\\start.log");
        List<String> endData = readLinesFromFile("C:\\Users\\mdgxv\\Downloads\\end.log");

        List<Racer> racers = parseData(startData, endData, abbreviations);

        List<Racer> sortedRacers = racers.stream()
                .sorted(Comparator.comparing(Racer::getLapTime))
                .collect(Collectors.toList());

        printRacers(sortedRacers);
    }

    private static Map<String, String> readAbbreviations(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);
        Map<String, String> abbreviations = lines.stream()
                .map(line -> line.split("_", 3))
                .filter(parts -> parts.length == 3)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1] + "_" + arr[2]));
        return abbreviations;
    }

    private static List<String> readLinesFromFile(String filePath) throws IOException {
    	Path path = Paths.get(filePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    private static List<Racer> parseData(List<String> startData, List<String> endData, Map<String, String> abbreviations) {
        List<Racer> racers = new ArrayList<>();

        for (String startEntry : startData) {
            String abbreviation = startEntry.substring(0, 3);
            String racerInfo = startEntry.substring(3);

            String name = abbreviations.get(abbreviation);
            String team = racerInfo.split("_")[2];

            String endEntry = endData.get(racers.size());

            Racer racer = new Racer(abbreviation, racerInfo, LocalDateTime.parse(startEntry, formatter), LocalDateTime.parse(endEntry, formatter));
            racers.add(racer);
        }

        return racers;
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
