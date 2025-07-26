package com.adanilov.oms.kafka;

import com.adanilov.oms.domain.Order;
import com.adanilov.oms.domain.Side;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

public class KafkaProducerApp {
    public static void main(String[] args) {
        String topic = "test-topic-order12";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        try (KafkaProducer<String, Order> producer = new KafkaProducer<>(props)) {
            String key = "key1";
            Order sellOrder = new Order(1, 2, Side.SELL);
            ProducerRecord<String, Order> record = new ProducerRecord<>(topic, key, sellOrder);
            producer.send(record);
            System.out.println("Message sent to Kafka: " + sellOrder);
        }
    }
}