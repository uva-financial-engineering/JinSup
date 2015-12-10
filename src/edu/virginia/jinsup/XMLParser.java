package edu.virginia.jinsup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

public class XMLParser {

  /**
   * Parse the XML configuration file for simulation parameters.
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
    Parameters.buyPrice =
      Integer.parseInt(root.getAttribute("buyPrice").getValue());
    Parameters.startTime =
      Long.parseLong(root.getAttribute("startTime").getValue());
    Parameters.tradeTime =
      Long.parseLong(root.getAttribute("tradeTime").getValue());
    Parameters.testing =
      root.getAttribute("testing").getValue().toLowerCase().equals("true");
    Parameters.showGui =
      root.getAttribute("showGui").getValue().toLowerCase().equals("true");

    // Agent Counts
    Element agentCounts = root.getFirstChildElement("AgentCounts");
    Parameters.fundCount =
      Integer.parseInt(agentCounts.getFirstChildElement("FundamentalCount")
        .getValue());
    Parameters.marketMakerCount =
      Integer.parseInt(agentCounts.getFirstChildElement("MarketMakerCount")
        .getValue());
    Parameters.opporStratCount =
      Integer.parseInt(agentCounts.getFirstChildElement("OpporStratCount")
        .getValue());
    Parameters.hftCount =
      Integer.parseInt(agentCounts.getFirstChildElement("HFTCount").getValue());
    Parameters.smallTraderCount =
      Integer.parseInt(agentCounts.getFirstChildElement("SmallTraderCount")
        .getValue());
    Parameters.intelligentAgentCount =
      Integer.parseInt(agentCounts
        .getFirstChildElement("IntelligentAgentCount").getValue());

    // Poisson arrival rates
    Element poissonArrivalRates =
      root.getFirstChildElement("PoissonArrivalRates");
    Parameters.fundamentalArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "FundamentalArrivalRate").getValue());
    Parameters.marketMakerArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "MarketMakerArrivalRate").getValue());
    Parameters.opporStratArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "OpporStratArrivalRate").getValue());
    Parameters.hftArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "HFTArrivalRate").getValue());
    Parameters.smallTraderArrivalRate =
      Double.parseDouble(poissonArrivalRates.getFirstChildElement(
        "SmallTraderArrivalRate").getValue());

    // Poisson cancel rates
    Element poissonCancelRates =
      root.getFirstChildElement("PoissonCancelRates");
    Parameters.fundamentalCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "FundamentalCancelRate").getValue());
    Parameters.marketMakerCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "MarketMakerCancelRate").getValue());
    Parameters.opporStratCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "OpporStratCancelRate").getValue());
    Parameters.hftCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "HFTCancelRate").getValue());
    Parameters.smallTraderCancelRate =
      Double.parseDouble(poissonCancelRates.getFirstChildElement(
        "SmallTraderCancelRate").getValue());

    // Poisson order size probabilities
    Element orderSizeProbabilities =
      root.getFirstChildElement("PoissonOrderSizeProbabilities");
    Parameters.fundamentalOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("FundamentalSizes"));
    Parameters.marketMakerOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("MarketMakerSizes"));
    Parameters.opporStratOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("OpporStratSizes"));
    Parameters.hftOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("HFTSizes"));
    Parameters.smallTraderOrderSizeProbabilities =
      getMultipleDoubleElements(orderSizeProbabilities
        .getFirstChildElement("SmallTraderSizes"));

    // Poisson tick probabilities
    Element tickProbabilities =
      root.getFirstChildElement("PoissonTickProbabilities");
    Parameters.fundamentalTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("FundamentalTicks"));
    Parameters.marketMakerTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("MarketMakerTicks"));
    Parameters.opporStratTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("OpporStratTicks"));
    Parameters.hftTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("HFTTicks"));
    Parameters.smallTraderTickProbabilities =
      getMultipleDoubleElements(tickProbabilities
        .getFirstChildElement("SmallTraderTicks"));

    // Inventory Limits
    Element inventoryLimits = root.getFirstChildElement("InventoryLimits");
    Parameters.marketMakerInventoryLimit =
      Integer.parseInt(inventoryLimits.getFirstChildElement(
        "MarketMakerInventoryLimit").getValue());
    Parameters.opporStratInventoryLimit =
      Integer.parseInt(inventoryLimits.getFirstChildElement(
        "OpporStratInventoryLimit").getValue());
    Parameters.hftInventoryLimit =
      Integer.parseInt(inventoryLimits
        .getFirstChildElement("HFTInventoryLimit").getValue());
    Parameters.intelligentAgentInventoryLimit =
      Integer.parseInt(inventoryLimits.getFirstChildElement(
        "IntelligentAgentInventoryLimit").getValue());

    // Intelligent Agent Parameters
    Element intelligentAgentParams =
      root.getFirstChildElement("IntelligentAgentParams");
    Parameters.intelligentAgentLogFreq =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement(
        "ProfitLogFrequency").getValue());
    Parameters.intelligentAgentThreshold =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement("Threshold")
        .getValue());
    Parameters.intelligentAgentDelays =
      getMultipleIntegerElements(intelligentAgentParams
        .getFirstChildElement("Delays"));
    Parameters.halfTickWidth =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement(
        "HalfTickWidth").getValue());
    Parameters.orderSize =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement("OrderSize")
        .getValue());
    Parameters.actInterval =
      Integer.parseInt(intelligentAgentParams.getFirstChildElement(
        "ActInterval").getValue());
    Parameters.intelligentAgentThresholdEnable =
      intelligentAgentParams.getFirstChildElement("ThresholdEnable").getValue()
        .toLowerCase().equals("true");

    // OpporStrat Parameters
    Element opporStratParams = root.getFirstChildElement("OpporStratParams");
    Parameters.opporStratNewsFreq =
      Integer.parseInt(opporStratParams.getFirstChildElement(
        "OpporStratNewsFreq").getValue());
    Parameters.initialBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "InitialBuyProbability").getValue());
    Parameters.minBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "MinBuyProbability").getValue());
    Parameters.maxBuyProbability =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "MaxBuyProbability").getValue());
    Parameters.lowerUniformBound =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "LowerUniformBound").getValue());
    Parameters.upperUniformBound =
      Double.parseDouble(opporStratParams.getFirstChildElement(
        "UpperUniformBound").getValue());

  }

  /**
   * Helper method used to parse a list of Double values.
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
    if (!Parameters.checkProbabilitiesList(values)) {
      System.err
        .println("Error: Probabilities do not add up close enough to 1.0.");
    }
    return values;
  }

  /**
   * Helper method used to parse a list of Integer values.
   * 
   * @param element
   *          The element to extract the list from.
   * @return ArrayList of values from the given element.
   */
  private static ArrayList<Integer> getMultipleIntegerElements(Element element) {
    ArrayList<Integer> values = new ArrayList<Integer>();
    String[] strArray = element.getValue().split(" ");
    for (String str : strArray) {
      values.add(Integer.parseInt(str));
    }

    return values;
  }

}
