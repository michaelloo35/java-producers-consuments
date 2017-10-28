package application;

import logic.model.Consumer;
import logic.model.Producer;
import logic.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        List<Integer> mList = List.of(1000, 10000, 100000);
        List<Integer> consumersAndProducers = List.of(10, 100, 1000);
        List<String> bufferTypes = List.of("native", "fair");

        for (Integer m : mList) {
            for (Integer count : consumersAndProducers) {
                for (String bufferType : bufferTypes)
                    go(m, count, count, bufferType);
            }
        }


    }

    private static void go(int m, int p, int c, String bufferType) throws InterruptedException {

        int m2 = 2 * m;

        // set result structures
        List<PlotDot> producersPlotDots = Collections.synchronizedList(new ArrayList<>());
        List<PlotDot> consumersPlotDots = Collections.synchronizedList(new ArrayList<>());

        // set buffer type
        Buffer buffer;
        switch (bufferType) {
            case "native":
                buffer = new NativeBoundedBuffer(m2, producersPlotDots, consumersPlotDots);
                break;
            case "fair":
                buffer = new FairBoundedBuffer(m2, producersPlotDots, consumersPlotDots);
                break;
            default:
                throw new IllegalStateException();
        }

        List<Thread> producerThreads = new ArrayList<>(p);
        List<Thread> consumerThreads = new ArrayList<>(c);

        // initialize nad start producers
        for (int i = 0; i < p; i++) {
            Thread prod = new Thread(new Producer(m, buffer));
            prod.start();
            producerThreads.add(prod);
        }
        for (int i = 0; i < c; i++) {
            Thread cons = new Thread(new Consumer(m, buffer));
            cons.start();
            consumerThreads.add(cons);
        }

        // let threads do their job for some time
        sleep(15000);

        // kill'em all
        consumerThreads.forEach(Thread::interrupt);
        producerThreads.forEach(Thread::interrupt);


        for (Thread thread : producerThreads) {
            thread.join();
        }

        for (Thread thread : consumerThreads) {
            thread.join();
        }

        // generate csv

        CSVWriter.generateCSVAndCreatePlot(m, p, c, producersPlotDots, buffer,"Producers");
        CSVWriter.generateCSVAndCreatePlot(m, p, c, producersPlotDots, buffer,"Consumers");
    }


}
