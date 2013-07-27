package edu.virginia.jinsup;

// import java.security.SecureRandom;

// import org.apache.commons.math3.distribution.ExponentialDistribution;
// import org.apache.commons.math3.distribution.PoissonDistribution;
// import org.apache.commons.math3.util.FastMath;

import com.beust.jcommander.JCommander;

public class JinSup {

  public static Settings settings;

  public static void main(String[] args) {

    /**
     * TODO Remove this when satisfied that new arrival algorithm is correct.
     */
    // double mean = 40;
    // PoissonDistribution poisson = new PoissonDistribution(mean);
    // ExponentialDistribution expo = new ExponentialDistribution(1 / mean);
    // SecureRandom rand = new SecureRandom();
    //
    // double sumP = 0.0; // Apache Poisson
    // double sumE = 0.0; // Java exponential
    // double sumA = 0.0; // Apache exponential
    //
    // int trials = 100000;
    // for (int i = 0; i < trials; ++i) {
    // sumP += poisson.sample();
    // sumE -= FastMath.log(1.0 - rand.nextDouble());
    // sumA += 1.0 - mean * (expo.sample() - 1.0);
    // }
    // double p = Math.abs(sumP / trials - mean);
    // double e = Math.abs(sumE * mean / trials - mean);
    // double a = Math.abs(sumA / trials - mean);
    // System.out.println(p);
    // System.out.println(e);
    // System.out.println(a);
    // System.exit(0);

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
