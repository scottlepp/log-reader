package log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class LogWriter {

  @Autowired
  LogDao logDao;

  public void execute(String logFile) throws FileNotFoundException {
    if (logFile == null) {
      logFile = "./access.log";
    }
    Scanner scanner = new Scanner(new BufferedReader(new FileReader(logFile)));
    int batchSize = 1000;
    List<String[]> batch = new ArrayList<>();
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cols = line.split("\\|");
      batch.add(cols);
      if (batch.size() == batchSize) {
        logDao.batchInsert(batch);
        batch.clear();
      }
    }
    logDao.batchInsert(batch);  // insert the last batch
    scanner.close();
  }

}
