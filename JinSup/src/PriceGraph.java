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

  public PriceGraph(long start, long end) {
    super("JinSup");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    minPrice = Double.MAX_VALUE;
    maxPrice = 0;
    prices = new XYSeries("Sell Orders");
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(prices);
    JFreeChart chart =
      ChartFactory.createXYLineChart("Order Prices", "Time (s)", "Price ($)",
        dataset, PlotOrientation.VERTICAL, true, true, false);
    yAxis = chart.getXYPlot().getRangeAxis();
    chart.getXYPlot().getDomainAxis().setRange(start / 1000.0, end / 1000.0);
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(800, 640));
    setContentPane(chartPanel);
  }

  public void addPoint(double seconds, double price) {
    boolean needResize = false;
    if (price < minPrice) {
      minPrice = price;
      needResize = true;
      if (price > maxPrice) {
        maxPrice = price;
      }
    } else if (price > maxPrice) {
      maxPrice = price;
      needResize = true;
    }
    if (needResize) {
      priceRange = maxPrice - minPrice;
      yAxis.setRange(minPrice - priceRange / 4.0 - 1, maxPrice + priceRange
        / 4.0 + 1);
    }
    prices.add(seconds, price);
  }
}