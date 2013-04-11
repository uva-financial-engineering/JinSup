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

    FundBuyer fundBuyer;
    FundSeller fundSeller;
    for (int i = 0; i < FUND_BUYER_SELLER_COUNT; ++i) {
      fundBuyer = new FundBuyer(matchingEngine);
      fundBuyer.setNextActTime((long) (Math.random() * startupTime));
      fundSeller = new FundSeller(matchingEngine);
      fundSeller.setNextActTime((long) (Math.random() * startupTime));
      agentList.add(fundBuyer);
      agentList.add(fundSeller);
      actQueue.add(fundBuyer);
      actQueue.add(fundSeller);
    }

    MarketMaker marketMaker;
    for (int i = 0; i < MARKET_MAKER_COUNT; ++i) {
      marketMaker = new MarketMaker(matchingEngine);
      marketMaker.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(marketMaker);
    }

    OpporStrat opporStrat;
    for (int i = 0; i < OPPOR_STRAT_COUNT; ++i) {
      opporStrat = new OpporStrat(matchingEngine);
      opporStrat.setNextActTime((long) (Math.random() * startupTime));
      actQueue.add(opporStrat);
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