package edu.virginia.jinsup;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

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

    // Initialize reusable RNG instance
    String rng = Settings.getRNG().toLowerCase();
    if (rng.equals("mersenne")) {
      randGen = new MersenneTwister(Settings.getSeed());
      rand = new RandomAdaptor(randGen);
    } else if (rng.equals("well19937c")) {
      randGen = new Well19937c(Settings.getSeed());
      rand = new RandomAdaptor(randGen);
    } else if (rng.equals("secure")) {
      rand = new SecureRandom();
      rand.setSeed(Settings.getSeed());
      randGen = new AbstractRandomGenerator() {
        @Override
        public boolean nextBoolean() {
          return rand.nextBoolean();
        }

        @Override
        public void nextBytes(byte[] bytes) {
          rand.nextBytes(bytes);
        }

        @Override
        public double nextDouble() {
          return rand.nextDouble();
        }

        @Override
        public float nextFloat() {
          return rand.nextFloat();
        }

        @Override
        public double nextGaussian() {
          return rand.nextGaussian();
        }

        @Override
        public int nextInt() {
          return rand.nextInt();
        }

        @Override
        public int nextInt(int n) {
          return rand.nextInt(n);
        }

        @Override
        public long nextLong() {
          return rand.nextLong();
        }

        @Override
        public void setSeed(long seed) {
          rand.setSeed(seed);
        }
      };
    } else {
      randGen = new JDKRandomGenerator();
      randGen.setSeed(Settings.getSeed());
      rand = new RandomAdaptor(randGen);
    }

    // Create window
    if (!Settings.isTestMode()) {
      Controller.graphFrame = new GraphFrame();
    }

    // Time conversion from seconds to milliseconds
    int buyPrice = Settings.getBuyPrice();
    long startTime = Settings.getStartTime();
    long endTime = startTime + Settings.getTradeTime();

    MatchingEngine matchingEngine = new MatchingEngine(buyPrice, startTime);
    Controller controller =
      new Controller(startTime, endTime, matchingEngine,
        Settings.getThreshold(), Settings.getDelay(),
        Settings.getNumIntelligentAgents());
    long elapsedTime = System.nanoTime();
    controller.runSimulator();
    if (!Settings.isTestMode()) {
      Controller.graphFrame.showFinished(System.nanoTime() - elapsedTime);
    }
  }
}
