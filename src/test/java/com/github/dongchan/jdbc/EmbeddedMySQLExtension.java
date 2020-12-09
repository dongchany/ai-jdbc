package com.github.dongchan.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

/**
 * @author Dongchan Year
 */
public class EmbeddedMySQLExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

    }

    public static DataSource getDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://192.168.43.251:33081/easesqlbot?useUnicode=true&characterEncoding=utf-8&useSSL=false&useTimezone=true&serverTimezone=GMT%2B8");
        config.setUsername("super");
        config.setPassword("super");
        return new HikariDataSource(config);
    }
}
