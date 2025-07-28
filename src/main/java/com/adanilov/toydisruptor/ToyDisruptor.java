package com.adanilov.toydisruptor;

public interface ToyDisruptor<T> {
    void publishEvent(T event);
    void shutdown();
}