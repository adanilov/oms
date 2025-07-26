package com.adanilov.oms.kafka;

import com.adanilov.oms.OrderMatcher;
import com.adanilov.oms.domain.Order;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Component
public class KafkaConsumerApp {

    @Autowired
    OrderMatcher om;

    @PostConstruct
    public void met() {
        new Thread(this::runConsumer).start();
    }


    public void runConsumer() {
        String topic = "test-topic-order12";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group3123");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read from beginning
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.adanilov.oms.domain");

        try (KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            System.out.println("Listening for messages...");

            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, Order> record : records) {
                    System.out.printf("Received: key=%s, value=%s, offset=%d%n",
                            record.key(), record.value(), record.offset());
                }
            }
        }
    }
}