package com.github.dongchan.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Dongchan Year
 */
public interface ResultSetMapper<T> {

    T map(ResultSet rs) throws SQLException;
}
