package edu.virginia.jinsup;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static Settings settings;

  public static void main(String[] args) {
    settings = new Settings();
    new JCommander(settings, args);

    // Price in dollars
    int buyPrice;

    // Time in seconds
    int startTime;
    int endTime;

    // Time conversion from seconds to milliseconds
    if (settings.isSet()) {
      Controller.graphFrame = new GraphFrame(true);
      buyPrice = (int) settings.getBuyPrice().doubleValue() * 100;
      startTime = settings.getStartTime() * 1000;
      endTime = startTime + settings.getTradeTime() * 1000;
    } else {
      Controller.graphFrame = new GraphFrame(false);
      buyPrice = Controller.graphFrame.getBuyPrice();
      startTime = Controller.graphFrame.getStartTime();
      endTime = Controller.graphFrame.getEndTime();
    }

    MatchingEngine matchingEngine =
      new MatchingEngine(buyPrice, startTime, false);
    Controller controller = new Controller(startTime, endTime, matchingEngine);
    System.out.println("Starting simulator...");
    long elapsedTime = System.nanoTime();
    controller.runSimulator();
    Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
  }
}