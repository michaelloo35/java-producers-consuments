package logic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairBoundedBuffer implements Buffer {

    private final Lock lock = new ReentrantLock();
    private final Condition firstProd = lock.newCondition();
    private final Condition firstCons = lock.newCondition();
    private final Condition otherProd = lock.newCondition();
    private final Condition otherCons = lock.newCondition();
    private boolean firstP = false;
    private boolean firstC = false;

    private final List<PlotDot> producersPlotList;
    private final List<PlotDot> consumersPlotList;

    final List<Object> buffer = new ArrayList<>();
    private final int bound;


    public FairBoundedBuffer(int bound, List<PlotDot> producersPlotList, List<PlotDot> consumersPlotList) {
        this.producersPlotList = producersPlotList;
        this.consumersPlotList = consumersPlotList;
        this.bound = bound;

    }

    public void putM(List<Object> objects) throws InterruptedException {
        lock.lock();
        try {

            long start = System.nanoTime();


            if (!firstP) {
                firstP = true;
                otherProd.await();
            }

            while (objects.size() + buffer.size() > bound) {
                firstProd.await();
            }

            long end = System.nanoTime();
            producersPlotList.add(new PlotDot(objects.size(), end - start));

            buffer.addAll(objects);

            // when done with ur stuff no need to be first anymore
            firstP = false;

            // let other thread take role of first
            otherProd.signal();

            // wake first consumer
            otherCons.signal();
            firstCons.signal();

        } finally {
            lock.unlock();
        }
    }

    public List<Object> takeM(int numberOfItems) throws InterruptedException {
        lock.lock();
        try {
            long start = System.nanoTime();

            if (!firstC) {
                firstC = true;
                otherCons.await();
            }

            while (buffer.size() < numberOfItems) {
                firstProd.signal();
                firstCons.await();
            }

            long end = System.nanoTime();
            consumersPlotList.add(new PlotDot(numberOfItems, end - start));


            // take n objects
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < numberOfItems; i++) {
                result.add(buffer.get(0));
                buffer.remove(0);
            }

            // when done with ur stuff no need to be first anymore
            firstC = false;

            // let other thread take role of first
            otherCons.signal();

            // wake first producer
            otherProd.signal();
            firstProd.signal();
            return result;

        } finally {
            lock.unlock();
        }
    }

    public String type() {
        return "fair";
    }
}