import java.awt.GridLayout;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class GraphFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  /**
   * First element of array is buy volume at the given price; second element is
   * sell volume.
   */
  private final TreeMap<Integer, Integer[]> orderMap;
  private DefaultCategoryDataset orderDataset;
  private final JFreeChart orderChart;

  private final XYSeries priceCollection;
  private int minTradePrice;
  private int maxTradePrice;
  private final ValueAxis tradeXAxis;
  private final ValueAxis tradeYAxis;
  private double tradeVerticalMargin;

  /**
   * Create the graph window.
   */
  public GraphFrame() {
    super("JinSup");

    minTradePrice = Integer.MAX_VALUE;
    maxTradePrice = 0;
    JPanel window = new JPanel();
    window.setLayout(new GridLayout(2, 1));

    // Order Book graph
    orderMap = new TreeMap<Integer, Integer[]>();
    orderChart =
      ChartFactory.createStackedBarChart3D("Order Book", "Price", "Volume",
        orderDataset, PlotOrientation.VERTICAL, true, true, false);
    orderChart.getCategoryPlot().getDomainAxis()
      .setCategoryLabelPositions(CategoryLabelPositions.UP_90);
    ChartPanel orderPanel = new ChartPanel(orderChart);
    orderPanel.setPreferredSize(new java.awt.Dimension(1000, 300));
    window.add(orderPanel);

    // Trade Prices graph
    priceCollection = new XYSeries("Trades");
    XYSeriesCollection priceDataset = new XYSeriesCollection();
    priceDataset.addSeries(priceCollection);
    JFreeChart tradeChart =
      ChartFactory.createXYLineChart("Trade Prices", "Time", "Price",
        priceDataset, PlotOrientation.VERTICAL, false, true, false);
    tradeXAxis = tradeChart.getXYPlot().getDomainAxis();
    tradeYAxis = tradeChart.getXYPlot().getRangeAxis();
    ChartPanel tradePanel = new ChartPanel(tradeChart);
    tradePanel.setPreferredSize(new java.awt.Dimension(1000, 300));
    window.add(tradePanel);

    // Draw window
    setContentPane(window);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    RefineryUtilities.centerFrameOnScreen(this);
    setVisible(true);
  }

  /**
   * Add a point to the order book graph.
   * 
   * @param isBuy
   *          True if buy order, false if sell order.
   * @param volume
   *          Volume of the order.
   * @param price
   *          Price of the order in cents.
   */
  public void addOrder(boolean isBuy, int volume, int price) {

    Integer[] priceVolume = orderMap.get(price);
    // Update orderMap
    if (priceVolume != null) {
      priceVolume[(isBuy) ? 0 : 1] += volume;
      if (priceVolume[0] == 0 && priceVolume[1] == 0) {
        orderMap.remove(price);
      }
    } else {
      priceVolume = new Integer[2];
      priceVolume[0] = 0;
      priceVolume[1] = 0;
      priceVolume[(isBuy) ? 0 : 1] = volume;
      orderMap.put(price, priceVolume);
    }
    // Rebuild data set
    orderDataset = new DefaultCategoryDataset();
    String zeroPad;
    // TODO Ensure prices increase linearly, i.e. add gaps where buy = sell = 0
    for (Map.Entry<Integer, Integer[]> e : orderMap.entrySet()) {
      zeroPad = ((e.getKey() % 50) > 0) ? "" : "0";
      if (e.getValue()[0] > 0) {
        orderDataset.addValue(e.getValue()[0], "Buy", e.getKey() / 100.0
          + zeroPad);
      }
      if (e.getValue()[1] > 0) {
        orderDataset.addValue(e.getValue()[0], "Sell", e.getKey() / 100.0
          + zeroPad);
      }
    }
    ((CategoryPlot) orderChart.getPlot()).setDataset(orderDataset);
  }

  /**
   * Add a point to the trade price graph.
   * 
   * @param seconds
   *          Time when the order took place.
   * @param price
   *          Price of the order in cents.
   */
  public void addTrade(double seconds, int price) {
    boolean needResize = false;
    if (price < minTradePrice) {
      minTradePrice = price;
      needResize = true;
      if (price > maxTradePrice) {
        maxTradePrice = price;
      }
    } else if (price > maxTradePrice) {
      maxTradePrice = price;
      needResize = true;
    }
    if (needResize) {
      tradeVerticalMargin = (maxTradePrice - minTradePrice) / 4.0;
      tradeYAxis.setRange((minTradePrice - tradeVerticalMargin - 25.0) / 100.0,
        (maxTradePrice + tradeVerticalMargin + 25.0) / 100.0);
    }
    priceCollection.add(seconds, price / 100.0);
  }

  /**
   * Set the minimum and maximum values for the x-axis (time) of the trade price
   * graph.
   * 
   * @param start
   *          Simulation start time in milliseconds.
   * @param end
   *          Simulation end time in milliseconds.
   */
  public void setTradePeriod(long start, long end) {
    tradeXAxis.setRange(start / 1000.0, end / 1000.0);
  }
}