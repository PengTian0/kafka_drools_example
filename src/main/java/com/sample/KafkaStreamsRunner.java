package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.ArrayList;
/**
 * Runs the Kafka Streams job.
 */
public class KafkaStreamsRunner {

    private KafkaStreamsRunner() {
    }

    public static KafkaProducer<String, String> createProducer(PropertiesConfiguration properties) {
        Properties props = new Properties();
        props.put("bootstrap.servers", properties.getString("bootstrapServers"));
        props.put("acks", "all");   
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", 
         "org.apache.kafka.common.serialization.StringSerializer");
         
        props.put("value.serializer", 
         "org.apache.kafka.common.serialization.StringSerializer");
      
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    public static void runNotificationKafkaStream(PropertiesConfiguration properties) throws Exception {
        DroolsRulesApplier rulesApplier = new DroolsRulesApplier(properties);
        KStreamBuilder builder = new KStreamBuilder();
        String inputTopic = properties.getString("eventInputTopic");
        String outputTopic = properties.getString("notificationOutputTopic");

        KStream<byte[], String> inputData = builder.stream(inputTopic);
        KafkaProducer<String, String> producer = createProducer(properties);

        inputData.mapValues(rulesApplier::applyNotificationRule);

        Properties streamsConfig = createStreamConfig(properties);
        KafkaStreams streams = new KafkaStreams(builder, streamsConfig);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));
        
        while(true){
            ArrayList<String> messages = rulesApplier.getNotificationRequest();
            if(messages != null && messages.size() > 0){
                for(String message: messages) {
                    producer.send(new ProducerRecord<String, String>(outputTopic, message, message));
                }
                rulesApplier.resetNotifications();
            }
            java.util.concurrent.TimeUnit.SECONDS.sleep(1);
            rulesApplier.runRules("notification");
        }
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
