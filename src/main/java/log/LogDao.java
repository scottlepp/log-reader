package log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class LogDao {

  @Autowired
  JdbcTemplate jdbcTemplate;

  public List<String> fetch(LocalDateTime start, LocalDateTime end, String threshold) {
    String sql = "select ip from access_log ";
    sql += "where date >= '" + start + "' AND date <= '" + end + "' ";
    sql += "group by ip having count(ip) > " + threshold;

    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    List<String> ips = new ArrayList<>();
    for (Map<String, Object> item: list) {
      ips.add((String)item.get("ip"));
    }
    return ips;
  }

  public void insert(String[] values) {
    String sql = "insert into access_log values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, values);
  }

  public void batchInsert(List<String[]> rows) {
    String sql = "insert into access_log values (?, ?, ?, ?, ?)";
    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        String[] values = rows.get(i);
        ps.setString(1, values[0]);
        ps.setString(2, values[1]);
        ps.setString(3, values[2]);
        ps.setInt(4, Integer.parseInt(values[3]));
        ps.setString(5, values[4]);
      }

      @Override
      public int getBatchSize() {
        return rows.size();
      }
    });
  }

  public void insertBlocked(String ip, String comment) {
    String sql = "insert into blocked values (?, ?)";
    jdbcTemplate.update(sql, ip, comment);
  }
}
