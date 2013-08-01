package edu.virginia.jinsup;

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
   * Number of fund buyers (equal to number of fund sellers)
   */
  private static final int FUND_BUYER_SELLER_COUNT = 84;

  /**
   * Number of market makers
   */
  private static final int MARKET_MAKER_COUNT = 19;

  /**
   * Number of oppor strat traders
   */
  private static final int OPPOR_STRAT_COUNT = 96;

  /**
   * Number of HFT traders
   */
  private static final int HFT_COUNT = 6;

  /**
   * Number of small traders
   */
  private static final int SMALL_TRADER_COUNT = 421;

  /**
   * Number of intelligent agents
   */
  private static final int INTELLIGENT_AGENT_COUNT = 10;

  // Specify parameters for intelligent agents here

  /**
   * How far in the past Intelligent Agents should look for data.
   */
  private static final int INTELLIGENT_AGENT_DELAY_LENGTH = 100;

  /**
   * To speed up the simulation with infinite thresholds, set this to false.
   * Takes precedence over INTELLIGENT_AGENT_THRESHOLD.
   */
  private static final boolean INTELLIGENT_AGENT_THRESHOLD_ENABLE = true;

  /**
   * Maximum difference between the total volume at the best bid/ask allowed
   * before additional actions are taken.
   */
  private static final int INTELLIGENT_AGENT_THRESHOLD = 200;

  // Specify the lambdas here in seconds
  private static final int FUND_BUYER_SELLER_LAMBDA_ORDER = 40;

  private static final int FUND_BUYER_SELLER_LAMBDA_CANCEL = 60;

  private static final int MARKET_MAKER_LAMBDA_ORDER = 3;

  private static final int MARKET_MAKER_LAMBDA_CANCEL = 2;

  private static final int OPPOR_STRAT_LAMBDA_ORDER = 30;

  private static final int OPPOR_STRAT_LAMBDA_CANCEL = 40;

  private static final double HFT_LAMBDA_ORDER = 0.6;

  private static final double HFT_LAMBDA_CANCEL = 0.4;

  private static final int SMALL_TRADER_LAMBDA_ORDER = 1500;

  private static final int SMALL_TRADER_LAMBDA_CANCEL = 1000;

  /**
   * How often buy probabilities for poisson opportunistic traders should
   * change, in seconds.
   */
  private static final int NEWS_FREQUENCY = 300;

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
   * Creates a controller with no agents.
   */
  public Controller(long startupTime, long endTime,
    MatchingEngine matchingEngine) {
    agentList = new ArrayList<Agent>();
    time = 0;
    this.startupTime = startupTime;
    this.endTime = endTime;
    this.matchingEngine = matchingEngine;
    poissonGeneratorNews = new PoissonDistribution(NEWS_FREQUENCY * 1000);
    lastNewsTime = NEWS_FREQUENCY * 1000;
  }

  /**
   * Update the delay data for intelligent agents. A positive number means that
   * there are more buy orders than sell orders at the best bid/ask.
   */
  public void triggerHelperEvent() {
    if (time >= (startupTime - INTELLIGENT_AGENT_DELAY_LENGTH)) {
      intelligentAgentHelper.addData(matchingEngine.getBestBidQuantity()
        - matchingEngine.getBestAskQuantity(), matchingEngine.getBestBid()
        .getPrice(), matchingEngine.getBestAsk().getPrice());
      if (INTELLIGENT_AGENT_THRESHOLD_ENABLE) {
        IntelligentAgent.setOldThresholdState(intelligentAgentHelper
          .getOldThresholdState());
      }
    }
  }

  /**
   * Updates a group of agents. Currently only updates the buy probabilities
   * across all opportunistic traders.
   */
  public void triggerGroupEvent() {
    if (time == lastNewsTime) {
      OpporStratPoisson.calcNewBuyProbability();
      lastNewsTime += poissonGeneratorNews.sample();
    }
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time specified by the user.
   */
  public void runSimulator() {
    graphFrame.setTradePeriod(startupTime, endTime);

    // Create agents

    graphFrame.updateTitleBar(0, "Creating agents...");

    FundBuyerPoisson fundBuyerPoisson;
    FundSellerPoisson fundSellerPoisson;
    for (int i = 0; i < FUND_BUYER_SELLER_COUNT; ++i) {
      fundBuyerPoisson =
        new FundBuyerPoisson(matchingEngine, FUND_BUYER_SELLER_LAMBDA_ORDER,
          FUND_BUYER_SELLER_LAMBDA_CANCEL, (long) (Math.random() * startupTime));
      fundSellerPoisson =
        new FundSellerPoisson(matchingEngine, FUND_BUYER_SELLER_LAMBDA_ORDER,
          FUND_BUYER_SELLER_LAMBDA_CANCEL, (long) (Math.random() * startupTime));
      agentList.add(fundBuyerPoisson);
      agentList.add(fundSellerPoisson);
    }

    MarketMakerPoisson marketMakerPoisson;
    for (int i = 0; i < MARKET_MAKER_COUNT; ++i) {
      marketMakerPoisson =
        new MarketMakerPoisson(matchingEngine, MARKET_MAKER_LAMBDA_ORDER,
          MARKET_MAKER_LAMBDA_CANCEL, (long) (Math.random() * startupTime));
      agentList.add(marketMakerPoisson);
    }

    OpporStratPoisson opporStratPoisson;
    // Explicitly set global buy probability.
    OpporStratPoisson.setBuyProbability(0.50);
    for (int i = 0; i < OPPOR_STRAT_COUNT; ++i) {
      opporStratPoisson =
        new OpporStratPoisson(matchingEngine, OPPOR_STRAT_LAMBDA_ORDER,
          OPPOR_STRAT_LAMBDA_CANCEL, (long) (Math.random() * startupTime));
      agentList.add(opporStratPoisson);
    }

    HFTPoisson hftPoisson;
    for (int i = 0; i < HFT_COUNT; ++i) {
      hftPoisson =
        new HFTPoisson(matchingEngine, HFT_LAMBDA_ORDER, HFT_LAMBDA_CANCEL,
          (long) (Math.random() * startupTime));
      agentList.add(hftPoisson);
    }

    SmallTrader smallTrader;
    for (int i = 0; i < SMALL_TRADER_COUNT; ++i) {
      smallTrader =
        new SmallTrader(matchingEngine, SMALL_TRADER_LAMBDA_ORDER,
          SMALL_TRADER_LAMBDA_CANCEL, (long) (Math.random() * startupTime));
      agentList.add(smallTrader);
    }

    if (INTELLIGENT_AGENT_COUNT != 0) {
      intelligentAgentHelper =
        new IntelligentAgentHelper(INTELLIGENT_AGENT_DELAY_LENGTH,
          INTELLIGENT_AGENT_THRESHOLD, matchingEngine.getLastTradePrice(),
          INTELLIGENT_AGENT_THRESHOLD_ENABLE);

      IntelligentAgent intelligentAgent;
      // Explicitly set delay, threshold, and helper.
      IntelligentAgent.setDelay(INTELLIGENT_AGENT_DELAY_LENGTH);
      IntelligentAgent.setThreshold(INTELLIGENT_AGENT_THRESHOLD);
      IntelligentAgent.setIntelligentAgentHelper(intelligentAgentHelper);
      for (int i = 0; i < INTELLIGENT_AGENT_COUNT; ++i) {
        intelligentAgent = new IntelligentAgent(matchingEngine);
        agentList.add(intelligentAgent);
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
    matchingEngine.writeToLog();

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
    if (INTELLIGENT_AGENT_COUNT != 0) {
      // Update the delay data for intelligent agents. A positive number means
      // that there are more buy orders than sell orders at the best bid/ask.
      if (time >= (startupTime - INTELLIGENT_AGENT_DELAY_LENGTH)) {
        intelligentAgentHelper.addData(matchingEngine.getBestBidQuantity()
          - matchingEngine.getBestAskQuantity(), matchingEngine.getBestBid()
          .getPrice(), matchingEngine.getBestAsk().getPrice());
        if (INTELLIGENT_AGENT_THRESHOLD_ENABLE) {
          IntelligentAgent.setOldThresholdState(intelligentAgentHelper
            .getOldThresholdState());
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

    if (time % 500 == 0) {
      graphFrame.updateTitleBar(time, state);
    }
  }
}
