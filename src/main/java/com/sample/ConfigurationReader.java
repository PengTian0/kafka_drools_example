package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationReader {
	public static PropertiesConfiguration getProperties(String configurationFile) {
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().properties()
                                .setFileName(configurationFile)
                                .setThrowExceptionOnMissing(true));
        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Unable to parse configuration. Exiting");
        }
    }


}
