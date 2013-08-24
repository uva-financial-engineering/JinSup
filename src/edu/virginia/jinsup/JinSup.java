package edu.virginia.jinsup;

import java.awt.Frame;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static final boolean BATCH_MODE = true;
  public static final int NUMBER_OF_RUNS = 2;

  /**
   * If true, no trade logging information will be saved and graphs will not be
   * updated. Other logging may still be done, however (e.g. IA profit).
   */
  public static final boolean TEST_MODE = true;

  public static void main(String[] args) {

    new JCommander(new Settings(), args);

    // Time conversion from seconds to milliseconds
    Controller.graphFrame = new GraphFrame();
    int buyPrice = Controller.graphFrame.getBuyPrice();
    int startTime = Controller.graphFrame.getStartTime();
    int endTime = Controller.graphFrame.getEndTime();

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
        Controller.graphFrame = new GraphFrame();
        Controller.graphFrame.setState(Frame.ICONIFIED);
        matchingEngine = new MatchingEngine(buyPrice, startTime, TEST_MODE);

        controller =
          (Settings.isSet() ? new Controller(startTime, endTime,
            matchingEngine, TEST_MODE, Settings.threshold, Settings.delay)
            : new Controller(startTime, endTime, matchingEngine, TEST_MODE));
        System.out.println("Starting simulator, run " + (i + 1) + " of "
          + NUMBER_OF_RUNS + " ...");
        controller.runSimulator();
        Controller.graphFrame.dispose();
      }
    }
  }
}
