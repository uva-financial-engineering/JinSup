package edu.virginia.jinsup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Keeps track of simulation time and selects eligible agents so that they can
 * act during their alloted time.
 * 
 */
public class Controller {

  /**
   * Number of fund buyers (equal to number of fund sellers)
   */
  private static final int FUND_BUYER_SELLER_COUNT = 200;

  /**
   * Number of market makers
   */
  private static final int MARKET_MAKER_COUNT = 10;

  /**
   * Number of oppor strat traders
   */
  private static final int OPPOR_STRAT_COUNT = 40;

  private static final int HFT_COUNT = 20;

  private static final int SMALL_TRADER_COUNT = 10;

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
   * to trade.
   */
  private final long startupTime;

  /**
   * The time to stop the simulator.
   */
  private final long endTime;

  /**
   * The MatchingEngine used for this simulation.
   */
  private final MatchingEngine matchingEngine;

  /**
   * Queue of agents scheduled to act
   */
  private final PriorityQueue<Agent> actQueue;

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
    this.actQueue =
      new PriorityQueue<Agent>(FUND_BUYER_SELLER_COUNT * 2 + MARKET_MAKER_COUNT
        + OPPOR_STRAT_COUNT);
  }

  /**
   * Selects all eligible agents in random order to act during a given time
   * slot.
   */
  public void selectActingAgent() {
    LinkedList<Agent> actingAgents = new LinkedList<Agent>();
    for (Agent a : agentList) {
      if (a.getNextActTime() == time) {
        actingAgents.add(a);
      }
    }
    // pick a random agent to activate
    while (!actingAgents.isEmpty()) {
      activateAgent(actingAgents.remove((int) (Math.random() * actingAgents
        .size())));
    }
  }

  /**
   * Enable the agent to act until it no longer needs to act.
   * 
   * @param a
   *          Eligible agent to act.
   */
  public void activateAgent(Agent a) {
    a.setWillAct(true);
    do {
      a.act();
    } while (a.getWillAct());
  }

  /**
   * Method that increments simulator time and performs other necessary actions
   * after each time step.
   */
  public void moveTime() {
    matchingEngine.storeMovingAverage();
    matchingEngine.reset();
    time += 1;
    if (time % 500 == 0) {
      graphFrame.updateTitleBar(time);
    }
    if (time == startupTime) {
      matchingEngine.setStartingPeriod(false);
      System.out.println("Trading Enabled!");
    }
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time specified by the user.
   */
  public void runSimulator() {
    graphFrame.setTradePeriod(startupTime, endTime);

    // Create agents

    System.out.println("Creating agents...");

    FundBuyerPoisson fundBuyerPoisson;
    FundSellerPoisson fundSellerPoisson;
    for (int i = 0; i < FUND_BUYER_SELLER_COUNT; ++i) {
      fundBuyerPoisson = new FundBuyerPoisson(matchingEngine, 80, 60);
      fundBuyerPoisson.setNextActTime((long) (Math.random() * startupTime));
      fundSellerPoisson = new FundSellerPoisson(matchingEngine, 80, 60);
      fundSellerPoisson.setNextActTime((long) (Math.random() * startupTime));
      agentList.add(fundBuyerPoisson);
      agentList.add(fundSellerPoisson);
      actQueue.add(fundBuyerPoisson);
      actQueue.add(fundSellerPoisson);
    }

    MarketMakerPoisson marketMakerPoisson;
    for (int i = 0; i < MARKET_MAKER_COUNT; ++i) {
      marketMakerPoisson = new MarketMakerPoisson(matchingEngine, 6, 2);
      marketMakerPoisson.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(marketMakerPoisson);
    }

    OpporStratPoisson opporStratPoisson;
    for (int i = 0; i < OPPOR_STRAT_COUNT; ++i) {
      opporStratPoisson = new OpporStratPoisson(matchingEngine, 60, 40, 0.50);
      opporStratPoisson.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(opporStratPoisson);
    }

    HFTPoisson hftPoisson;
    for (int i = 0; i < HFT_COUNT; ++i) {
      hftPoisson = new HFTPoisson(matchingEngine, 0.60, 0.40);
      hftPoisson.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(hftPoisson);
    }

    SmallTrader smallTrader;
    for (int i = 0; i < SMALL_TRADER_COUNT; ++i) {
      smallTrader = new SmallTrader(matchingEngine, 3000, 1000);
      smallTrader.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(smallTrader);
    }

    System.out.println("Done! Simulation has started");

    // run simulator until endTime is reached.
    long nextActTime = actQueue.peek().getNextActTime();
    // TODO Loop moveTime until nextActTime without testing whether nextActTime
    // == time
    while (time < endTime) {
      selectActingAgent();
      // activateAgent(actQueue.poll());
      moveTime();
    }

    // write remaining entries to the log
    matchingEngine.writeToLog();

    System.out.println("The simulation has ended.");
  }
}