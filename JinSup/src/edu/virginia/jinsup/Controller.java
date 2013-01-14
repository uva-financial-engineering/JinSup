package edu.virginia.jinsup;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Keeps track of simulation time and selects eligible agents so that they can
 * act during their alloted time.
 * 
 */
public class Controller {

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
   * Creates a controller with no agents.
   */
  public Controller(long startupTime, long endTime,
    MatchingEngine matchingEngine) {
    agentList = new ArrayList<Agent>();
    time = 0;
    this.startupTime = startupTime;
    this.endTime = endTime;
    this.matchingEngine = matchingEngine;
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
    moveTime();
    if (time == startupTime) {
      matchingEngine.setStartingPeriod(false);
      System.out.println("Trading Enabled!");
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
    while (a.getWillAct()) {
      a.act();
    }
  }

  /**
   * Method that increments simulator time and performs other necessary actions
   * after each time step.
   */
  public void moveTime() {
    matchingEngine.storeMovingAverage(500);
    matchingEngine.reset();
    time += 1;
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time specified by the user.
   */
  public void runSimulator() {
    graphFrame.setTradePeriod(startupTime, endTime);
    // create agents
    System.out.println("Creating agents...");
    FundBuyer fundBuyer;
    FundSeller fundSeller;
    for (int i = 0; i < 200; i++) {
      fundBuyer = new FundBuyer(matchingEngine);
      fundBuyer.setNextActTime((long) (Math.random() * startupTime));
      fundSeller = new FundSeller(matchingEngine);
      fundSeller.setNextActTime((long) (Math.random() * startupTime));
      agentList.add(fundBuyer);
      agentList.add(fundSeller);
    }

    MarketMaker marketMaker;
    for (int i = 0; i < 10; i++) {
      marketMaker = new MarketMaker(matchingEngine);
      marketMaker.setNextActTime((long) (Math.random() * startupTime));
    }

    OpporStrat opporStrat;
    for (int i = 0; i < 40; i++) {
      opporStrat = new OpporStrat(matchingEngine);
      opporStrat.setNextActTime((long) (Math.random() * startupTime));
    }
    System.out.println("Done!\nSimulation has started");

    // run simulator until endTime is reached.
    while (time < endTime) {
      selectActingAgent();
    }

    // write remaining entries to the log
    matchingEngine.writeToLog();

    System.out.println("The simulation has ended.");
  }
}