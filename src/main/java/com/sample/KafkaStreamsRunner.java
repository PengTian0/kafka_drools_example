package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

/**
 * Runs the Kafka Streams job.
 */
public class KafkaStreamsRunner {

    private KafkaStreamsRunner() {
        //To prevent instantiation
    }

    private static boolean filterNonNull(byte[] key, String value){
        return value != null && value != "";
    }

    /**
     * Runs the Kafka Streams job.
     *
     * @param properties the configuration for the job
     * @return the Kafka Streams instance
     */
    public static KafkaStreams runKafkaStream(PropertiesConfiguration properties) throws Exception {
        //String droolsRuleName = properties.getString("droolsRuleName");
        DroolsRulesApplier rulesApplier = new DroolsRulesApplier();
        KStreamBuilder builder = new KStreamBuilder();
        String inputTopic = properties.getString("kpiFilterInputTopic");
        String outputTopic = properties.getString("kpiFilterOutputTopic");
        KStream<byte[], String> inputData = builder.stream(inputTopic);
        KStream<byte[], String> outputData = inputData.mapValues(rulesApplier::applyKpiFilterRule);
        outputData.filter((key, value) -> value != null)
                  .mapValues(rulesApplier::applyKpiComputeRule)
                  .to(outputTopic);
        /*
        String computeInputTopic = properties.getString("kpiComputeInputTopic");
        String computeOutputTopic = properties.getString("kpiComputeOutputTopic");
        KStream<byte[], String> computeInputData = builder.stream(computeInputTopic);
        KStream<byte[], String> computeOutputData = computeInputData.mapValues(rulesApplier::applyKpiComputeRule);

        computeOutputData.to(computeOutputTopic);
        */
        Properties streamsConfig = createStreamConfig(properties);
        KafkaStreams streams = new KafkaStreams(builder, streamsConfig);
        System.out.println("start kafka streams");
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        return streams;
    }

    /**
     * Creates the Kafka Streams configuration.
     *
     * @param properties the configuration for the job
     * @return the Kafka Streams configuration in a Properties object
     */
    private static Properties createStreamConfig(PropertiesConfiguration properties) {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, properties.getString("applicationName"));
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getString("bootstrapServers"));
        streamsConfiguration.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, properties.getString("zookeeperServers"));
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        return streamsConfiguration;
    }
}
