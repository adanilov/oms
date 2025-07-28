package com.adanilov.toydisruptor;

public class EventWrapper<T> {
    private T value;
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
}