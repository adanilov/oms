package com.adanilov.toydisruptor;

public class MyToyDisruptor<T> implements ToyDisruptor<T> {
    private final Thread consumerThread;
    private final T[] buffer;
    private volatile boolean running = true;
    private final java.util.function.Consumer<T> handler;
    private volatile int writePos = 0;
    private volatile int readPos = 0;

    @SuppressWarnings("unchecked")
    public MyToyDisruptor(java.util.function.Consumer<T> handler) {
        this.handler = handler;
        this.buffer = (T[]) new Object[1024];  // fixed buffer size
        consumerThread = new Thread(() -> {
            while (running || readPos != writePos) {
                if (readPos != writePos) {
                    T val = buffer[readPos % buffer.length];
                    handler.accept(val);
                    readPos++;
                }
            }
        });
        consumerThread.start();
    }

    @Override
    public void publishEvent(T event) {
        buffer[writePos % buffer.length] = event;
        writePos++;
    }

    @Override
    public void shutdown() {
        running = false;
        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}