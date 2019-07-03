import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    private static Logger logger = Logger.getLogger(PropertiesManager.class);

    public static final String DATABASSE = "database.properties";

    /**
     * this method returns Properties of given source
     *
     * @param src
     * @return Properties
     */
    public static Properties getProperty(String src) {
        String propertiesPath = Thread.currentThread().getContextClassLoader().getResource(src).getPath();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            logger.debug("Couldn't load properties source");
        }
        return properties;
    }
}
