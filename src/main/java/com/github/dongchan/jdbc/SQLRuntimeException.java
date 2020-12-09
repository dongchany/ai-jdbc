package com.github.dongchan.jdbc;

import java.sql.SQLException;

/**
 * @author Dongchan Year
 */
public class SQLRuntimeException extends RuntimeException {
    public SQLRuntimeException(SQLException cause){
        super(cause);
    }
    public SQLRuntimeException(String message, SQLException cause){
        super(message, cause);
    }

    public SQLRuntimeException(String message) {
        super(message);
    }
}
