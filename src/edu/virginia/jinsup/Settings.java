package edu.virginia.jinsup;

import java.util.Calendar;

import com.beust.jcommander.Parameter;

public class Settings {

  private static Calendar calendar = Calendar.getInstance();
  private static String timestamp = String.format("%2d%02d%02d-%02d%02d%02d",
    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

  // Random

  @Parameter(names = {"--randomGenerator", "-rng"},
    description = "Random number generation algorithm")
  private static String rng = "mersenne";

  @Parameter(names = {"--seed"}, description = "PRNG seed")
  private static long seed = System.currentTimeMillis();

  // Logs

  @Parameter(names = {"--destTradeFile", "-dt"},
    description = "File to write trade log data")
  private static String destTradeFile = "log-" + timestamp + ".csv";

  @Parameter(names = {"--destIAProfitFile", "-dia"},
    description = "File to write IA profits")
  private static String destIAProfitFile = "IAProfits-" + timestamp + ".csv";

  // Configuration File
  @Parameter(names = {"--config"}, description = "Configuration file",
    required = true)
  private static String configPath;

  // Help

  @Parameter(names = {"--help"}, description = "Show this usage information",
    help = true)
  private static boolean help;

  public static String getRNG() {
    return rng;
  }

  public static long getSeed() {
    return seed;
  }

  public static String getDestTradeFile() {
    return destTradeFile;
  }

  public static String getDestIAProfitFile() {
    return destIAProfitFile;
  }

  public static String getConfigPath() {
    return configPath;
  }

  public static boolean showHelp() {
    return help;
  }

}
