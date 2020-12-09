package com.github.dongchan.jdbc;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.github.dongchan.jdbc.EmbeddedMySQLExtension.getDataSource;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongchan Year
 */
public class JdbcRunnerTest {

    private JdbcRunner jdbcRunner;

    @RegisterExtension
    public EmbeddedMySQLExtension dataExtension = new EmbeddedMySQLExtension();

    @BeforeEach
    public void setUp() {
        jdbcRunner = new JdbcRunner(getDataSource());

        jdbcRunner.execute("create table table1 (column1 INT);", PreparedStatementSetter.NOOP);
    }

    @AfterEach
    public void shutDown(){
        jdbcRunner.execute("drop table table1", PreparedStatementSetter.NOOP);
    }

    @Test
    public void testBasics() {
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

    }

    @Test
    public void testMapMultipleRows(){
        jdbcRunner.execute("insert into table1(column1) values (1)", PreparedStatementSetter.NOOP);
        jdbcRunner.execute("insert into table1(column1) values (2)", PreparedStatementSetter.NOOP);

        final List<Integer> rowMapped = jdbcRunner.query("select * from table1", PreparedStatementSetter.NOOP, new TableRowMapper());
        assertThat(rowMapped, hasSize(2));
        assertThat(rowMapped.get(0), is(1));
        assertThat(rowMapped.get(1), is(2));

        final List<Integer> resultSetMapped = jdbcRunner.query("select * from table1", PreparedStatementSetter.NOOP, new TableRowMapper());
        assertThat(resultSetMapped, hasSize(2));
        assertThat(resultSetMapped.get(0), is(1));
        assertThat(resultSetMapped.get(1), is(2));
    }

    @Test
    public void should_map_constraint_violations_to_custom_exception_for_primary_key_constraint(){
        assertThrows(SQLRuntimeException.class,() -> {
            jdbcRunner.execute("create table table1 ( column1 INT);", PreparedStatementSetter.NOOP);
            jdbcRunner.execute("alter table table1 add constraint col1_uidx unique (column1);", PreparedStatementSetter.NOOP);
            jdbcRunner.execute("insert into table1(column1) values (1)", PreparedStatementSetter.NOOP);
            jdbcRunner.execute("insert into table1(column1) values (1)", PreparedStatementSetter.NOOP);
        });
    }

    private static class TableRowMapper implements RowMapper<Integer> {

        @Override
        public Integer map(ResultSet rs) throws SQLException {
            return rs.getInt("column1");
        }
    }
}
