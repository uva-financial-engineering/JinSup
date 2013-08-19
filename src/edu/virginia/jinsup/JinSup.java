package edu.virginia.jinsup;

// import java.security.SecureRandom;

// import org.apache.commons.math3.distribution.ExponentialDistribution;
// import org.apache.commons.math3.distribution.PoissonDistribution;
// import org.apache.commons.math3.util.FastMath;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static Settings settings;

  public static final boolean BATCH_MODE = true;

  public static final int NUMBER_OF_RUNS = 10;

  public static void main(String[] args) {

    settings = new Settings();
    new JCommander(settings, args);

    // Price in dollars
    int buyPrice;

    // Time in seconds
    int startTime;
    int endTime;

    // Store directory information when in batch mode
    String logDest;

    // Time conversion from seconds to milliseconds
    if (settings.isSet()) {
      Controller.graphFrame = new GraphFrame(true, null);
      buyPrice = (int) settings.getBuyPrice().doubleValue() * 100;
      startTime = settings.getStartTime() * 1000;
      endTime = startTime + settings.getTradeTime() * 1000;
    } else {
      Controller.graphFrame = new GraphFrame(false, null);
      buyPrice = Controller.graphFrame.getBuyPrice();
      startTime = Controller.graphFrame.getStartTime();
      endTime = Controller.graphFrame.getEndTime();
    }

    logDest = Controller.graphFrame.getDest();
    MatchingEngine matchingEngine;
    Controller controller;

    if (!BATCH_MODE) {
      matchingEngine = new MatchingEngine(buyPrice, startTime, false);
      controller = new Controller(startTime, endTime, matchingEngine);
      System.out.println("Starting simulator...");
      long elapsedTime = System.nanoTime();
      controller.runSimulator();
      Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
    } else {
      Controller.graphFrame.dispose();
      for (int i = 0; i < NUMBER_OF_RUNS; i++) {
        Controller.graphFrame = new GraphFrame(true, logDest);
        matchingEngine = new MatchingEngine(buyPrice, startTime, false);
        controller = new Controller(startTime, endTime, matchingEngine);
        System.out.println("Starting simulator, run " + (i + 1) + " of "
          + NUMBER_OF_RUNS + " ...");
        controller.runSimulator();
        Controller.graphFrame.dispose();
      }
    }
  }
}
