package edu.virginia.jinsup;

import java.awt.Frame;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static Settings settings;

  /**
   * If true, no trade logging information will be saved and graphs will not be
   * updated. Other logging may still be done, however (e.g. IA profit).
   */

  public static void main(String[] args) {

    settings = new Settings();
    new JCommander(settings, args);

    // Store directory information when in batch mode
    String logDest;
    String configLocation;

    // Time conversion from seconds to milliseconds
    if (settings.isSet()) {
      Controller.graphFrame = new GraphFrame(true, null);
      configLocation = settings.getConfigLocation();
    } else {
      Controller.graphFrame = new GraphFrame(false, null);
      configLocation = Controller.graphFrame.getConfigLocation();
    }

    logDest = Controller.graphFrame.getDest();

    // Load parameters from config file
    Parameters.loadParameters(configLocation);

    MatchingEngine matchingEngine;
    Controller controller;

    int buyPrice = (int) Parameters.buyPrice * 100;
    int startTime = Parameters.startTime * 1000;
    int endTime = startTime + Parameters.tradeTime * 1000;
    boolean testing = Parameters.testing;

    if (!Parameters.batchModeEnable) {
      matchingEngine = new MatchingEngine(buyPrice, startTime, testing);
      controller = new Controller(startTime, endTime, matchingEngine, testing);
      System.out.println("Starting simulator...");
      long elapsedTime = System.nanoTime();
      controller.runSimulator();
      Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
    } else {
      Controller.graphFrame.dispose();
      for (int i = 0; i < Parameters.numberOfRuns; i++) {
        Controller.graphFrame = new GraphFrame(true, logDest);
        Controller.graphFrame.setState(Frame.ICONIFIED);
        matchingEngine = new MatchingEngine(buyPrice, startTime, testing);
        controller =
          new Controller(startTime, endTime, matchingEngine, testing);
        System.out.println("Starting simulator, run " + (i + 1) + " of "
          + Parameters.numberOfRuns + " ...");
        controller.runSimulator();
        Controller.graphFrame.dispose();
      }
    }
  }
}
