package edu.virginia.jinsup;

import com.beust.jcommander.Parameter;

public class Settings {

  @Parameter(names = {"--buy"},
    description = "Buy price in dollars (must be in increments of $0.25)")
  public static Double buyPrice;

  @Parameter(names = {"--start"},
    description = "Length of starting period in seconds")
  public static Integer startTime;

  @Parameter(names = {"--trade"},
    description = "Length of trading period in seconds")
  public static Integer tradeTime;

  @Parameter(names = {"--dest"}, description = "File to write log data")
  public static String dest;

  @Parameter(names = {"--threshold"}, description = "Threshold for IAs")
  public static Integer threshold = 0;

  @Parameter(names = {"--delay"}, description = "Delay for IAs, in ms")
  public static Integer delay = 0;

  public static boolean isSet() {
    return (buyPrice != null && startTime != null && tradeTime != null
      && dest != null && threshold != null && delay != null);
  }

}
