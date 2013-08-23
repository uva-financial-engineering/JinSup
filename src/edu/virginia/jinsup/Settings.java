package edu.virginia.jinsup;

import com.beust.jcommander.Parameter;

public class Settings {

  @Parameter(names = "-configLocation",
    description = "Location of the configuration file", required = false)
  private String configLocation;
  @Parameter(names = "-dest", description = "File to write log data",
    required = false)
  private String dest;

  public String getConfigLocation() {
    return configLocation;
  }

  public String getDest() {
    return dest;
  }

  public boolean isSet() {
    return (configLocation != null && dest != null);
  }

}