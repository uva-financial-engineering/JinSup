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
  private static final int OPPOR_STRAT_COUNT = 180;

  /**
   * Number of HFT traders
   */
  private static final int HFT_COUNT = 7;

  /**
   * Number of small traders
   */
  private static final int SMALL_TRADER_COUNT = 421;

  /**
   * To speed up the simulation with infinite thresholds, set this to false.
   * Takes precedence over INTELLIGENT_AGENT_THRESHOLD.
   */
  private static final boolean INTELLIGENT_AGENT_THRESHOLD_ENABLE = true;

  /**
   * How often the average profit over all intelligent agents should be logged,
   * in milliseconds.
   */
  private static final int INTELLIGENT_AGENT_PROFIT_LOG_FREQUENCY =
    1000 * 60 * 10;

  /**
   * Location of the log file for intelligent agent average profits. It will be
   * in the same directory as the order log files and named
   * "IAProfits-{time}.csv".
   */
  private final String INTELLIGENT_AGENT_PROFIT_LOG_LOCATION;

  // Specify the lambdas here in seconds
  private static final int FUND_BUYER_SELLER_LAMBDA_ORDER = 40;

  private static final int FUND_BUYER_SELLER_LAMBDA_CANCEL = 60;

  private static final int MARKET_MAKER_LAMBDA_ORDER = 3;

  private static final int MARKET_MAKER_LAMBDA_CANCEL = 2;

  private static final int OPPOR_STRAT_LAMBDA_ORDER = 30;

  private static final int OPPOR_STRAT_LAMBDA_CANCEL = 40;

  private static final double HFT_LAMBDA_ORDER = 0.35;

  private static final double HFT_LAMBDA_CANCEL = 0.4;

  private static final int SMALL_TRADER_LAMBDA_ORDER = 1500;

  private static final int SMALL_TRADER_LAMBDA_CANCEL = 1000;

  /**
   * How often buy probabilities for poisson opportunistic traders should
   * change, in seconds.
   */
  private static final int NEWS_FREQUENCY = 60;

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
      new PoissonDistribution(JinSup.randGen, NEWS_FREQUENCY * 1000,
        PoissonDistribution.DEFAULT_EPSILON,
        PoissonDistribution.DEFAULT_MAX_ITERATIONS);
    lastNewsTime = NEWS_FREQUENCY * 1000;
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
    if (!Settings.isTestMode()) {
      graphFrame.setTradePeriod(Settings.getStartTime(), Settings.getEndTime());
      graphFrame.updateTitleBar(0, "Creating agents...");
    }

    // Create agents
    Order.setNextOrderID(0);

    FundBuyerPoisson fundBuyerPoisson;
    FundSellerPoisson fundSellerPoisson;
    for (int i = 0; i < FUND_BUYER_SELLER_COUNT; ++i) {
      fundBuyerPoisson =
        new FundBuyerPoisson(matchingEngine, FUND_BUYER_SELLER_LAMBDA_ORDER,
          FUND_BUYER_SELLER_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      fundSellerPoisson =
        new FundSellerPoisson(matchingEngine, FUND_BUYER_SELLER_LAMBDA_ORDER,
          FUND_BUYER_SELLER_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      agentList.add(fundBuyerPoisson);
      agentList.add(fundSellerPoisson);
    }

    MarketMakerPoisson marketMakerPoisson;
    for (int i = 0; i < MARKET_MAKER_COUNT; ++i) {
      marketMakerPoisson =
        new MarketMakerPoisson(matchingEngine, MARKET_MAKER_LAMBDA_ORDER,
          MARKET_MAKER_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      agentList.add(marketMakerPoisson);
    }

    OpporStratPoisson opporStratPoisson;
    // Explicitly set global buy probability.
    OpporStratPoisson.setBuyProbability(0.50);
    for (int i = 0; i < OPPOR_STRAT_COUNT; ++i) {
      opporStratPoisson =
        new OpporStratPoisson(matchingEngine, OPPOR_STRAT_LAMBDA_ORDER,
          OPPOR_STRAT_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      agentList.add(opporStratPoisson);
    }

    HFTPoisson hftPoisson;
    for (int i = 0; i < HFT_COUNT; ++i) {
      hftPoisson =
        new HFTPoisson(matchingEngine, HFT_LAMBDA_ORDER, HFT_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      agentList.add(hftPoisson);
    }

    SmallTrader smallTrader;
    for (int i = 0; i < SMALL_TRADER_COUNT; ++i) {
      smallTrader =
        new SmallTrader(matchingEngine, SMALL_TRADER_LAMBDA_ORDER,
          SMALL_TRADER_LAMBDA_CANCEL,
          (long) (JinSup.rand.nextDouble() * Settings.getStartTime()));
      agentList.add(smallTrader);
    }

    if (Settings.getNumIntelligentAgents() != 0) {
      // Set up appropriate number of log files, depending on number of delays
      IntelligentAgentHelper currentIAH;
      IntelligentAgent intelligentAgent;
      String fileName;
      for (Integer l : Settings.getDelays()) {
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
          new IntelligentAgentHelper((int) l, Settings.getThreshold(),
            Settings.getBuyPrice(), INTELLIGENT_AGENT_THRESHOLD_ENABLE);

        intelligentAgentHelpers.add(currentIAH);
        ArrayList<IntelligentAgent> intelligentAgentList =
          new ArrayList<IntelligentAgent>();
        for (int i = 0; i < Settings.getNumIntelligentAgents()
          / Settings.getDelays().size(); i++) {
          intelligentAgent =
            new IntelligentAgent(matchingEngine, currentIAH, l);
          intelligentAgentList.add(intelligentAgent);
          agentList.add(intelligentAgent);
        }
        intelligentAgentByDelay.add(intelligentAgentList);
      }

      // run simulator until Settings.getEndTime() is reached.
      while (time < Settings.getStartTime()) {
        moveTime();
      }
      matchingEngine.setStartingPeriod(false);
      state = "Trading Period";
      while (time < Settings.getEndTime()) {
        moveTime();
      }

      // write remaining entries to the log
      if (!Settings.isTestMode()) {
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

    if (Settings.getNumIntelligentAgents() != 0) {
      // Update the delay data for intelligent agents. A positive number means
      // that there are more buy orders than sell orders at the best bid/ask.
      for (int i = 0; i < Settings.getDelays().size(); i++) {
        if (time >= (Settings.getStartTime() - Settings.getDelays().get(i))) {
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
      for (int i = 0; i < Settings.getDelays().size(); i++) {
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

    if (!Settings.isTestMode() && time % 500 == 0) {
      graphFrame.updateTitleBar(time, state);
    }
  }
}
