import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple data-save helper for tasks.
 *
 * File format: one task per line: <urlencoded-task-name>\t<yyyy-MM-dd>\n
 */
public class DSSave {
    private static final String DEFAULT_FILENAME = "tasks.txt";

    public static void save(List<Task> tasks) throws IOException {
        saveToFile(tasks, DEFAULT_FILENAME);
    }

    public static void saveToFile(List<Task> tasks, String filename) throws IOException {
        if (tasks == null) tasks = Collections.emptyList();
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            String nameEnc = URLEncoder.encode(t.getTaskName(), StandardCharsets.UTF_8.toString());
            String date = t.getDueDate().toString();
            lines.add(nameEnc + "\t" + date);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static List<Task> load() throws IOException {
        return loadFromFile(DEFAULT_FILENAME);
    }

    public static List<Task> loadFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String[] parts = line.split("\t", 2);
            if (parts.length != 2) continue;
            String name = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.toString());
            LocalDate date = LocalDate.parse(parts[1]);
            tasks.add(new Task(name, date));
        }
        return tasks;
    }
}
