package edu.virginia.jinsup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONParser {

  /**
   * Parse the JSON configuration file for simulation parameters.
   * 
   * @param configFileLocation
   *          The location of the configuration file.
   */
  public static void loadParameters(String configFileLocation) {
    ObjectMapper m = new ObjectMapper();
    JsonNode rootNode = null;
    try {
      rootNode = m.readTree(new File(configFileLocation));
    } catch (IOException e) {
      System.err.println("Error: Could not parse configuration file.");
    }

    JsonNode rootConfigs = rootNode.path("JinSupRunConfigs");

    // Overall simulation parameters
    Parameters.buyPrice = rootConfigs.path("buyPrice").asInt();
    Parameters.startTime = rootConfigs.path("startTime").asLong();
    Parameters.tradeTime = rootConfigs.path("tradeTime").asLong();
    Parameters.testing = rootConfigs.path("testing").asBoolean();
    Parameters.showGui = rootConfigs.path("showGui").asBoolean();

    // Agent Counts
    JsonNode agentCounts = rootConfigs.path("AgentCounts");
    Parameters.fundCount = agentCounts.path("FundamentalCount").asInt();
    Parameters.marketMakerCount = agentCounts.path("MarketMakerCount").asInt();
    Parameters.opporStratCount = agentCounts.path("OpporStratCount").asInt();
    Parameters.hftCount = agentCounts.path("HFTCount").asInt();
    Parameters.smallTraderCount = agentCounts.path("SmallTraderCount").asInt();
    Parameters.intelligentAgentCount =
      agentCounts.path("IntelligentAgentCount").asInt();

    // Poisson arrival rates
    JsonNode poissonArrivalRates = rootConfigs.path("PoissonArrivalRates");
    Parameters.fundamentalArrivalRate =
      poissonArrivalRates.path("FundamentalArrivalRate").asDouble();
    Parameters.marketMakerArrivalRate =
      poissonArrivalRates.path("MarketMakerArrivalRate").asDouble();
    Parameters.opporStratArrivalRate =
      poissonArrivalRates.path("OpporStratArrivalRate").asDouble();
    Parameters.hftArrivalRate =
      poissonArrivalRates.path("HFTArrivalRate").asDouble();
    Parameters.smallTraderArrivalRate =
      poissonArrivalRates.path("SmallTraderArrivalRate").asDouble();

    // Poisson cancel rates
    JsonNode poissonCancelRates = rootConfigs.path("PoissonCancelRates");
    Parameters.fundamentalCancelRate =
      poissonCancelRates.path("FundamentalCancelRate").asDouble();
    Parameters.marketMakerCancelRate =
      poissonCancelRates.path("MarketMakerCancelRate").asDouble();
    Parameters.opporStratCancelRate =
      poissonCancelRates.path("OpporStratCancelRate").asDouble();
    Parameters.hftCancelRate =
      poissonCancelRates.path("HFTCancelRate").asDouble();
    Parameters.smallTraderCancelRate =
      poissonCancelRates.path("SmallTraderCancelRate").asDouble();

    // Poisson order size probabilities
    JsonNode orderSizeProbabilities =
      rootConfigs.path("PoissonOrderSizeProbabilities");
    Parameters.fundamentalOrderSizeProbabilities =
      getMulitpleDoubleElements(orderSizeProbabilities.path("FundamentalSizes"));
    Parameters.marketMakerOrderSizeProbabilities =
      getMulitpleDoubleElements(orderSizeProbabilities.path("MarketMakerSizes"));
    Parameters.opporStratOrderSizeProbabilities =
      getMulitpleDoubleElements(orderSizeProbabilities.path("OpporStratSizes"));
    Parameters.hftOrderSizeProbabilities =
      getMulitpleDoubleElements(orderSizeProbabilities.path("HFTSizes"));
    Parameters.smallTraderOrderSizeProbabilities =
      getMulitpleDoubleElements(orderSizeProbabilities.path("SmallTraderSizes"));

    // Poisson tick probabilities
    JsonNode tickProbabilities = rootConfigs.path("PoissonTickProbabilities");
    Parameters.fundamentalTickProbabilities =
      getMulitpleDoubleElements(tickProbabilities.path("FundamentalTicks"));
    Parameters.marketMakerTickProbabilities =
      getMulitpleDoubleElements(tickProbabilities.path("MarketMakerTicks"));
    Parameters.opporStratTickProbabilities =
      getMulitpleDoubleElements(tickProbabilities.path("OpporStratTicks"));
    Parameters.hftTickProbabilities =
      getMulitpleDoubleElements(tickProbabilities.path("HFTTicks"));
    Parameters.smallTraderTickProbabilities =
      getMulitpleDoubleElements(tickProbabilities.path("SmallTraderTicks"));

    // Inventory Limits
    JsonNode inventoryLimits = rootConfigs.path("InventoryLimits");
    Parameters.marketMakerInventoryLimit =
      inventoryLimits.path("MarketMakerInventoryLimit").asInt();
    Parameters.opporStratInventoryLimit =
      inventoryLimits.path("OpporStratInventoryLimit").asInt();
    Parameters.hftInventoryLimit =
      inventoryLimits.path("HFTInventoryLimit").asInt();
    Parameters.intelligentAgentInventoryLimit =
      inventoryLimits.path("IntelligentAgentInventoryLimit").asInt();

    // Intelligent Agent Parameters
    JsonNode intelligentAgentParams =
      rootConfigs.path("IntelligentAgentParams");
    Parameters.intelligentAgentLogFreq =
      intelligentAgentParams.path("ProfitLogFrequency").asInt();
    Parameters.intelligentAgentThresholdEnable =
      intelligentAgentParams.path("ThresholdEnable").asBoolean();
    Parameters.intelligentAgentThreshold =
      intelligentAgentParams.path("Threshold").asInt();
    Parameters.halfTickWidth =
      intelligentAgentParams.path("HalfTickWidth").asInt();
    Parameters.orderSize = intelligentAgentParams.path("OrderSize").asInt();
    Parameters.actInterval = intelligentAgentParams.path("ActInterval").asInt();
    Parameters.intelligentAgentDelays =
      getMulitpleIntegerElements(intelligentAgentParams.path("Delays"));

    // OpporStrat Parameters
    JsonNode opporStratParams = rootConfigs.path("OpporStratParams");
    Parameters.opporStratNewsFreq =
      opporStratParams.path("OpporStratNewsFreq").asInt();
    Parameters.initialBuyProbability =
      opporStratParams.path("InitialBuyProbability").asDouble();
    Parameters.minBuyProbability =
      opporStratParams.path("MinBuyProbability").asDouble();
    Parameters.maxBuyProbability =
      opporStratParams.path("MaxBuyProbability").asDouble();
    Parameters.lowerUniformBound =
      opporStratParams.path("LowerUniformBound").asDouble();
    Parameters.upperUniformBound =
      opporStratParams.path("UpperUniformBound").asDouble();
  }

  /**
   * Helper method used to parse a list of Double values.
   * 
   * @param element
   *          The element to extract the list from.
   * @return ArrayList of values from the given element.
   */
  private static ArrayList<Double> getMulitpleDoubleElements(JsonNode jsonNode) {
    ArrayList<Double> values = new ArrayList<Double>();
    for (JsonNode value : jsonNode) {
      values.add(value.asDouble());
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
  private static ArrayList<Integer>
    getMulitpleIntegerElements(JsonNode jsonNode) {
    ArrayList<Integer> values = new ArrayList<Integer>();
    for (JsonNode value : jsonNode) {
      values.add(value.asInt());
    }
    return values;
  }
}
