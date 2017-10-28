package logic.utils;


import java.util.Locale;

public class PlotDot {
    private final long x;
    private final long y;

    public PlotDot(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public String toCSVLine() {
        return x + "," + String.format(Locale.ROOT, "%.2f", nanoToMilli(y)) + "\n";
    }

    private double nanoToMilli(long nano) {
        return nano / 100_000_0.;
    }
}
