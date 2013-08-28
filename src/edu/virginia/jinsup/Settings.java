package edu.virginia.jinsup;

import java.util.Calendar;

import com.beust.jcommander.Parameter;

public class Settings {

  private static Calendar calendar = Calendar.getInstance();
  private static String timestamp = String.format("%2d%02d%02d-%02d%02d%02d",
    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

  // UI

  /**
   * If true, no trade logging information will be saved and graphs will not be
   * updated. Other logging may still be done, however (e.g. IA profit).
   */
  @Parameter(names = {"--test"}, description = "Enable test mode")
  private static boolean testMode;

  // Time

  @Parameter(names = {"--buy", "-b"},
    description = "Buy price in dollars (must be in increments of $0.25)",
    required = true)
  private static Double buyPrice;

  @Parameter(names = {"--start", "-s"},
    description = "Length of starting period in seconds", required = true)
  private static int startTime;

  @Parameter(names = {"--trade", "-t"},
    description = "Length of trading period in seconds", required = true)
  private static int tradeTime;

  // Random

  @Parameter(names = {"--randomGenerator", "-rng"},
    description = "Random number generation algorithm")
  private static String rng = "jdk";

  @Parameter(names = {"--seed"}, description = "PRNG seed")
  private static long seed = System.currentTimeMillis();

  // Logs

  @Parameter(names = {"--destTradeFile", "-dt"},
    description = "File to write trade log data")
  private static String destTradeFile = "log-" + timestamp + ".csv";

  @Parameter(names = {"--destIAProfitFile", "-dia"},
    description = "File to write IA profits")
  private static String destIAProfitFile = "IAProfits-" + timestamp + ".csv";

  // Agents

  @Parameter(names = {"--numIntelligentAgents", "-nia"},
    description = "Number of intelligent agents", required = true)
  private static int numIntelligentAgents;

  @Parameter(names = {"--threshold", "-th"}, description = "Threshold for IAs",
    required = true)
  private static int threshold = 0;

  @Parameter(names = {"--delay", "-d"}, description = "Delay for IAs, in ms",
    required = true)
  private static int delay = 0;

  // Getters

  public static Double getBuyPrice() {
    return buyPrice;
  }

  public static int getStartTime() {
    return startTime;
  }

  public static int getTradeTime() {
    return tradeTime;
  }

  public static String getDestTradeFile() {
    return destTradeFile;
  }

  public static String getDestIAProfitFile() {
    return destIAProfitFile;
  }

  public static int getNumIntelligentAgents() {
    return numIntelligentAgents;
  }

  public static int getThreshold() {
    return threshold;
  }

  public static int getDelay() {
    return delay;
  }

  public static long getSeed() {
    return seed;
  }

  public static boolean isTestMode() {
    return testMode;
  }
}
