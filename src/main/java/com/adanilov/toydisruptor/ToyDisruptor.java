package com.adanilov.toydisruptor;

public class ToyDisruptor<T> {
    private Thread consumerThread;
    private final T[] buffer;
    private volatile boolean running = true;
    private java.util.function.Consumer<T> handler;
    private final int bufferSize;
    private volatile int writePos = 0;
    private volatile int readPos = 0;

    @SuppressWarnings("unchecked")
    public ToyDisruptor(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = (T[]) new Object[bufferSize];
    }

    public void publishEvent(T event) {
        while (writePos - readPos >= bufferSize) {
            Thread.onSpinWait();
        }

        buffer[writePos % bufferSize] = event;
        writePos++;
    }

    public void shutdown() {
        running = false;
        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void handleEventsWith(java.util.function.Consumer<T> handler) {
        this.handler = handler;
    }

    public void start() {
        consumerThread = new Thread(() -> {
            while (running || readPos < writePos) {
                if (readPos < writePos) {
                    T val = buffer[readPos % bufferSize];
                    handler.accept(val);
                    buffer[readPos % bufferSize] = null;
                    readPos++;
                } else {
                    Thread.onSpinWait();
                }
            }
        });
        consumerThread.start();
    }
}