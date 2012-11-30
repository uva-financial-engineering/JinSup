import java.awt.GridLayout;
import java.util.TreeMap;

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
import org.jfree.ui.RefineryUtilities;

public class GraphFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  /**
   * First element of array is buy volume at the given price; second element is
   * sell volume.
   */
  private final TreeMap<Integer, Integer[]> orderMap;
  private final XYSeries orderCollection;
  private int minOrderPrice;
  private int maxOrderPrice;
  private final DefaultCategoryDataset orderDataset;

  private final XYSeries priceCollection;
  private int minTradePrice;
  private int maxTradePrice;
  private final ValueAxis tradeXAxis;
  private final ValueAxis tradeYAxis;
  private double priceRange;

  /**
   * Create the graph window.
   */
  public GraphFrame() {
    super("JinSup");

    minOrderPrice = Integer.MAX_VALUE;
    minTradePrice = Integer.MAX_VALUE;
    minOrderPrice = 0;
    maxTradePrice = 0;
    JPanel window = new JPanel();
    window.setLayout(new GridLayout(2, 1));

    // Order Book graph
    orderMap = new TreeMap<Integer, Integer[]>();
    orderCollection = new XYSeries("Orders");
    orderDataset = new DefaultCategoryDataset();
    // orderDataset.setValue(6, "Buy", "0.97");
    // orderDataset.setValue(3, "Buy", "0.98");
    // orderDataset.setValue(7, "Buy", "0.99");
    // orderDataset.setValue(10, "Buy", "1.00");
    // orderDataset.setValue(8, "Buy", "1.01");
    // orderDataset.setValue(8, "Sell", "1.02");
    // orderDataset.setValue(5, "Sell", "1.03");
    // orderDataset.setValue(6, "Sell", "1.04");
    // orderDataset.setValue(12, "Sell", "1.05");
    // orderDataset.setValue(5, "Sell", "1.06");
    JFreeChart orderChart =
      ChartFactory.createStackedBarChart3D("Order Book", "Price", "Volume",
        orderDataset, PlotOrientation.VERTICAL, true, true, false);
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
    // Number currentVolume =
    // orderDataset.getValue(isBuy ? "Buy" : "Sell", price / 100.0);
    // if (currentVolume != null) {
    // volume += currentVolume.intValue();
    // }

    // if (price < minOrderPrice) {
    // minOrderPrice = price;
    // }
    // if (price > maxOrderPrice) {
    // maxOrderPrice = price;
    // }
    // Integer[] priceVolume = orderMap.get(price);
    // if (priceVolume != null) {
    // priceVolume[(isBuy) ? 0 : 1] += volume;
    // if (priceVolume[0] == 0 && priceVolume[1] == 0) {
    // orderMap.remove(price);
    // }
    // } else {
    // priceVolume = new Integer[2];
    // priceVolume[0] = 0;
    // priceVolume[1] = 0;
    // priceVolume[(isBuy) ? 0 : 1] = volume;
    // orderMap.put(price, priceVolume);
    // }
    // DefaultCategoryDataset newOrderDataset = new DefaultCategoryDataset();
    // for (Map.Entry<Integer, Integer[]> e : orderMap.entrySet()) {
    // if (e.getValue()[0] > 0) {
    // newOrderDataset.addValue(volume, "Buy", e.getValue()[0]);
    // }
    // if (e.getValue()[1] > 0) {
    // newOrderDataset.addValue(volume, "Sell", e.getValue()[1]);
    // }
    // }
    // orderDataset = newOrderDataset;
    orderDataset.setValue(volume, isBuy ? "Buy" : "Sell", price / 100.0 + "");
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
      priceRange = maxTradePrice - minTradePrice;
      tradeYAxis.setRange((minTradePrice - priceRange / 4.0 - 25.0) / 100.0,
        (maxTradePrice + priceRange / 4.0 + 25.0) / 100.0);
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