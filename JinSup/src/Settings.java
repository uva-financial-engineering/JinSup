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

  public boolean isSet() {
    return (buyPrice != null && startTime != null && tradeTime != null && dest != null);
  }

}