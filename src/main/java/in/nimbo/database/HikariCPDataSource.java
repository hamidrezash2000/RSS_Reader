package in.nimbo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import in.nimbo.util.PropertiesManager;

import java.util.Properties;

class HikariCPDataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    static {
        Properties properties = PropertiesManager.database;
        config.setJdbcUrl(String.format("jdbc:%s/%s?useUnicode=true&characterEncoding=UTF-8",
                properties.getProperty("address"),
                properties.getProperty("database")));
        config.setUsername(properties.getProperty("username"));
        config.setPassword(properties.getProperty("password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    private HikariCPDataSource() {
    }

    static HikariDataSource getDataSource() {
        return dataSource;
    }
}
