/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsejinsuplog;

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author enriqueareyan
 */
public class AgentLog {
  /*
   * Agent ID
   */
  private long agentID;
  /*
   * Type of Agent.
   */
  private String agentType;
  /*
   * An agent is composed of Orders.
   */
  private TreeMap<Long, OrderLog> Orders;

  /*
   * Constructor. Needs the agent ID.
   */
  public AgentLog(long agentID, String agentType) {
    this.setagentID(agentID);
    this.setagentType(agentType);
    this.Orders = new TreeMap<Long, OrderLog>();
  }

  /*
   * set agent ID
   */
  private void setagentID(long agentID) {
    this.agentID = agentID;
  }

  /*
   * get agent ID
   */
  public long getagentID() {
    return this.agentID;
  }

  /*
   * set agent type
   */
  private void setagentType(String agentType) {
    this.agentType = agentType;
  }

  /*
   * get agenty type
   */
  public String getagentType() {
    return this.agentType;
  }

  /*
   * Adds an order to this agent
   */
  public void addOrder(long orderID, OrderLog O) {
    this.Orders.put(orderID, O);
  }

  /*
   * Gets orders
   */
  public TreeMap<Long, OrderLog> getOrders() {
    return this.Orders;
  }

  /*
   * string representation of this object
   */
  @Override
  public String toString() {
    return "=======================================\nagentID:"
      + this.getagentID() + ", agentType:" + this.getagentType() + "\nOrders: "
      + this.getOrders();
  }

  /*
   * Helper method to print all the agents in an array list of agents
   */
  public static void printAgents(Map<Long, AgentLog> Agents) {
    int i = 0;

    for (Long key : Agents.keySet()) {
      System.out.println(Agents.get(key));
      i++;
    }
    System.out.println("There are " + i + " agents in total");
  }
}
