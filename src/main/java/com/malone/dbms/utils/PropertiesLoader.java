package com.malone.dbms.utils;

import java.io.InputStream;
import java.io.IOException;

import java.util.Properties;

public class PropertiesLoader {

    public static Properties loadProperties(String resourceFileName) throws IOException {
        Properties configuration = new Properties();

        InputStream inputStream = PropertiesLoader.class
          .getClassLoader()
          .getResourceAsStream(resourceFileName);

        configuration.load(inputStream);

        inputStream.close();

        return configuration;
    }
}

// String property = configuration.getProperty(key);
