package com.github.dongchan.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Dongchan Year
 */
public interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
