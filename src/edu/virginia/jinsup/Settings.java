package edu.virginia.jinsup;

import com.beust.jcommander.Parameter;

public class Settings {

  @Parameter(names = "-buy",
    description = "Buy price in dollars (must be in increments of $0.25)",
    required = false)
  private Double buyPrice;

  @Parameter(names = "-start",
    description = "Length of starting period in seconds", required = false)
  private Integer startTime;

  @Parameter(names = "-trade",
    description = "Length of trading period in seconds", required = false)
  private Integer tradeTime;

  @Parameter(names = "-dest", description = "File to write log data",
    required = false)
  private String dest;

  @Parameter(names = "-threshold", description = "Threshold for IAs",
    required = false)
  private Integer threshold;

  @Parameter(names = "-delay", description = "Delay for IAs, in ms",
    required = false)
  private Integer delay;

  public Double getBuyPrice() {
    return buyPrice;
  }

  public Integer getStartTime() {
    return startTime;
  }

  public Integer getTradeTime() {
    return tradeTime;
  }

  public String getDest() {
    return dest;
  }

  public Integer getThreshold() {
    return threshold;
  }

  public Integer getDelay() {
    return delay;
  }

  public boolean isSet() {
    return (buyPrice != null && startTime != null && tradeTime != null
      && dest != null && threshold != null && delay != null);
  }

}