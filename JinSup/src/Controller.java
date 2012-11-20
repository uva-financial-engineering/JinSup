import java.util.ArrayList;

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
  private long time;

  /**
   * The time when the simulation should end the startup period and allow agents
   * to trade.
   */
  private final long startupTime;

  /**
   * The MatchingEngine used for this simulation.
   */
  private final MatchingEngine matchingEngine;

  /**
   * Creates a controller with no agents.
   */
  public Controller(long startupTime, MatchingEngine matchingEngine) {
    agentList = new ArrayList<Agent>();
    time = 0;
    this.startupTime = startupTime;
    this.matchingEngine = matchingEngine;
  }

  /**
   * Select eligible agents randomly to act.
   */
  public void selectActingAgent() {
    ArrayList<Agent> actingAgents = new ArrayList<Agent>();
    for (Agent a : agentList) {
      if (a.getNextActTime() == time) {
        actingAgents.add(a);
      }
    }
    // pick a random agent to activate
    while (!actingAgents.isEmpty()) {
      int agentIndex = (int) (Math.random() * actingAgents.size());
      Agent acting = actingAgents.remove(agentIndex);
      acting.setWillAct(true);
      activateAgent(acting);
    }
    time += 1;
    if (time == startupTime) {
      matchingEngine.setStartingPeriod(false);
    }
  }

  /**
   * Enable the agent to act until it no longer needs to act.
   * 
   * @param a
   *          Eligibble agent to act.
   */
  public void activateAgent(Agent a) {
    while (a.getWillAct()) {
      a.act();
    }
    // also log the action
  }

}
