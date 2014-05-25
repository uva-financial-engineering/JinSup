package edu.virginia.jinsup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 * Keeps track of simulation time and selects eligible agents so that they can
 * act during their alloted time.
 * 
 */
public class Controller {

  /**
   * To speed up the simulation with infinite thresholds, set this to false.
   * Takes precedence over INTELLIGENT_AGENT_THRESHOLD.
   */
  private static final boolean INTELLIGENT_AGENT_THRESHOLD_ENABLE =
    Parameters.intelligentAgentThresholdEnable;

  /**
   * How often the average profit over all intelligent agents should be logged,
   * in milliseconds.
   */
  private static final int INTELLIGENT_AGENT_PROFIT_LOG_FREQUENCY =
    Parameters.intelligentAgentLogFreq;

  /**
   * Location of the log file for intelligent agent average profits. It will be
   * in the same directory as the order log files and named
   * "IAProfits-{time}.csv".
   */
  private final String INTELLIGENT_AGENT_PROFIT_LOG_LOCATION;

  /**
   * List of all agents in the simulator
   */
  public ArrayList<Agent> agentList;

  /**
   * Simulator time in milliseconds.
   */
  public static long time;

  /**
   * Price graph
   */
  public static GraphFrame graphFrame;

  /**
   * The MatchingEngine used for this simulation.
   */
  private final MatchingEngine matchingEngine;

  // Intelligent Agent fields for multiple delays
  /**
   * Lists the agents by delay.
   */
  private ArrayList<ArrayList<IntelligentAgent>> intelligentAgentByDelay;

  /**
   * List of log files for each type of agent.
   */
  private ArrayList<String> intelligentAgentProfitFileNames;

  /**
   * List of all intelligent agents.
   */
  private ArrayList<IntelligentAgentHelper> intelligentAgentHelpers;

  /**
   * State of the simulation to be displayed on the title bar.
   */
  private String state = "Starting up period";

  /**
   * Poisson distribution used to space out calculation of buy probabilities for
   * poisson opportunistic traders.
   */
  private final PoissonDistribution poissonGeneratorNews;

  /**
   * The last time the news was updated. Time in milliseconds.
   */
  private long lastNewsTime;

  /**
   * Creates a controller with no agents.
   */
  public Controller(MatchingEngine matchingEngine) {
    agentList = new ArrayList<Agent>();
    time = 0;
    this.matchingEngine = matchingEngine;
    poissonGeneratorNews =
      new PoissonDistribution(JinSup.randGen, Parameters.opporStratNewsFreq,
        PoissonDistribution.DEFAULT_EPSILON,
        PoissonDistribution.DEFAULT_MAX_ITERATIONS);
    lastNewsTime = Parameters.opporStratNewsFreq * 1000;
    File logFile = new File(Settings.getDestIAProfitFile());
    INTELLIGENT_AGENT_PROFIT_LOG_LOCATION = logFile.getAbsolutePath();
    intelligentAgentByDelay = new ArrayList<ArrayList<IntelligentAgent>>();
    intelligentAgentProfitFileNames = new ArrayList<String>();
    intelligentAgentHelpers = new ArrayList<IntelligentAgentHelper>();
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time specified by the user.
   */
  public void runSimulator() {
    if (!Parameters.testing) {
      graphFrame.setTradePeriod(Parameters.startTime, Parameters.endTime);
      graphFrame.updateTitleBar(0, "Creating agents...");
    }

    // Create agents
    Order.setNextOrderID(0);

    FundBuyerPoisson fundBuyerPoisson;
    FundSellerPoisson fundSellerPoisson;
    for (int i = 0; i < Parameters.fundCount; ++i) {
      fundBuyerPoisson =
        new FundBuyerPoisson(matchingEngine,
          (int) Parameters.fundamentalArrivalRate,
          (int) Parameters.fundamentalCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      fundSellerPoisson =
        new FundSellerPoisson(matchingEngine,
          (int) Parameters.fundamentalArrivalRate,
          (int) Parameters.fundamentalCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      agentList.add(fundBuyerPoisson);
      agentList.add(fundSellerPoisson);
    }

    MarketMakerPoisson marketMakerPoisson;
    for (int i = 0; i < Parameters.marketMakerCount; ++i) {
      marketMakerPoisson =
        new MarketMakerPoisson(matchingEngine,
          (int) Parameters.marketMakerArrivalRate,
          (int) Parameters.marketMakerCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      agentList.add(marketMakerPoisson);
    }

    OpporStratPoisson opporStratPoisson;
    // Explicitly set global buy probability.
    OpporStratPoisson.setBuyProbability(Parameters.initialBuyProbability);
    for (int i = 0; i < Parameters.opporStratCount; ++i) {
      opporStratPoisson =
        new OpporStratPoisson(matchingEngine,
          (int) Parameters.opporStratArrivalRate,
          (int) Parameters.opporStratCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      agentList.add(opporStratPoisson);
    }

    HFTPoisson hftPoisson;
    for (int i = 0; i < Parameters.hftCount; ++i) {
      hftPoisson =
        new HFTPoisson(matchingEngine, Parameters.hftArrivalRate,
          Parameters.hftCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      agentList.add(hftPoisson);
    }

    SmallTrader smallTrader;
    for (int i = 0; i < Parameters.smallTraderCount; ++i) {
      smallTrader =
        new SmallTrader(matchingEngine,
          (int) Parameters.smallTraderArrivalRate,
          (int) Parameters.smallTraderCancelRate,
          (long) (JinSup.rand.nextDouble() * Parameters.startTime));
      agentList.add(smallTrader);
    }

    if (Parameters.intelligentAgentCount != 0) {
      // Set up appropriate number of log files, depending on number of delays
      IntelligentAgentHelper currentIAH;
      IntelligentAgent intelligentAgent;
      String fileName;
      for (Integer l : Parameters.intelligentAgentDelays) {
        try {
          fileName =
            INTELLIGENT_AGENT_PROFIT_LOG_LOCATION.split(".csv")[0] + "-Delay"
              + l + ".csv";
          FileWriter writer = new FileWriter(fileName);
          writer.append("Time, IA Average Profit\n");
          writer.flush();
          writer.close();
          intelligentAgentProfitFileNames.add(fileName);
        } catch (IOException e) {
          System.err.println("Error: Failed to create log file.");
          e.printStackTrace();
          System.exit(1);
        }
        currentIAH =
          new IntelligentAgentHelper((int) l,
            Parameters.intelligentAgentThreshold, Parameters.buyPrice,
            INTELLIGENT_AGENT_THRESHOLD_ENABLE);

        intelligentAgentHelpers.add(currentIAH);
        ArrayList<IntelligentAgent> intelligentAgentList =
          new ArrayList<IntelligentAgent>();
        for (int i = 0; i < Parameters.intelligentAgentCount
          / Parameters.intelligentAgentDelays.size(); i++) {
          intelligentAgent =
            new IntelligentAgent(matchingEngine, currentIAH, l);
          intelligentAgentList.add(intelligentAgent);
          agentList.add(intelligentAgent);
        }
        intelligentAgentByDelay.add(intelligentAgentList);
      }

      // run simulator until Settings.getEndTime() is reached.
      while (time < Parameters.startTime) {
        moveTime();
      }
      matchingEngine.setStartingPeriod(false);
      state = "Trading Period";
      while (time < Parameters.endTime) {
        moveTime();
      }

      // write remaining entries to the log
      if (!Parameters.testing) {
        matchingEngine.writeToLog();
        graphFrame.updateTitleBar(time, "Simulation Finished");
      }
    }
  }

  /**
   * Method that increments simulator time and performs other necessary actions
   * after each time step.
   */
  private void moveTime() {
    // Select all eligible agents in random order to act during a given time
    ArrayList<Agent> actingAgents = new ArrayList<Agent>();
    for (Agent a : agentList) {
      if (a.getNextActTime() == time) {
        actingAgents.add(a);
      }
    }
    Collections.shuffle(actingAgents, JinSup.rand);
    for (Agent a : actingAgents) {
      // Enable the agent to act until it no longer needs to act.
      a.setWillAct(true);
      do {
        a.act();
      } while (a.getWillAct());
    }

    if (Parameters.intelligentAgentCount != 0) {
      // Update the delay data for intelligent agents. A positive number means
      // that there are more buy orders than sell orders at the best bid/ask.
      for (int i = 0; i < Parameters.intelligentAgentDelays.size(); i++) {
        if (time >= (Parameters.startTime - Parameters.intelligentAgentDelays
          .get(i))) {
          intelligentAgentHelpers.get(i).addData(
            matchingEngine.getBestBidQuantity()
              - matchingEngine.getBestAskQuantity(),
            matchingEngine.getBestBid().getPrice(),
            matchingEngine.getBestAsk().getPrice());
          if (INTELLIGENT_AGENT_THRESHOLD_ENABLE) {
            for (IntelligentAgent a : intelligentAgentByDelay.get(i)) {
              a.setOldThresholdState(intelligentAgentHelpers.get(i)
                .getOldThresholdState());
            }
          }
        }
      }
    }

    // Check if profit logging should be done
    if (time % INTELLIGENT_AGENT_PROFIT_LOG_FREQUENCY == 0) {
      // Log the average profit over all intelligent agents, based on delay
      int totalProfit;
      int totalInventory;
      FileWriter writer;
      for (int i = 0; i < Parameters.intelligentAgentDelays.size(); i++) {
        totalProfit = 0;
        totalInventory = 0;
        for (IntelligentAgent a : intelligentAgentByDelay.get(i)) {
          totalProfit += a.getProfit();
          totalInventory += a.getInventory();
        }
        totalProfit =
          totalProfit + totalInventory * matchingEngine.getLastTradePrice();
        try {
          writer = new FileWriter(intelligentAgentProfitFileNames.get(i), true);
          writer.append(time + ","
            + (totalProfit / (intelligentAgentByDelay.get(i).size() * 100.0))
            + "\n");
          writer.flush();
          writer.close();
        } catch (IOException e) {
          System.err.println("Error: Failed to update log.");
          System.exit(1);
        }
      }
    }

    time++;
    matchingEngine.incrementTime();

    // Update a group of agents. Currently only updates the buy probabilities
    // across all opportunistic traders.
    if (time == lastNewsTime) {
      OpporStratPoisson.calcNewBuyProbability();
      lastNewsTime += poissonGeneratorNews.sample();
    }

    if (!Parameters.testing && time % 500 == 0) {
      graphFrame.updateTitleBar(time, state);
    }
  }
}
