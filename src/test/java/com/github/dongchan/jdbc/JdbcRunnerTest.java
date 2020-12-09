package com.github.dongchan.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Dongchan Year
 */
public class JdbcRunnerTest {

    private JdbcRunner jdbcRunner;

    @BeforeEach
    public void setUp() {
        jdbcRunner = new JdbcRunner(getDataSource());
    }

    @Test
    public void testBasics() {
        jdbcRunner.execute("create table table1 (column1 INT);", PreparedStatementSetter.NOOP);
        final int inserted = jdbcRunner.execute("insert into table1(column1) values (?)", ps -> ps.setInt(1, 1));
        assertThat(inserted, is(1));

        final List<Integer> rowMapped = jdbcRunner.query("select * from table1", PreparedStatementSetter.NOOP, new TableRowMapper());
        assertThat(rowMapped, hasSize(1));
        assertThat(rowMapped.get(0), is(1));

        assertThat(jdbcRunner.query("select * from table1", PreparedStatementSetter.NOOP, Mappers.SINGLE_INT),is(1));

        final int updated = jdbcRunner.execute("update table1 set column1 = ? where column1 = ?",
                ps -> {
                    ps.setInt(1, 5);
                    ps.setInt(2, 1);
                });
        assertThat(updated, is(1));

        jdbcRunner.execute("drop table table1", PreparedStatementSetter.NOOP);
    }

    private DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://192.168.43.251:33081/easesqlbot?useUnicode=true&characterEncoding=utf-8&useSSL=false&useTimezone=true&serverTimezone=GMT%2B8");
        config.setUsername("super");
        config.setPassword("super");
        return new HikariDataSource(config);
    }

    private static class TableRowMapper implements RowMapper<Integer> {

        @Override
        public Integer map(ResultSet rs) throws SQLException {
            return rs.getInt("column1");
        }
    }
}
