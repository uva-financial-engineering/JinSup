package edu.virginia.jinsup;

import java.util.ArrayList;

/**
 * Holds all parameters required for the simulation and provides the parameters
 * to the rest of the simulation.
 */
public class Parameters {

  // Overall simulation parameters
  // Price must be in CENTS and a multiple of 25.
  public static int buyPrice;
  public static long startTime;
  public static long endTime;
  public static long tradeTime;
  public static boolean testing;

  // Agent counts
  public static int fundCount;
  public static int marketMakerCount;
  public static int opporStratCount;
  public static int hftCount;
  public static int smallTraderCount;
  public static int intelligentAgentCount;

  // Poisson arrival rates
  public static double fundamentalArrivalRate;
  public static double marketMakerArrivalRate;
  public static double opporStratArrivalRate;
  public static double hftArrivalRate;
  public static double smallTraderArrivalRate;

  // Poisson cancel rates
  public static double fundamentalCancelRate;
  public static double marketMakerCancelRate;
  public static double opporStratCancelRate;
  public static double hftCancelRate;
  public static double smallTraderCancelRate;

  // Poisson Order size probabilities
  public static ArrayList<Double> fundamentalOrderSizeProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> marketMakerOrderSizeProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> opporStratOrderSizeProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> hftOrderSizeProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> smallTraderOrderSizeProbabilities =
    new ArrayList<Double>();

  // Poisson tick level probabilities
  public static ArrayList<Double> fundamentalTickProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> marketMakerTickProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> opporStratTickProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> hftTickProbabilities =
    new ArrayList<Double>();
  public static ArrayList<Double> smallTraderTickProbabilities =
    new ArrayList<Double>();

  // Inventory Limits
  public static int marketMakerInventoryLimit;
  public static int opporStratInventoryLimit;
  public static int hftInventoryLimit;
  public static int intelligentAgentInventoryLimit;

  // OS parameters
  public static int opporStratNewsFreq;
  public static double initialBuyProbability;
  public static double minBuyProbability;
  public static double maxBuyProbability;
  public static double lowerUniformBound;
  public static double upperUniformBound;

  // IA parameters
  public static int intelligentAgentThreshold;
  public static ArrayList<Integer> intelligentAgentDelays;
  public static int intelligentAgentLogFreq;
  public static boolean intelligentAgentThresholdEnable;
  public static int halfTickWidth;
  public static int orderSize;
  public static int actInterval;

  public static boolean checkProbabilitiesList(ArrayList<Double> listToCheck) {
    double sum = 0.0;
    for (double i : listToCheck) {
      sum += i;
    }
    return (sum > 0.9999999998 && sum < 1.0000000002);
  }

}
