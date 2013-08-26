package edu.virginia.jinsup;

import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class JinSup {

  public static RandomGenerator randGen;
  public static Random rand;

  public static void main(String[] args) {
    // Read command-line flags
    try {
      new JCommander(new Settings(), args);
    } catch (ParameterException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    // Initialize reusable PRNG instance
    randGen = new MersenneTwister(Settings.getSeed());
    rand = new RandomAdaptor(randGen);

    if (!Settings.isTestMode()) {
      Controller.graphFrame = new GraphFrame();
    }

    // Time conversion from seconds to milliseconds
    int buyPrice = (int) (Settings.getBuyPrice() * 100);
    int startTime = Settings.getStartTime() * 1000;
    int endTime = startTime + Settings.getTradeTime() * 1000;

    MatchingEngine matchingEngine = new MatchingEngine(buyPrice, startTime);
    Controller controller =
      new Controller(startTime, endTime, matchingEngine,
        Settings.getThreshold(), Settings.getDelay());
    long elapsedTime = System.nanoTime();
    controller.runSimulator();
    if (!Settings.isTestMode()) {
      Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
    }
  }
}
