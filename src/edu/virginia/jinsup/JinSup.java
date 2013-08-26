package edu.virginia.jinsup;

import java.security.SecureRandom;
import com.beust.jcommander.JCommander;

public class JinSup {

  public static SecureRandom rand = new SecureRandom();

  public static void main(String[] args) {

    new JCommander(new Settings(), args);

    // Time conversion from seconds to milliseconds
    Controller.graphFrame = new GraphFrame();
    int buyPrice = (int) (Settings.getBuyPrice() * 100);
    int startTime = Settings.getStartTime() * 1000;
    int endTime = startTime + Settings.getTradeTime() * 1000;

    MatchingEngine matchingEngine;
    Controller controller;

    matchingEngine = new MatchingEngine(buyPrice, startTime);
    controller =
      new Controller(startTime, endTime, matchingEngine,
        Settings.getThreshold(), Settings.getDelay());
    System.out.println("Starting simulator...");
    long elapsedTime = System.nanoTime();
    controller.runSimulator();
    Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
  }
}
