package com.adanilov.toydisruptor;

public class ToyDisruptor<T> {
    private final int mask;
    private Thread consumerThread;
    private final EventWrapper<T>[] buffer;
    private volatile boolean running = true;
    private java.util.function.Consumer<T> handler;
    private final int bufferSize;
    private volatile long writePos = 0;
    private volatile long readPos = 0;

    @SuppressWarnings("unchecked")
    public ToyDisruptor(int bufferSize) {
        this.bufferSize = bufferSize;
        this.mask = bufferSize - 1;
        this.buffer = new EventWrapper[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            buffer[i] = new EventWrapper<T>();
        }
    }

    public void publishEvent(T event) {
        while (writePos - readPos >= bufferSize) {
            Thread.onSpinWait();
        }

        EventWrapper<T> eventWrapper = buffer[(int) (writePos & mask)];
        eventWrapper.setValue(event);
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
                    EventWrapper<T> eventWrapper = buffer[(int) (readPos & mask)];
                    handler.accept(eventWrapper.getValue());
                    eventWrapper.setValue(null);
                    readPos++;
                } else {
                    Thread.onSpinWait();
                }
            }
        });
        consumerThread.start();
    }
}