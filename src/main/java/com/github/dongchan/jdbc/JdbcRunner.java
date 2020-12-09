package com.github.dongchan.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Dongchan Year
 */
public class JdbcRunner<T> {
    private static final Logger log = LoggerFactory.getLogger(JdbcRunner.class);
    private final DataSource dataSource;

    public JdbcRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int execute(String query, PreparedStatementSetter setParameters){
        return execute(query, setParameters, (PreparedStatement p) -> p.getUpdateCount());
    }

    public <T> T query(String query, PreparedStatementSetter setParameters, ResultSetMapper<T> resultSetMapper){
        return execute(query, setParameters, (PreparedStatement p) -> mapResultSet(p, resultSetMapper));
    }

    public <T> List<T> query(String query, PreparedStatementSetter setParameters, RowMapper<T> rowMapper){
        return execute(query, setParameters, (PreparedStatement p) -> mapResultSet(p, rowMapper));
    }

    public <T> T execute(String query, PreparedStatementSetter setParameters, AfterExecution<T> afterExecution) {
        return withConnection(c -> {
            try (PreparedStatement ps = c.prepareStatement(query)) {
                log.trace("Setting parameters of prepared staement.");
                setParameters.setParameters(ps);
                log.trace("Executing prepared statement");
                ps.execute();

                return afterExecution.doAfterExecution(ps);
            } catch (SQLException e) {
                throw new SQLRuntimeException("Error when execute.", e);
            }

        });
    }

    private <T> T withConnection(Function<Connection, T> doWithConnection) {
        try (Connection c = dataSource.getConnection()) {
            return doWithConnection.apply(c);
        } catch (SQLException e) {
            throw new SQLRuntimeException("Unable to open connection", e);
        }
    }

    private <T> List<T> mapResultSet(PreparedStatement executePreparedStatement, RowMapper<T> rowMapper){
        return withResultSet(
                executePreparedStatement,
                (ResultSet rs) -> {
                    List<T> results = new ArrayList<>();
                    while (rs.next()){
                        results.add(rowMapper.map(rs));
                    }
                    return results;
                }
        );
    }

    private <T> T mapResultSet(PreparedStatement executePreparedStatement, ResultSetMapper<T> resultSetMapper){
        return withResultSet(
                executePreparedStatement,
                (ResultSet rs) -> resultSetMapper.map(rs)
        );
    }

    private <T> T withResultSet(PreparedStatement executedPreparedStatement, DoWithResultSet<T> doWithResultSet){
        try (ResultSet rs = executedPreparedStatement.getResultSet()){
            return doWithResultSet.withResultSet(rs);
        } catch (SQLException throwables) {
            throw new SQLRuntimeException(throwables);
        }
    }

    interface AfterExecution<T>{
        T doAfterExecution(PreparedStatement executedPreparedStatement) throws SQLException;
    }

    interface DoWithResultSet<T> {
        T withResultSet(ResultSet rs) throws SQLException;
    }
}
