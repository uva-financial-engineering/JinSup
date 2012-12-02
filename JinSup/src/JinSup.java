import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Scanner;

import com.beust.jcommander.JCommander;

public class JinSup implements ItemListener {

  public static void main(String[] args) {
    Settings settings = new Settings();
    new JCommander(settings, args);
    int buyPrice;
    int startTime;
    int endTime;
    Controller.graphFrame = new GraphFrame();
    if (settings.isSet()) {
      buyPrice = (int) settings.getBuyPrice().doubleValue() * 100;
      startTime = settings.getStartTime() * 1000;
      endTime = startTime + settings.getTradeTime() * 1000;
    } else {
      Scanner scan = new Scanner(System.in);
      System.out
        .println("Enter the buy price in dollars (increments of 0.25): ");
      buyPrice = (int) (scan.nextDouble() * 100);
      System.out.println("Enter the starting period in simulation seconds: ");
      startTime = scan.nextInt() * 1000;
      System.out.println("Enter the end time in simulation seconds: ");
      endTime = scan.nextInt() * 1000;
      scan.close();
    }
    MatchingEngine matchingEngine = new MatchingEngine(buyPrice);
    Controller controller = new Controller(startTime, endTime, matchingEngine);
    System.out.println("Starting simulator...");
    controller.runSimulator();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    // TODO Auto-generated method stub

  }
}