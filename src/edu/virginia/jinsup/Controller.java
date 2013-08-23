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
   * Location of the log file for intelligent agent average profits. It will be
   * in the same directory as the order log files and named
   * "IAProfits-{time}.csv".
   */
  private final String INTELLIGENT_AGENT_PROFIT_LOG_LOCATION;

  /**
   * Holds a list of intelligent agents.
   */
  private final ArrayList<IntelligentAgent> intelligentAgentList;

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
   * The time when the simulation should end the startup period and allow agents
   * to trade in ms.
   */
  private final long startupTime;

  /**
   * The time to stop the simulator in ms.
   */
  private final long endTime;

  private int threshold;

  private int delay;

  private boolean setViaCommandLine;

  /**
   * The MatchingEngine used for this simulation.
   */
  private final MatchingEngine matchingEngine;

  private IntelligentAgentHelper intelligentAgentHelper;

  /**
   * State of the simulation to be displayed on the title bar.
   */
  private String state = "Starting up period";

  // TODO Remove this and related material when done with poisson trading.
  /**
   * Poisson distribution used to space out calculation of buy probabilities for
   * poisson opportunistic traders.
   */
  private final PoissonDistribution poissonGeneratorNews;

  /**
   * The last time the news was updated. Time in milliseconds.
   */
  private int lastNewsTime;

  /**
   * If true, does not write to trading log. IA logging still enabled.
   */
  private final boolean testing;

  /**
   * Creates a controller with no agents.
   */
  public Controller(long startupTime, long endTime,
    MatchingEngine matchingEngine, boolean testing) {
    agentList = new ArrayList<Agent>();
    time = 0;
    this.startupTime = startupTime;
    this.endTime = endTime;
    this.matchingEngine = matchingEngine;
    this.testing = testing;

    poissonGeneratorNews =
      new PoissonDistribution(Parameters.opporStratNewsFreq * 1000);
    lastNewsTime = Parameters.opporStratNewsFreq * 1000;
    intelligentAgentList = new ArrayList<IntelligentAgent>();
    setViaCommandLine = false;

    File logFile = new File(graphFrame.getDest());
    INTELLIGENT_AGENT_PROFIT_LOG_LOCATION =
      logFile.getAbsoluteFile().getParent() + File.separator + "IAProfits-"
        + graphFrame.getLogTime() + ".csv";

    try {
      FileWriter writer = new FileWriter(INTELLIGENT_AGENT_PROFIT_LOG_LOCATION);
      writer.append("Time, IA Average Profit\n");
      writer.flush();
      writer.close();
    } catch (IOException e) {
      System.err.println("Error: Failed to create log file.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates a controller with no agents but specifies IA parameters
   */
  public Controller(long startupTime, long endTime,
    MatchingEngine matchingEngine, boolean testing, int threshold, int delay) {
    this(startupTime, endTime, matchingEngine, testing);
    this.threshold = threshold;
    this.delay = delay;
    setViaCommandLine = true;

  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time specified by the user.
   */
  public void runSimulator() {
    graphFrame.setTradePeriod(startupTime, endTime);

    // Create agents

    Order.setNextOrderID(0);

    graphFrame.updateTitleBar(0, "Creating agents...");

    FundBuyerPoisson fundBuyerPoisson;
    FundSellerPoisson fundSellerPoisson;
    for (int i = 0; i < Parameters.fundCount; ++i) {
      fundBuyerPoisson =
        new FundBuyerPoisson(matchingEngine,
          (int) Parameters.fundamentalArrivalRate,
          (int) Parameters.fundamentalCancelRate,
          (long) (Math.random() * startupTime));
      fundSellerPoisson =
        new FundSellerPoisson(matchingEngine,
          (int) Parameters.fundamentalArrivalRate,
          (int) Parameters.fundamentalCancelRate,
          (long) (Math.random() * startupTime));
      agentList.add(fundBuyerPoisson);
      agentList.add(fundSellerPoisson);
    }

    MarketMakerPoisson marketMakerPoisson;
    for (int i = 0; i < Parameters.marketMakerCount; ++i) {
      marketMakerPoisson =
        new MarketMakerPoisson(matchingEngine,
          (int) Parameters.marketMakerArrivalRate,
          (int) Parameters.marketMakerCancelRate,
          (long) (Math.random() * startupTime));
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
          (long) (Math.random() * startupTime));
      agentList.add(opporStratPoisson);
    }

    HFTPoisson hftPoisson;
    for (int i = 0; i < Parameters.hftCount; ++i) {
      hftPoisson =
        new HFTPoisson(matchingEngine, Parameters.hftArrivalRate,
          Parameters.hftCancelRate, (long) (Math.random() * startupTime));
      agentList.add(hftPoisson);
    }

    SmallTrader smallTrader;
    for (int i = 0; i < Parameters.smallTraderCount; ++i) {
      smallTrader =
        new SmallTrader(matchingEngine,
          (int) Parameters.smallTraderArrivalRate,
          (int) Parameters.smallTraderCancelRate,
          (long) (Math.random() * startupTime));
      agentList.add(smallTrader);
    }

    if (Parameters.intelligentAgentCount != 0) {
      intelligentAgentHelper =
        new IntelligentAgentHelper(Parameters.intelligentAgentDelay,
          Parameters.intelligentAgentThreshold,
          matchingEngine.getLastTradePrice(),
          Parameters.intelligentAgentThresholdEnable);

      IntelligentAgent intelligentAgent;
      // Explicitly set delay, threshold, and helper.
      if (setViaCommandLine) {
        IntelligentAgent.setDelay(delay);
        IntelligentAgent.setThreshold(threshold);
      } else {
        IntelligentAgent.setDelay(Parameters.intelligentAgentDelay);
        IntelligentAgent.setThreshold(Parameters.intelligentAgentThreshold);
      }
      IntelligentAgent.setIntelligentAgentHelper(intelligentAgentHelper);
      IntelligentAgent.setTotalProfit(0);
      for (int i = 0; i < Parameters.intelligentAgentCount; ++i) {
        intelligentAgent = new IntelligentAgent(matchingEngine);
        agentList.add(intelligentAgent);
        intelligentAgentList.add(intelligentAgent);
      }
    }

    System.out.println("Done! Simulation has started");

    // run simulator until endTime is reached.
    while (time < startupTime) {
      moveTime();
    }
    matchingEngine.setStartingPeriod(false);
    System.out.println("Trading Enabled!");
    state = "Trading Period";
    while (time < endTime) {
      moveTime();
    }

    // write remaining entries to the log
    if (!testing) {
      matchingEngine.writeToLog();
    }

    System.out.println("The simulation has ended.");
    graphFrame.updateTitleBar(time, "Simulation Finished");
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
    Collections.shuffle(actingAgents);
    for (Agent a : actingAgents) {
      // Enable the agent to act until it no longer needs to act.
      a.setWillAct(true);
      do {
        a.act();
      } while (a.getWillAct());
    }

    // Moving average is not used for poisson trading.
    // matchingEngine.storeMovingAverage();
    matchingEngine.reset();
    if (Parameters.intelligentAgentCount != 0) {
      // Update the delay data for intelligent agents. A positive number means
      // that there are more buy orders than sell orders at the best bid/ask.
      if (time >= (startupTime - Parameters.intelligentAgentDelay)) {
        intelligentAgentHelper.addData(matchingEngine.getBestBidQuantity()
          - matchingEngine.getBestAskQuantity(), matchingEngine.getBestBid()
          .getPrice(), matchingEngine.getBestAsk().getPrice());
        if (Parameters.intelligentAgentThresholdEnable) {
          IntelligentAgent.setOldThresholdState(intelligentAgentHelper
            .getOldThresholdState());
        }
      }
    }

    // Check if profit logging should be done
    if (time % Parameters.intelligentAgentLogFreq == 0) {
      // Log the average profit over all intelligent agents.
      int totalProfit = 0;
      for (IntelligentAgent a : intelligentAgentList) {
        totalProfit += a.getInventory();
      }
      totalProfit =
        totalProfit * matchingEngine.getLastTradePrice()
          + IntelligentAgent.getTotalProfit();
      FileWriter writer;
      try {
        writer = new FileWriter(INTELLIGENT_AGENT_PROFIT_LOG_LOCATION, true);
        writer.append(time + ","
          + (totalProfit / (Parameters.intelligentAgentCount * 100.0)) + "\n");
        writer.flush();
        writer.close();
      } catch (IOException e) {
        System.err.println("Error: Failed to update log.");
        e.printStackTrace();
        System.exit(1);
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

    if (time % 500 == 0) {
      graphFrame.updateTitleBar(time, state);
    }
  }
}
