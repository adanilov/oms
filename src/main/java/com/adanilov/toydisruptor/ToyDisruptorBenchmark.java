package com.adanilov.toydisruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ToyDisruptorBenchmark {

    private static final int BUFFER_SIZE = 1024;

    private ToyDisruptor<Long> toyDisruptor;
    private Disruptor<ValueEvent> realDisruptor;
    private RingBuffer<ValueEvent> ringBuffer;
    private ExecutorService executor;

    private static final AtomicLong COUNTER = new AtomicLong();

    @Setup(Level.Iteration)
    public void setup() {
        toyDisruptor = new ToyDisruptor<>(BUFFER_SIZE);
        toyDisruptor.handleEventsWith(event -> {});
        toyDisruptor.start();

        executor = Executors.newSingleThreadExecutor();
        realDisruptor = new Disruptor<>(ValueEvent::new, BUFFER_SIZE, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
        realDisruptor.handleEventsWith((event, sequence, endOfBatch) -> {});
        realDisruptor.start();

        ringBuffer = realDisruptor.getRingBuffer();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        toyDisruptor.shutdown();
        realDisruptor.shutdown();
        executor.shutdown();
    }

    @Benchmark
    public void testToyDisruptor() {
        toyDisruptor.publishEvent(COUNTER.incrementAndGet());
    }

    @Benchmark
    public void testRealDisruptor() {
        long seq = ringBuffer.next();
        ringBuffer.get(seq).set(COUNTER.incrementAndGet());
        ringBuffer.publish(seq);
    }

    public static class ValueEvent {
        private long value;
        public void set(long value) { this.value = value; }
        public long get() { return value; }
    }
}