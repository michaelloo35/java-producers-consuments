package logic.utils;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CSVImporter;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;

import java.io.*;
import java.util.List;

public class CSVWriter {

    public static void generateCSVAndCreatePlot(int m, int p, int c, List<PlotDot> consumers, Buffer buffer, String type) {


        String filename = buffer.type() + type + m + "M" + p + "Producers" + c + "Consumers";
        String path = "results/manipulatedRand/" + m + "/" + buffer.type() + "/" + filename + "/";

        new File(path).mkdirs();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path + filename + ".csv"), "utf-8"))) {

            // set axis names
//            writer.write("waiting-time[ms],items-to-consume\n");

            for (PlotDot pd : consumers) {
                writer.write(pd.toCSVLine());
            }
            writer.close();
            XYChart chart = CSVImporter.getChartFromCSVDir(path, CSVImporter.DataOrientation.Columns, 1280, 720);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            chart.setTitle(filename);
            chart.setXAxisTitle("M");
            chart.setYAxisTitle("wait time[ms]");

            BitmapEncoder.saveJPGWithQuality(chart, path + filename + ".jpg", 0.95F);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
