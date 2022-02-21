package com.njzq.dataDemo.dataDemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author LH
 * @description:
 * @date 2021-11-23 16:59
 */
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    public static Properties getConf(String str) {
        Properties prop = new Properties();
        try {
            //读取属性文件a.properties
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(str);
            prop.load(inputStream);
        } catch (Exception e) {
            logger.debug(e.toString());
        }
        return prop;
    }
}
