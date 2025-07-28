package com.adanilov.toydisruptor;

public class ToyDisruptor<T> {
    private Thread consumerThread;
    private final T[] buffer;
    private volatile boolean running = true;
    private java.util.function.Consumer<T> handler;
    private volatile int writePos = 0;
    private volatile int readPos = 0;

    @SuppressWarnings("unchecked")
    public ToyDisruptor(int size) {
        this.buffer = (T[]) new Object[size];
    }

    public void publishEvent(T event) {
        buffer[writePos % buffer.length] = event;
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
}