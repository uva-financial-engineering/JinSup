package edu.virginia.jinsup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * Parses all parameters required for the simulation and provides the parameters
 * to the rest of the simulation.
 */
public class Parameters {

  // Overall simulation parameters
  public static double buyPrice;
  public static int startTime;
  public static int tradeTime;
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

  // OS parameters
  public static int opporStratNewsFreq;
  public static double initialBuyProbability;
  public static double minBuyProbability;
  public static double maxBuyProbability;
  public static double lowerUniformBound;
  public static double upperUniformBound;

  // IA parameters
  public static int intelligentAgentThreshold;
  public static int intelligentAgentDelay;
  public static int intelligentAgentLogFreq;
  public static boolean intelligentAgentThresholdEnable;

  // Batch parameters
  public static boolean batchModeEnable;
  public static int numberOfRuns;

  /**
   * Parse the configuration file for simulation parameters.
   * 
   * @param configFileLocation
   *          The location of the configuration file.
   */
  public static void loadParameters(String configFileLocation) {
    Document doc = null;
    Builder parser = new Builder();
    try {
      doc = parser.build(new File(configFileLocation));
    } catch (ParsingException | IOException e) {
      // TODO Auto-generated catch block
      System.err.println("Error: Could not parse configuration file.");
    }

    Element root = doc.getRootElement();

    // Overall simulation parameters
    buyPrice = Double.parseDouble(root.getAttribute("buyPrice").getValue());
    startTime = Integer.parseInt(root.getAttribute("startTime").getValue());
    tradeTime = Integer.parseInt(root.getAttribute("tradeTime").getValue());
    testing =
      root.getAttribute("testing").getValue().toLowerCase().equals("true");

    // Agent Counts
    Element agentCounts = root.getFirstChildElement("AgentCounts");
    fundCount =
      Integer.parseInt(agentCounts.getFirstChildElement("FundamentalCount")
        .getValue());
    marketMakerCount =
      Integer.parseInt(agentCounts.getFirstChildElement("MarketMakerCount")
        .getValue());
    opporStratCount =
      Integer.parseInt(agentCounts.getFirstChildElement("OpporStratCount")
        .getValue());
    hftCount =
      Integer.parseInt(agentCounts.getFirstChildElement("HFTCount").getValue());
    smallTraderCount =
      Integer.parseInt(agentCounts.getFirstChildElement("SmallTraderCount")
        .getValue());
    intelligentAgentCount =
      Integer.parseInt(agentCounts
        .getFirstChildElement("IntelligentAgentCount").getValue());

    // Poisson arrival rates
    Element poissonArrivalRates =
      root.getFirstChildElement("PoissonArrivalRates");
    fundamentalArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "FundamentalArrivalRate").getValue());
    marketMakerArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "MarketMakerArrivalRate").getValue());
    opporStratArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "OpporStratArrivalRate").getValue());
    hftArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "HFTArrivalRate").getValue());
    smallTraderArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "SmallTraderArrivalRate").getValue());

    // Poisson cancel rates
    Element poissonCancelRates =
      root.getFirstChildElement("PoissonCancelRates");
    fundamentalCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "FundamentalCancelRate").getValue());
    marketMakerCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "MarketMakerCancelRate").getValue());
    opporStratCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "OpporStratCancelRate").getValue());
    hftCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "HFTCancelRate").getValue());
    smallTraderCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "SmallTraderCancelRate").getValue());

    // Poisson order size probabilities
    Element orderSizeProbabilities =
      root.getFirstChildElement("PoissonOrderSizeProbabilities");
    fundamentalOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("FundamentalSizes"));
    marketMakerOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("MarketMakerSizes"));
    opporStratOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("OpporStratSizes"));
    hftOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("HFTSizes"));
    smallTraderOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("SmallTraderSizes"));

    // Poisson tick probabilities
    Element tickProbabilities =
      root.getFirstChildElement("PoissonTickProbabilities");
    fundamentalTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("FundamentalTicks"));
    marketMakerTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("MarketMakerTicks"));
    opporStratTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("OpporStratTicks"));
    hftTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("HFTTicks"));
    smallTraderTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("SmallTraderTicks"));

    // Inventory Limits
    Element inventoryLimits = root.getFirstChildElement("InventoryLimits");
    marketMakerInventoryLimit =
      Integer.parseInt(inventoryLimits.getFirstChildElement(
        "MarketMakerInventoryLimit").getValue());
    opporStratInventoryLimit =
      Integer.parseInt(inventoryLimits.getFirstChildElement(
        "OpporStratInventoryLimit").getValue());
    hftInventoryLimit =
      Integer.parseInt(inventoryLimits
        .getFirstChildElement("HFTInventoryLimit").getValue());

    // OpporStrat Parameters
    Element opporStratParams = root.getFirstChildElement("OpporStratParams");
    opporStratNewsFreq =
      Integer.parseInt(opporStratParams.getFirstChildElement(
        "OpporStratNewsFreq").getValue());
    initialBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "InitialBuyProbability").getValue());
    minBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "MinBuyProbability").getValue());
    maxBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "MaxBuyProbability").getValue());
    lowerUniformBound =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "LowerUniformBound").getValue());
    upperUniformBound =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "UpperUniformBound").getValue());

    // Intelligent Agent Parameters
    Element intelligentAgentParams =
      root.getFirstChildElement("IntelligentAgentParams");
    intelligentAgentLogFreq =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement(
        "ProfitLogFrequency").getValue());
    intelligentAgentThreshold =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement("Threshold")
        .getValue());
    intelligentAgentDelay =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement("Delay")
        .getValue());
    intelligentAgentThresholdEnable =
      intelligentAgentParams.getFirstChildElement("ThresholdEnable").getValue()
        .toLowerCase().equals("true");

    // Batch parameters
    Element batchParams = root.getFirstChildElement("BatchParams");
    batchModeEnable =
      batchParams.getFirstChildElement("BatchModeEnable").getValue()
        .toLowerCase().equals("true");
    numberOfRuns =
      Integer.parseInt(batchParams.getFirstChildElement("NumberOfRuns")
        .getValue());
  }

  /**
   * Helper method used to parse a list of values.
   * 
   * @param element
   *          The element to extract the list from.
   * @return ArrayList of values from the given element.
   */
  private static ArrayList<Double> getMultipleDoubleElements(Element element) {
    ArrayList<Double> values = new ArrayList<Double>();
    String[] strArray = element.getValue().split(" ");
    for (String str : strArray) {
      values.add(Double.parseDouble(str));
    }
    if (!checkProbabilitiesList(values)) {
      System.err
        .println("Error: Probabilities do not add up close enough to 1.0.");
    }
    return values;
  }

  private static boolean checkProbabilitiesList(ArrayList<Double> listToCheck) {
    double sum = 0.0;
    for (double i : listToCheck) {
      sum += i;
    }
    if (sum < 0.9999999998 || sum > 1.0000000002) {
      return false;
    }
    return true;
  }
}
