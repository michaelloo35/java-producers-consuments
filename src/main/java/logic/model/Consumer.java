package logic.model;

import logic.utils.Buffer;

import java.util.Random;

public class Consumer implements Runnable {

    private final int limit;
    private final Buffer buffer;
    private final Random rand;

    public Consumer(int limit, Buffer buffer) {

        this.limit = limit;
        this.buffer = buffer;
        rand = new Random();
    }

    public void run() {
        boolean stop = false;
        while (!stop && !Thread.currentThread().isInterrupted()) {

            // first option normal balanced random
//            int numberOfItemsToConsume = rand.nextInt(limit) + 1;

            // second option manipulated random
            int numberOfItemsToConsume = randomizeSmaller() + 1;

            try {
                buffer.takeM(numberOfItemsToConsume);

            } catch (InterruptedException e) {
                // interrupt handling
                stop = true;
                System.out.println("consumer stopped.");
                return;
            }
        }
        System.out.println("consumer stopped.");

    }

    private int randomizeSmaller() {
        int number = rand.nextInt(limit);
        if (number < limit * 0.9) {
            return rand.nextInt(limit / 5);
        }
        return rand.nextInt(limit);
    }

}
