/*
 * This class implements the logic for analyzing the log of one run of jinsup simulator.
 */
package parsejinsuplog;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 
 * @author enriqueareyan
 */
public class SimulationLog {
  /*
   * A simulation is composed of agents, here represented as a Map of objects of
   * type AgentLog
   */
  private Map<Long, AgentLog> Agents;
  /*
   * A simulation also has an order book attached to it
   */
  private OrderBookLog OrderBook;

  /*
   * Constructor. Needs the map of agents.
   */
  public SimulationLog(Map<Long, AgentLog> Agents) {
    this.Agents = Agents;
  }

  /*
   * Constructor. Needs the map of agents and the order book
   */
  public SimulationLog(Map<Long, AgentLog> Agents,
    Map<Long, OrderBookItemLog> OrderBook) {
    this.Agents = Agents;
    this.OrderBook = new OrderBookLog(OrderBook);
  }

  /*
   * Summarize the information in the simulation
   */
  public void createRFile(String RFIleLocation) {
    int totalTransactions = 0, i = 1;
    String typeOfAgent = "", y = "y = c(";
    System.out.println("ID\t Type\t #Ord.\t #Tr.");
    try {
      PrintWriter writer = new PrintWriter(RFIleLocation, "UTF-8");
      writer.println("library(e1071)");
      writer.println("x = matrix(," + this.Agents.size() + ",2)");
      for (Long key : this.Agents.keySet()) { // loop through each agent
        for (Long key2 : this.Agents.get(key).getOrders().keySet()) {// loop
                                                                     // through
                                                                     // each
                                                                     // order
          // Count the total number of transactions per order
          totalTransactions +=
            this.Agents.get(key).getOrders().get(key2).getTransactions().size();
        }
        if (this.Agents.get(key).getagentType().equals("HFTPoisson")) {
          typeOfAgent = "1"; // 1 is a HFT
        } else {
          typeOfAgent = "-1";// -1 is any other type of agent other than HFT
        }
        y = y + typeOfAgent + ",";
        // Print summary to the terminal
        System.out.println(this.Agents.get(key).getagentID() + "\t "
          + typeOfAgent + "\t" + this.Agents.get(key).getOrders().size() + "\t"
          + totalTransactions);
        // Write summary to csv file
        writer.println("x[" + i + ",1] = "
          + this.Agents.get(key).getOrders().size());
        writer.println("x[" + i + ",2] = " + totalTransactions);
        totalTransactions = 0;
        i++;
      }
      y = y.replaceAll(",$", "");
      y = y + ")";
      writer.println(y);
      writer.println("dat=data.frame(x=x, y=as.factor(y))");
      writer
        .println("svmfit = svm(y~., data = dat, kernel = \"linear\", cost = 10, scale = FALSE)");
      writer.println("plot(svmfit,dat)");
      writer.println("svmfit$index");
      writer.println("summary(svmfit)");
      writer.close();
    } catch (Exception e) {
      System.out.println("Error in function summarizeData(): " + e);
    }
  }

  /*
   * Helper method to print all the agents in an array list of agents
   */
  public void printAgentSimulation() {
    int i = 0;

    for (Long key : this.Agents.keySet()) {
      System.out.println(this.Agents.get(key));
      i++;
    }
    System.out.println("There are " + i + " agents in total");
  }

  /*
   * Helper methor to print the order book
   */
  public void printOrderBook() {
    this.OrderBook.printOrderBookLog();
  }
}
