package com.adanilov.toydisruptor;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TestRB {

    public static void main(String[] args) throws Exception {
        List<String> realResults = runRealDisruptorTest();
        List<String> toyResults = runToyDisruptorTest();

        if (realResults.equals(toyResults)) {
            System.out.println("Functional output matches!");
        } else {
            System.out.println("Mismatch detected:");
            System.out.println("Real: " + realResults);
            System.out.println("Toy: " + toyResults);
        }
    }

    private static List<String> runRealDisruptorTest() throws Exception {
        int bufferSize = 1024;
        List<String> result = new ArrayList<>();

        Disruptor<MyEvent<String>> disruptor = new Disruptor<>(
                MyEvent::new,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BusySpinWaitStrategy()
        );

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> result.add(event.getValue()));
        disruptor.start();

        RingBuffer<MyEvent<String>> ringBuffer = disruptor.getRingBuffer();
        for (int i = 0; i < 10; i++) {
            String value = "val" + i;
            long seq = ringBuffer.next();
            try {
                MyEvent<String> event = ringBuffer.get(seq);
                event.setValue(value);
            } finally {
                ringBuffer.publish(seq);
            }
        }

        // Wait a bit to let consumer process
        Thread.sleep(100);
        disruptor.shutdown();
        return result;
    }

    private static List<String> runToyDisruptorTest() throws Exception {
        List<String> result = new ArrayList<>();

        ToyDisruptor<String> toy = new MyToyDisruptor<>(value -> result.add(value));
        for (int i = 0; i < 10; i++) {
            toy.publishEvent("val" + i);
        }

        // Wait a bit if needed
        Thread.sleep(100);
        toy.shutdown();
        return result;
    }
}