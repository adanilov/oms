package com.adanilov.toydisruptor.sandbox;

import org.jctools.queues.SpscArrayQueue;

import java.util.Queue;

public class HardToBelieve {
    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> q = new SpscArrayQueue<>(65_536);
        q.add(3);
    }
}
