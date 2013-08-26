package edu.virginia.jinsup;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static void main(String[] args) {

    new JCommander(new Settings(), args);

    // Time conversion from seconds to milliseconds
    Controller.graphFrame = new GraphFrame();
    int buyPrice = (int) (Settings.buyPrice * 100);
    int startTime = Settings.startTime * 1000;
    int endTime = startTime + Settings.tradeTime * 1000;

    MatchingEngine matchingEngine;
    Controller controller;

    matchingEngine = new MatchingEngine(buyPrice, startTime);
    controller = new Controller(startTime, endTime, matchingEngine);
    System.out.println("Starting simulator...");
    long elapsedTime = System.nanoTime();
    controller.runSimulator();
    Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
  }
}
