package com.adanilov.toydisruptor;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CompareRingBuffers {

    public static final int NUM = 20_000_000;

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

        Disruptor<EventWrapper<String>> disruptor = new Disruptor<>(
                EventWrapper::new,
                bufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new BusySpinWaitStrategy()
        );

        disruptor.handleEventsWith((event, sequence, endOfBatch) -> result.add(event.getValue()));
        disruptor.start();

        RingBuffer<EventWrapper<String>> ringBuffer = disruptor.getRingBuffer();
        for (int i = 0; i < NUM; i++) {
            String value = "val" + i;
            long seq = ringBuffer.next();
            try {
                EventWrapper<String> event = ringBuffer.get(seq);
                event.setValue(value);
            } finally {
                ringBuffer.publish(seq);
            }
        }

        Thread.sleep(100);
        disruptor.shutdown();
        return result;
    }

    private static List<String> runToyDisruptorTest() throws Exception {
        int bufferSize = 1024;
        List<String> result = new ArrayList<>();

        ToyDisruptor<String> toy = new ToyDisruptor<>(
                bufferSize
        );

        toy.handleEventsWith(value -> result.add(value));
        toy.start();

        for (int i = 0; i < NUM; i++) {
            String value = "val" + i;
            toy.publishEvent(value);
        }

        Thread.sleep(100);
        toy.shutdown();
        return result;
    }
}