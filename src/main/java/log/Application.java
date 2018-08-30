package log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

@SpringBootApplication
public class Application implements CommandLineRunner {

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(Application.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setWebApplicationType(WebApplicationType.NONE);
    app.run(args);
  }

  @Autowired
  LogDao logDao;

  @Autowired
  LogWriter logWriter;

  @Override
  public void run(String... args) throws Exception {

    Map<String, String> commandLineArgs = getCliArgs(args);

    String start = commandLineArgs.get("startDate");
    String duration = commandLineArgs.get("duration");
    String threshold = commandLineArgs.get("threshold");
    String accessLog = commandLineArgs.get("accesslog");
    String bypass = commandLineArgs.get("bypass");

    if (start != null && duration != null && threshold != null) {
      // allow bypassing of inserting the log file into mysql
      boolean load = bypass == null || "false".equals(bypass);

      if (load) {
        logWriter.execute(accessLog);
      }

      fetchIPs(start, duration, threshold);
    } else {
      System.out.println("Missing args");
    }

    exit(0);
  }

  private void fetchIPs(String start, String duration, String threshold) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
    LocalDateTime startDate = LocalDateTime.parse(start, formatter);
    LocalDateTime endDate = getEndDate(startDate, duration);
    List<String> ipList = logDao.fetch(startDate, endDate, threshold);
    for (String ip: ipList) {
      System.out.println(ip);
      logDao.insertBlocked(ip, "unknown");
    }
  }

  private LocalDateTime getEndDate(LocalDateTime startDate, String duration) {
    LocalDateTime endDate;
    if (duration.equals("hourly")) {
      endDate = startDate.plusHours(1);
    } else {
      endDate = startDate.plusDays(1);
    }
    return endDate;
  }

  private Map<String,String> getCliArgs(String... args) {
    Map<String, String> commandLineArgs = new HashMap<>();
    for (String arg: args) {
      String[] parts = arg.split("=");
      commandLineArgs.put(parts[0].replace("--",""), parts[1]);
    }
    return commandLineArgs;
  }
}
