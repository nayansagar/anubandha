package com.spouzee.server.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Sagar on 9/16/2015.
 */
@Component
public class SpzConfig {

    private static Properties properties;

    private static final Logger logger = LoggerFactory.getLogger(SpzConfig.class);

    static{
        properties = new Properties();
        String environment = System.getenv("environment");
        if(environment == null){
            environment = "prod";
        }
        String filename = "config-"+environment+".properties";
        logger.debug("Loading properties from file "+filename);
        try {
            properties.load(SpzConfig.class.getClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            logger.error("Error while loading properties", e);
        }
    }

    public String getStringProperty(String key){
        return properties.getProperty(key);
    }
}
