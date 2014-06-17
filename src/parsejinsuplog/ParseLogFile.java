/*
 * This class is responsible for opening the .csv log file 
 * produced by one simulation of JinSup, parse it and store it
 * in a TreeMap of objects of type AgentLog.
 * It also implements the logic for creating orders for each agent
 * and transactions for each order.
 */
package parsejinsuplog;

import java.io.FileReader;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 
 * @author enriqueareyan
 */
public class ParseLogFile {
  /*
   * String with the location of the log file to be parsed.
   */
  private String LogFileLocation;
  /*
   * Store the result of parsing the log file as a Map of agents This map will
   * contain agents which will contain orders which in turn will contain
   * transactions.
   */
  private TreeMap<Long, AgentLog> Agents;
  /*
   * Store the result of parsing the log file as an order book
   */
  private TreeMap<Long, OrderBookItemLog> OrderBook;
  /*
   * Columns index
   */
  private static final int TIME_COL = 0;
  private static final int BEST_BID_COL = 1;
  private static final int BEST_ASK_COL = 2;
  private static final int AGENT_ID_COL = 3;
  private static final int AGENT_TYPE_COL = 4;
  private static final int MESSAGE_COL = 5;
  private static final int BUY_SELL_COL = 6;
  private static final int ORDER_ID_COL = 7;
  private static final int ORIGINAL_QUANTITY_COL = 8;
  private static final int PRICE_COL = 9;
  private static final int TYPE_COL = 10;
  private static final int LEAVES_QUANTITY_COL = 11;
  private static final int TRADE_PRICE_COL = 12;
  private static final int QUANTITY_FILLED_COL = 13;
  private static final int AGGRESSOR_COL = 14;
  private static final int TRADE_MATCH_ID_COL = 15;

  /*
   * Constructor. Needs to be given the location of the log file.
   */
  public ParseLogFile(String LogFileLocation) {
    this.setLogFileLocation(LogFileLocation);
    try {
      Agents = new TreeMap<Long, AgentLog>();
      OrderBook = new TreeMap<Long, OrderBookItemLog>();
      this.readLog();
    } catch (FileNotFoundException e) {
      System.out.println("Log file not found at " + LogFileLocation);
    }
  }

  /*
   * set the location of the log file
   */
  private void setLogFileLocation(String LogFileLocation) {
    this.LogFileLocation = LogFileLocation;
  }

  /*
   * gets the location of the log file
   */
  public String getLogFileLocation() {
    return this.LogFileLocation;
  }

  /*
   * Gets the TreeMap of Agents
   */
  public TreeMap<Long, AgentLog> getAgents() {
    return this.Agents;
  }

  /*
   * Gets the TreeMap of OrderBook
   */
  public TreeMap<Long, OrderBookItemLog> getOrderBook() {
    return this.OrderBook;
  }

  /*
   * This function implements the bulk of the logic related to parsing the .csv
   * log file and creating appropiate OrderLog objects. The result of this
   * function is stored in the ArrayList Orders.
   */
  private void readLog() throws FileNotFoundException {

    /*
     * Initialize variables
     */
    long agentID, orderID, time, tradeMatchID;
    float bestBid, bestAsk, price, tradePrice;
    int j, message, originalQuantity, quantityFilled, leavesQuantity;
    boolean buyOrder, marketOrder;
    char aggressor;
    CSVReader reader = null;
    OrderLog O;
    String agentType;
    AgentLog agent;
    OrderBookItemLog orderBookItem;

    j = 0;

    try {
      // Get the CSVReader instance with specifying the delimiter to be used
      reader = new CSVReader(new FileReader(this.getLogFileLocation()), ',');
      String[] nextLine;
      // Read first line which is the header
      nextLine = reader.readNext();
      // Read one line at a time
      while ((nextLine = reader.readNext()) != null) {
        /*
         * Reset Variables
         */
        time = 0;
        bestBid = 0;
        bestAsk = 0;
        agentID = 0;
        agentType = "";
        message = -1;
        buyOrder = true;
        orderID = 0;
        originalQuantity = 0;
        price = 0;
        marketOrder = false;
        leavesQuantity = -1;
        tradePrice = -1;
        quantityFilled = -1;
        aggressor = ' ';
        tradeMatchID = -1;
        for (String token : nextLine) {
          switch (j) {
            case TIME_COL: // Get Time
              time = Long.parseLong(token);
              break;
            case BEST_BID_COL:
              try {
                bestBid = Float.parseFloat(token);
              } catch (Exception e) {
                bestBid = -1;
              }
              break;
            case BEST_ASK_COL:
              try {
                bestAsk = Float.parseFloat(token);
              } catch (Exception e) {
                bestAsk = -1;
              }
              break;
            case AGENT_ID_COL: // Get Agent ID
              agentID = Long.parseLong(token);
              break;
            case AGENT_TYPE_COL: // Get Agent Type
              agentType = token;
              break;
            case MESSAGE_COL: // Get Message
              message = Integer.parseInt(token);
              break;
            case BUY_SELL_COL: // Get Buy/Sell
              if (token.equals("1")) {
                buyOrder = true;
              } else {
                buyOrder = false;
              }
              break;
            case ORDER_ID_COL: // Get Order ID
              orderID = Long.parseLong(token);
              break;
            case ORIGINAL_QUANTITY_COL: // Get Original Quantity
              originalQuantity = Integer.parseInt(token);
              break;
            case PRICE_COL: // Get Price
              price = Float.parseFloat(token);
              break;
            case TYPE_COL: // Get Limit or Market Order
              if (token.equals("Limit")) {
                marketOrder = false;
              } else {
                marketOrder = true;
              }
              break;
            case LEAVES_QUANTITY_COL: // Get Leaves Quantity
              leavesQuantity = Integer.parseInt(token);
              break;
            case TRADE_PRICE_COL: // Get trade price
              if (!token.equals("")) {
                tradePrice = Float.parseFloat(token);
              } else {
                tradePrice = -1;
              }
              break;
            case QUANTITY_FILLED_COL: // Get Quantity Filled
              if (!token.equals("")) {
                quantityFilled = Integer.parseInt(token.trim());
              } else {
                quantityFilled = -1;
              }
              break;
            case AGGRESSOR_COL: // Get Aggressor
              if (!token.equals("")) {
                aggressor = token.charAt(0);
              } else {
                aggressor = ' ';
              }
              break;
            case TRADE_MATCH_ID_COL: // Get tade match ID
              if (!token.equals("")) {
                tradeMatchID = Long.parseLong(token.trim());
              } else {
                tradeMatchID = -1;
              }
              break;
          }
          j++;
        }
        j = 0;

        // *****Log agent directly, then orders, then transactions ******//

        if (Agents.containsKey(agentID)) {
          agent = Agents.get(agentID);
          // If we are here it means the agent is already in the Agents Map
        } else {
          agent = new AgentLog(agentID, agentType);
          Agents.put(agentID, agent);
        }

        // Check if the order has been already logged in for this agent
        if (agent.getOrders().containsKey(orderID)) {
          O = agent.getOrders().get(orderID);
          // If we are here it means the order has already been logged in
        } else {
          O = new OrderLog(orderID);
          // The order has not been added to the ArrayList
          agent.addOrder(orderID, O);
        }
        // Add the transaction to the order
        O.addTransaction(time, message, buyOrder, originalQuantity, price,
          marketOrder, leavesQuantity, tradePrice, quantityFilled, aggressor,
          tradeMatchID);
        // Create a new entry for the OrderBook
        orderBookItem = new OrderBookItemLog(bestBid, bestAsk);
        try {
          OrderBook.put(time, orderBookItem);
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    } catch (Exception e) {
      System.out.println("Error 1: " + e.getMessage());
    } finally {
      try {
        reader.close();
      } catch (Exception e) {
        System.out.println("Error 2: " + e.getMessage());
      }
    }
  }

  /*
   * Helper method to print all the orders in an array list of orders
   */
  public static void printOrders(Map<Long, OrderLog> Orders) {
    int i = 0;

    for (Long key : Orders.keySet()) {
      System.out.println("Key = " + key + " - " + Orders.get(key));
      i++;
    }
    System.out.println("There are " + i + " orders in total");
  }
}
