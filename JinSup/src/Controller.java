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
   * Select eligible agents randomly to act.
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
    a.getWillAct();
    a.act();
  }

  /**
   * Method that increments time and performs other necessary actions per time
   * step.
   */
  public void moveTime() {
    matchingEngine.storeMovingAverage(500);
    time += 1;
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time given in the main method.
   */
  public void runSimulator() {
    // create agents
    System.out.print("Creating agents...");
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
    System.out.print("Done!\nSimulation has started");

    // run simulator until endTime is reached.
    while (time < endTime) {
      selectActingAgent();
    }
    System.out.println("The simulation has ended.");
  }
}
