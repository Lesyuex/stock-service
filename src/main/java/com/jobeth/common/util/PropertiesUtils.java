package com.jobeth.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/21 18:26:26
 * Description: -
 */
public class PropertiesUtils {
    public static Properties properties;

    static {
        properties = new Properties();
        ClassLoader classLoader = PropertiesUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("url.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        PropertiesUtils.getByKey("eastMoneyBk");
    }

    public static String getByKey(String key) {
        if (properties == null || properties.getProperty(key) == null) {
            return "";
        }
        return properties.getProperty(key);
    }
}
