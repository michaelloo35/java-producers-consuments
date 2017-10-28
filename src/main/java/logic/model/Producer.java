package logic.model;

import logic.utils.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Producer implements Runnable {

    private final int limit;
    private final Buffer buffer;
    private final Random rand;

    public Producer(int limit, Buffer buffer) {
        this.limit = limit;
        this.buffer = buffer;
        rand = new Random();

    }

    public void run() {
        boolean stop = false;

        while (!stop && !Thread.currentThread().isInterrupted()) {

            // first option normal balanced random
//            int numberOfItemsToProduce = rand.nextInt(limit) + 1;

            // second option manipulated random
            int numberOfItemsToProduce = randomizeSmaller() + 1;

            List<Object> products = new ArrayList<>();

            for (int i = 0; i < numberOfItemsToProduce; i++) {
                products.add(new Object());
            }

            try {
                long start = System.nanoTime();
                buffer.putM(products);
                long end = System.nanoTime();

            } catch (InterruptedException e) {

                // interrupt handling
                stop = true;
                System.out.println("producer stopped.");
                return;
            }

        }
        System.out.println("producer stopped.");

    }

    private int randomizeSmaller() {
        int number = rand.nextInt(limit);
        if (number < limit * 0.9) {
            return rand.nextInt(limit / 5);
        }
        return rand.nextInt(limit);
    }
}
