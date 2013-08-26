package edu.virginia.jinsup;

import java.util.Calendar;
import com.beust.jcommander.Parameter;

public class Settings {

  public static Calendar calendar = Calendar.getInstance();

  @Parameter(names = {"--buy", "-b"},
    description = "Buy price in dollars (must be in increments of $0.25)",
    required = true)
  private static Double buyPrice;

  @Parameter(names = {"--start", "-s"},
    description = "Length of starting period in seconds", required = true)
  private static Integer startTime;

  @Parameter(names = {"--trade", "-t"},
    description = "Length of trading period in seconds", required = true)
  private static Integer tradeTime;

  @Parameter(names = {"--destTradeFile", "-dt"},
    description = "File to write trade log data", required = false)
  private static String destTradeFile = "log-"
    + String.format("%2d%02d%02d-%02d%02d%02d", calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
      calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
      calendar.get(Calendar.SECOND)) + ".csv";

  @Parameter(names = {"--destIAProfitFile", "-dia"},
    description = "File to write IA profits", required = false)
  private static String destIAProfitFile = "IAProfits-"
    + String.format("%2d%02d%02d-%02d%02d%02d", calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
      calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
      calendar.get(Calendar.SECOND)) + ".csv";

  @Parameter(names = {"--threshold", "-th"}, description = "Threshold for IAs",
    required = true)
  private static Integer threshold = 0;

  @Parameter(names = {"--delay", "-d"}, description = "Delay for IAs, in ms",
    required = true)
  private static Integer delay = 0;

  /**
   * If true, no trade logging information will be saved and graphs will not be
   * updated. Other logging may still be done, however (e.g. IA profit).
   */
  @Parameter(names = {"--test"}, description = "Enable test mode",
    required = false)
  private static boolean testMode;

  public static Double getBuyPrice() {
    return buyPrice;
  }

  public static Integer getStartTime() {
    return startTime;
  }

  public static Integer getTradeTime() {
    return tradeTime;
  }

  public static String getDestTradeFile() {
    return destTradeFile;
  }

  public static String getDestIAProfitFile() {
    return destIAProfitFile;
  }

  public static Integer getThreshold() {
    return threshold;
  }

  public static Integer getDelay() {
    return delay;
  }

  public static boolean isTestMode() {
    return testMode;
  }
}
