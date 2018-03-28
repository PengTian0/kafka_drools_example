package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;

public class KafkaStreamsDroolsMain {
    public static void main(String[] args) throws Exception{
        PropertiesConfiguration properties = ConfigurationReader.getProperties("config.properties");
        KafkaStreamsRunner.runNotificationKafkaStream(properties);
    }
}
