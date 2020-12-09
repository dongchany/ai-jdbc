package com.github.dongchan.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongchan Year
 */
@FunctionalInterface
public interface PreparedStatementSetter {
    PreparedStatementSetter NOOP = new PreparedStatementSetter() {
        @Override
        public void setParameters(PreparedStatement preparedStatement) {
        }
    };

    void setParameters(PreparedStatement preparedStatement) throws SQLException;
}
