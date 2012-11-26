import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

public class PieChart extends JFrame {

  private static final long serialVersionUID = 1L;

  public PieChart(String applicationTitle, String chartTitle) {
    super(applicationTitle);

    // This will create the dataset
    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Linux", 29);
    dataset.setValue("Mac", 20);
    dataset.setValue("Windows", 51);

    // based on the dataset we create the chart
    JFreeChart chart = ChartFactory.createPieChart3D(chartTitle, // chart title
      dataset, // data
      true, // include legend
      true, false);

    PiePlot3D plot = (PiePlot3D) chart.getPlot();
    plot.setStartAngle(290);
    plot.setDirection(Rotation.CLOCKWISE);
    plot.setForegroundAlpha(0.5f);

    // we put the chart into a panel
    ChartPanel chartPanel = new ChartPanel(chart);

    // default size
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

    // add it to our application
    setContentPane(chartPanel);

  }
}