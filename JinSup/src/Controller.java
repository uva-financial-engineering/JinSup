import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Keeps track of simulation time and selects eligible agents so that they can
 * act during their alloted time.
 * 
 */
public class Controller {

  /**
   * List of all agents in the simulator. K = nextActTime, V = agent
   */
  public TreeMap<Long, ArrayList<Agent>> agentMap;

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
    agentMap = new TreeMap<Long, ArrayList<Agent>>();
    time = 0;
    this.startupTime = startupTime;
    this.endTime = endTime;
    this.matchingEngine = matchingEngine;
  }

  /**
   * Select eligible agents randomly to act.
   */
  public void selectActingAgent() {
    for (Map.Entry<Long, ArrayList<Agent>> e : agentMap.entrySet()) {
      if (e.getKey() >= time) {
        moveTime(e.getKey() - time);
        ArrayList<Agent> actingAgents = new ArrayList<Agent>(e.getValue());
        // pick a random agent to activate
        while (!actingAgents.isEmpty()) {
          activateAgent(actingAgents.remove((int) (Math.random() * actingAgents
            .size())));
        }
        break;
      }
    }
    moveTime(1L);
  }

  /**
   * Enable the agent to act until it no longer needs to act.
   * 
   * @param a
   *          Eligible agent to act.
   */
  public void activateAgent(Agent a) {
    long oldGetNextAct = a.getNextActTime();
    a.setWillAct(true);
    a.getWillAct();
    a.act();
    agentMap.get(oldGetNextAct).remove(a);
    if (agentMap.get(a.getNextActTime()) == null) {
      ArrayList<Agent> tmp = new ArrayList<Agent>();
      tmp.add(a);
      agentMap.put(a.getNextActTime(), tmp);
    } else {
      agentMap.get(a.getNextActTime()).add(a);
    }
  }

  /**
   * Method that increments time and performs other necessary actions per time
   * step.
   */
  public void moveTime(long msToMove) {
    for (int i = 0; i < msToMove; i++) {
      matchingEngine.storeMovingAverage(500);
      time += 1;
      if (startupTime == time) {
        matchingEngine.setStartingPeriod(false);
        System.out.println("Trading Enabled!");
        graphFrame.setTradePeriod(startupTime, endTime);
      }
    }
  }

  /**
   * Creates agents with first startup time and runs the simulator and stops it
   * at a specified time given in the main method.
   */
  public void runSimulator() {
    graphFrame = new GraphFrame();
    // create agents
    System.out.println("Creating agents...");
    FundBuyer fundBuyer;
    FundSeller fundSeller;
    for (int i = 0; i < 200; i++) {
      fundBuyer = new FundBuyer(matchingEngine);
      fundBuyer.setNextActTime((long) (Math.random() * startupTime));
      fundSeller = new FundSeller(matchingEngine);
      fundSeller.setNextActTime((long) (Math.random() * startupTime));
      if (agentMap.get(fundBuyer.getNextActTime()) == null) {
        ArrayList<Agent> tmp = new ArrayList<Agent>();
        tmp.add(fundBuyer);
        agentMap.put(fundBuyer.getNextActTime(), tmp);
      } else {
        agentMap.get(fundBuyer.getNextActTime()).add(fundBuyer);
      }
      if (agentMap.get(fundSeller.getNextActTime()) == null) {
        ArrayList<Agent> tmp = new ArrayList<Agent>();
        tmp.add(fundSeller);
        agentMap.put(fundSeller.getNextActTime(), tmp);
      } else {
        agentMap.get(fundSeller.getNextActTime()).add(fundSeller);
      }
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
    System.out.println("The simulation has ended.");
  }
}
