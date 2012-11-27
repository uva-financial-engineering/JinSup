import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraphFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  // Order book graph variables
  DefaultCategoryDataset orderDataset;

  // Trade price graph variables
  private final XYSeries priceCollection;
  private double minPrice;
  private double maxPrice;
  private final ValueAxis yAxis;
  private double priceRange;

  /**
   * Create the graph window.
   * 
   * @param start
   *          Trading start time in milliseconds
   * @param end
   *          Trading end time in milliseconds
   */
  public GraphFrame(long start, long end) {
    super("JinSup");

    minPrice = Double.MAX_VALUE;
    maxPrice = 0;
    JPanel window = new JPanel();
    window.setLayout(new GridLayout(2, 1));

    // Order Book graph
    orderDataset = new DefaultCategoryDataset();
    orderDataset.setValue(6, "Buy", "0.97");
    orderDataset.setValue(3, "Buy", "0.98");
    orderDataset.setValue(7, "Buy", "0.99");
    orderDataset.setValue(10, "Buy", "1.00");
    orderDataset.setValue(8, "Buy", "1.01");
    orderDataset.setValue(8, "Sell", "1.02");
    orderDataset.setValue(5, "Sell", "1.03");
    orderDataset.setValue(6, "Sell", "1.04");
    orderDataset.setValue(12, "Sell", "1.05");
    orderDataset.setValue(5, "Sell", "1.06");
    JFreeChart orderChart =
      ChartFactory.createBarChart3D("Order Book", "Price", "Volume",
        orderDataset, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel orderPanel = new ChartPanel(orderChart);
    orderPanel.setPreferredSize(new java.awt.Dimension(800, 300));
    window.add(orderPanel);

    // Trade Prices graph
    priceCollection = new XYSeries("Trades");
    XYSeriesCollection priceDataset = new XYSeriesCollection();
    priceDataset.addSeries(priceCollection);
    JFreeChart tradeChart =
      ChartFactory.createXYLineChart("Trade Prices", "Time", "Price",
        priceDataset, PlotOrientation.VERTICAL, false, true, false);
    yAxis = tradeChart.getXYPlot().getRangeAxis();
    tradeChart.getXYPlot().getDomainAxis()
      .setRange(start / 1000.0, end / 1000.0);
    ChartPanel tradePanel = new ChartPanel(tradeChart);
    tradePanel.setPreferredSize(new java.awt.Dimension(800, 300));
    window.add(tradePanel);

    // Draw window
    setContentPane(window);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  /**
   * Add a point to the order book graph.
   * 
   * @param isBuy
   *          True if buy order, false if sell order.
   * @param volume
   *          Volume of the order.
   */
  public void addOrder(boolean isBuy, int volume, long price) {
    orderDataset.setValue(volume, isBuy ? "Buy" : "Sell", price + "");
  }

  /**
   * Add a point to the trade price graph.
   * 
   * @param seconds
   *          Time when the order took place
   * @param price
   *          Price of the order
   */
  public void addTrade(double seconds, double price) {
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
    priceCollection.add(seconds, price);
  }
}