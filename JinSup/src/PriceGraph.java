import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PriceGraph extends JFrame {

  private static final long serialVersionUID = 1L;
  private final XYSeries prices;
  private double minPrice;
  private double maxPrice;
  private final ValueAxis yAxis;
  private double priceRange;

  public PriceGraph(String applicationTitle, String chartTitle) {
    super(applicationTitle);
    minPrice = Double.MAX_VALUE;
    maxPrice = 0;
    prices = new XYSeries("Price");
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(prices);
    JFreeChart chart =
      ChartFactory.createXYLineChart(chartTitle, "Time", "Price", dataset,
        PlotOrientation.VERTICAL, true, true, false);
    yAxis = chart.getXYPlot().getRangeAxis();
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(800, 640));
    setContentPane(chartPanel);
  }

  public void addPoint(double seconds, double price) {
    boolean needResize = false;
    if (price < minPrice) {
      minPrice = price;
      needResize = true;
    }
    if (price > maxPrice) {
      maxPrice = price;
      needResize = true;
    }
    if (needResize) {
      priceRange = maxPrice - minPrice;
      yAxis.setRange(minPrice - priceRange / 4 - 1, maxPrice + priceRange / 4
        + 1);
      System.out.println(minPrice + ", " + maxPrice);
    }
    prices.add(seconds, price);
  }
}