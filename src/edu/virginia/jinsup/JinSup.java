package edu.virginia.jinsup;

import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;

import com.beust.jcommander.JCommander;

public class JinSup {

  // Initialize reusable PRNG instance
  public static RandomGenerator randGen = new MersenneTwister();
  public static Random rand = new RandomAdaptor(randGen);

  public static void main(String[] args) {
    // Read command-line flags
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
