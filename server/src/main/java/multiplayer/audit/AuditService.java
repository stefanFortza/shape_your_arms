package multiplayer.audit;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static final String FILE_PATH = "audit_log.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void logAction(String actionName) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.write(actionName + ", " + timestamp + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void deleteCsv() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.write("Audit log deleted, " + timestamp + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}