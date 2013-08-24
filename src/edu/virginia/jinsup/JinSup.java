package edu.virginia.jinsup;

import java.awt.Frame;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static final boolean BATCH_MODE = true;
  public static final int NUMBER_OF_RUNS = 30;

  /**
   * If true, no trade logging information will be saved and graphs will not be
   * updated. Other logging may still be done, however (e.g. IA profit).
   */
  public static final boolean TEST_MODE = true;

  public static void main(String[] args) {

    new JCommander(new Settings(), args);

    // Price in dollars
    int buyPrice;

    // Time in seconds
    int startTime;
    int endTime;

    // IA Parameters, delay in ms
    int threshold = 0;
    int delay = 0;

    // Time conversion from seconds to milliseconds
    if (Settings.isSet()) {
      Controller.graphFrame = new GraphFrame(true, null);
      buyPrice = (int) Settings.buyPrice.doubleValue() * 100;
      startTime = Settings.startTime * 1000;
      endTime = startTime + Settings.tradeTime * 1000;
      threshold = Settings.threshold;
      delay = Settings.delay;
    } else {
      Controller.graphFrame = new GraphFrame(false, null);
      buyPrice = Controller.graphFrame.getBuyPrice();
      startTime = Controller.graphFrame.getStartTime();
      endTime = Controller.graphFrame.getEndTime();
    }

    MatchingEngine matchingEngine;
    Controller controller;

    if (!BATCH_MODE) {
      matchingEngine = new MatchingEngine(buyPrice, startTime, TEST_MODE);
      controller =
        new Controller(startTime, endTime, matchingEngine, TEST_MODE);
      System.out.println("Starting simulator...");
      long elapsedTime = System.nanoTime();
      controller.runSimulator();
      Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
    } else {
      Controller.graphFrame.dispose();
      for (int i = 0; i < NUMBER_OF_RUNS; i++) {
        Controller.graphFrame = new GraphFrame(true, Settings.dest);
        Controller.graphFrame.setState(Frame.ICONIFIED);
        matchingEngine = new MatchingEngine(buyPrice, startTime, TEST_MODE);

        controller =
          (Settings.isSet() ? new Controller(startTime, endTime,
            matchingEngine, TEST_MODE, threshold, delay) : new Controller(
            startTime, endTime, matchingEngine, TEST_MODE));
        System.out.println("Starting simulator, run " + (i + 1) + " of "
          + NUMBER_OF_RUNS + " ...");
        controller.runSimulator();
        Controller.graphFrame.dispose();
      }
    }
  }
}
