package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;

public class KafkaStreamsDroolsMain {

    public static void main(String[] args) throws Exception{
        System.out.println("start main app");
        PropertiesConfiguration properties = ConfigurationReader.getProperties("config.properties");
        KafkaStreamsRunner.runNotificationKafkaStream(properties);
    }
}
