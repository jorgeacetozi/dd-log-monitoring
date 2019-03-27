package com.jorgeacetozi.dd.logmonitoring.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesUtil {

  private static final Logger LOGGER = Logger.getLogger(PropertiesUtil.class.getName());
  private static Properties prop = new Properties();

  static {
    InputStream input = null;

    try {
      input = new FileInputStream("./dd-log-monitoring.properties");
      prop.load(input);

      if (prop.isEmpty()) {

      }
    } catch (IOException ex) {
      LOGGER.info(
          "Properties file dd-log-monitoring.properties not found. Falling back to default values.");
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static Integer getOrDefault(String key, Integer defaultValue) {
    return prop.getProperty(key) != null ? Integer.parseInt(prop.getProperty(key)) : defaultValue;
  }

  public static String getOrDefault(String key, String defaultValue) {
    return prop.getProperty(key) != null ? prop.getProperty(key) : defaultValue;
  }
}
