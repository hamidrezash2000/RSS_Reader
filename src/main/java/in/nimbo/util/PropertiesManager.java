package in.nimbo.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
    public static final Properties database;
    private static Logger logger = Logger.getLogger(PropertiesManager.class);


    static {
        database = getProperty("database.properties");
    }

    /**
     * this method returns Properties of given source
     *
     * @param src
     * @return Properties
     */
    public static Properties getProperty(String src) {
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(src);
        Properties properties = new Properties();
        try {
            properties.load(resource);
        } catch (IOException e) {
            logger.debug("Couldn't load properties source");
        }
        return properties;
    }
}
