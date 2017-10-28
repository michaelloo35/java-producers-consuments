package logic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NativeBoundedBuffer implements Buffer {

    private final Lock lock = new ReentrantLock();
    private final Condition consumerConsumed = lock.newCondition();
    private final Condition producerProduced = lock.newCondition();

    private final List<Object> buffer = new ArrayList<>();
    private final int bound;

    private final List<PlotDot> producersPlotList;
    private final List<PlotDot> consumersPlotList;

    public NativeBoundedBuffer(int bound, List<PlotDot> producersPlotList, List<PlotDot> consumersPlotList) {

        this.bound = bound;
        this.producersPlotList = producersPlotList;
        this.consumersPlotList = consumersPlotList;
    }

    public void putM(List<Object> objects) throws InterruptedException {
        lock.lock();
        try {
            long start = System.nanoTime();

            // wait until there's enough space for products
            while (objects.size() + buffer.size() > bound) {
                consumerConsumed.await();
            }

            // count waiting time and mark it on the list
            long end = System.nanoTime();
            producersPlotList.add(new PlotDot(objects.size(), end - start));

            buffer.addAll(objects);
            producerProduced.signal();
        } finally {
            lock.unlock();
        }
    }

    public List<Object> takeM(int numberOfItems) throws InterruptedException {


        lock.lock();
        try {
            // start counting wait time
            long start = System.nanoTime();

            // wait until there'll be enough items to consume
            while (buffer.size() < numberOfItems) {
                producerProduced.await();
            }

            // count waiting time and mark it on the list
            long end = System.nanoTime();
            consumersPlotList.add(new PlotDot(numberOfItems, end - start));

            List<Object> result = new ArrayList<>();
            for (int i = 0; i < numberOfItems; i++) {
                result.add(buffer.get(0));
                buffer.remove(0);
            }

            // tell producers that some items were consumed
            consumerConsumed.signal();

            return result;
        } finally {
            lock.unlock();
        }

    }

    public String type() {
        return "native";
    }
}