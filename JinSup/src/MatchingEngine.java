import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class that handles order creation, modification, and cancellation. Also deals
 * with trades and provides agents with appropriate trade data from the last
 * millisecond of trading. *
 */
public class MatchingEngine {

  /**
   * All the orders in the simulation, grouped by agent ID.
   */
  private final HashMap<Long, ArrayList<Order>> orderMap;

  /**
   * All the agents in the simulation, indexed by agent ID.
   */
  private final HashMap<Long, Agent> agentMap;

  /**
   * All the orders in the simulation, unsorted.
   */
  private final ArrayList<Order> allOrders;

  /**
   * The volume of shares sold that were initiated by an aggressive buying agent
   * in the last millisecond of trading.
   * 
   * TODO make this work
   */
  private int lastAgVolumeBuySide;

  /**
   * The volumes of shares sold that were initiated by an aggressive selling
   * agent in the last millisecond of trading.
   */
  private int lastAgVolumeSellSide;

  /**
   * The current (per ms) volume of shares that were initiated by an aggressive
   * buying agent in the last millisecond of trading.
   */
  private int currAgVolumeBuySide;

  /**
   * The current (per ms) volume of shares that were initiated by an aggressive
   * selling agent in the last millisecond of trading.
   */
  private int currAgVolumeSellSide;

  /**
   * The price that the share was last traded at. This should be plotted every
   * time it is updated (i.e. whenever a trade occurs).
   */
  private int lastTradePrice;

  /**
   * Remains true while the simulator is still in the starting period. This
   * means that all orders that my result in a trade will be cancelled.
   */
  private boolean startingPeriod;

  /**
   * The buy price for a share.
   */
  private final int buyPrice;

  private final ArrayList<String> logBuffer;

  /**
   * Queue containing the midpoints of best bid and best ask prices. The length
   * of the list is determined by the get moving average method.
   */
  private final LinkedList<Integer> midpoints;

  /**
   * Stores moving sum, which is used to efficiently calculate moving average.
   */
  private int movingSum;

  /**
   * Creates a matching engine with empty fields. Everything is initialized to
   * zero.
   */
  public MatchingEngine(int buyPrice) {
    orderMap = new HashMap<Long, ArrayList<Order>>();
    allOrders = new ArrayList<Order>();
    agentMap = new HashMap<Long, Agent>();
    midpoints = new LinkedList<Integer>();
    lastAgVolumeBuySide = 0;
    lastAgVolumeSellSide = 0;
    lastTradePrice = 0;
    startingPeriod = true;
    movingSum = 0;
    this.buyPrice = buyPrice;

    // 2^19 lines before writing to file
    logBuffer = new ArrayList<String>(524288);

    // create the CSV file
    try {
      FileWriter writer = new FileWriter(Controller.graphFrame.getDest());
      writer
        .append("Agent ID, Message, Buy/Sell, Order ID, Original Quantity, Price, Type, Leaves Quantity, Trade Price, Aggressor, Trade Match ID\n");
      writer.flush();
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Deletes an order from the simulation (orderMap and allOrders).
   * 
   * @param o
   *          The order to be removed.
   * @param agentID
   *          ID of the agent whose order is to be removed.
   */
  public void cancelOrder(Order o, boolean market) {
    allOrders.remove(o);
    orderMap.get(o.getCreatorID()).remove(o);
    // log the action.
    if (!market) {
      logOrder(o, 3, false, -1 * o.getCurrentQuant(), 0);
    } else {
      logOrder(o, 3, true, -1 * o.getCurrentQuant(), 0);
    }
    o.setQuant(0);
  }

  /**
   * Inserts the agent into the MatchingEngine's agentMap so that it can keep
   * track of it. This is called every time a new agent is constructed so it
   * should not have to be explicitly called.
   * 
   * @param id
   *          ID of the agent.
   * @param agent
   *          Agent object to be added.
   */
  public void addNewAgent(long id, Agent agent) {
    agentMap.put(id, agent);
  }

  /**
   * Takes a newly created order and stores it in the MatchingEngine's allOrders
   * and orderMap so that it can keep track of it. Also checks if any trade
   * occurs from this newly created order.
   * 
   * @param order
   *          New order to be inserted into the MatchingEngine.
   * @param agentID
   *          ID of the agent that initiated the order.
   */
  public boolean createOrder(Order order, boolean market) {
    allOrders.add(order);
    if (orderMap.containsKey(order.getCreatorID())) {
      orderMap.get(order.getCreatorID()).add(order);
    } else {
      ArrayList<Order> orderList = new ArrayList<Order>();
      orderList.add(order);
      orderMap.put(order.getCreatorID(), orderList);
    }
    // log the action.
    // must then check if a trade can occur

    if (!market) {
      logOrder(order, 1, false, 0, 0);
      return trade(order, willTrade(order));
    }
    logOrder(order, 1, true, 0, 0);
    return false;
  }

  // TODO must account for the case when there are not enough orders to satisfy
  // a market order.
  /**
   * Trades market orders only. If there are not orders to satisfy the market
   * order, then the order is cancelled.
   * 
   * @param order
   *          The market order to be traded.
   */
  public void tradeMarketOrder(Order order) {
    // have to add the order to the orderMap and all orders, otherwise the
    // trade method will not work.
    createOrder(order, true);
    if (startingPeriod) {
      cancelOrder(order, true);
      return;
    }
    // save price for logging at the end.
    int price = 0;

    long aggressorID = order.getCreatorID();
    int totalVolumeTraded = 0;
    int quantityToRid = order.getCurrentQuant();
    if (order.isBuyOrder()) {
      ArrayList<Order> topSells = topSellOrders();
      if (topSells.isEmpty()) {
        // nothing was traded. order will be cancelled.
        cancelOrder(order, true);
        return;
      }
      while (!topSells.isEmpty()) {
        price = topSells.get(0).getPrice();
        int currentVolumeTraded = trade(order, topSells.get(0));
        totalVolumeTraded += currentVolumeTraded;

        // notify the non aggressor.
        agentMap.get(topSells.get(0).getCreatorID()).setLastOrderTraded(true,
          currentVolumeTraded);

        if (totalVolumeTraded < quantityToRid) {
          topSells.remove(0);
        } else {
          break;
        }
      }
      // notify the aggressor
      agentMap.get(aggressorID).setLastOrderTraded(true, totalVolumeTraded);
      lastAgVolumeBuySide += totalVolumeTraded;
    } else {
      ArrayList<Order> topBuys = topBuyOrders();

      if (topBuys.isEmpty()) {
        // nothing was traded. order will be cancelled.
        cancelOrder(order, true);
        return;
      }
      while (!topBuys.isEmpty()) {
        price = topBuys.get(0).getPrice();
        int currentVolumeTraded = trade(order, topBuys.get(0));
        totalVolumeTraded += currentVolumeTraded;

        // notify the non aggressor.
        agentMap.get(topBuys.get(0).getCreatorID()).setLastOrderTraded(true,
          currentVolumeTraded);

        if (totalVolumeTraded < quantityToRid) {
          topBuys.remove(0);
        } else {
          break;
        }
      }
      // notify the aggressor
      agentMap.get(aggressorID).setLastOrderTraded(true, totalVolumeTraded);
      lastAgVolumeSellSide += totalVolumeTraded;
    }
    // System.out.print("Market ORDER ");
    lastTradePrice = price;
    logTrade(order, price, totalVolumeTraded);
    logAggressiveTrader(order, true, price, totalVolumeTraded);

  }

  // can use the code below to replace some code in checkMakeTrade()
  // we cannot use this to notify agents that a trade occurs because
  // tradeMarketOrder() is handled differently than checkMakeTrade().
  /**
   * @param o1
   *          Order of the aggressive agent.
   * @param o2
   *          Order of the passive agent.
   * @return The volume that was traded between the two orders.
   */
  private int trade(Order o1, Order o2) {
    // save price for logging at the end.
    int price = o2.getPrice();
    lastTradePrice = price;

    int volumeTraded;
    if (o1.getCurrentQuant() == o2.getCurrentQuant()) {
      volumeTraded = o1.getCurrentQuant();
      allOrders.remove(o1);
      allOrders.remove(o2);
      o1.setQuant(0);
      o2.setQuant(0);
      orderMap.get(o1.getCreatorID()).remove(o1);
      orderMap.get(o2.getCreatorID()).remove(o2);

    } else if (o1.getCurrentQuant() > o2.getCurrentQuant()) {
      // delete orderToTrade, decrease quantity of o
      volumeTraded = o1.getCurrentQuant() - o2.getCurrentQuant();
      o1.setQuant(volumeTraded);
      allOrders.remove(o2);
      o2.setQuant(0);
      orderMap.get(o2.getCreatorID()).remove(o2);
    } else {
      volumeTraded = o2.getCurrentQuant() - o1.getCurrentQuant();
      o2.setQuant(volumeTraded);
      allOrders.remove(o1);
      o1.setQuant(0);
      orderMap.get(o1.getCreatorID()).remove(o1);
    }

    // NO LOGGING HERE! LOGGING IS DONE IN THE OTHER TWO TRADE METHODS!
    // except for logging extras....

    logExtraTrades(o2, price, volumeTraded);

    return volumeTraded;
  }

  /**
   * Modifies the order in the MatchingEngine's allOrders and orderMap. Also
   * checks if any trade occurs from this modification.
   * 
   * @param o
   *          Order being modified.
   * @param newPrice
   *          The new price the order should have.
   * @param newQuant
   *          The new quantity the order should have.
   */
  public boolean modifyOrder(Order o, int newPrice, int newQuant) {
    int priceDiff = newPrice - o.getPrice();
    int quantDiff = newQuant - o.getCurrentQuant();
    o.setPrice(newPrice);
    o.setQuant(newQuant);
    // log the action
    logOrder(o, 2, false, quantDiff, priceDiff);
    // must then check if a trade can occur
    return trade(o, willTrade(o));
  }

  /**
   * @return The last price that a share was traded for. Used primarily as data
   *         for the bar chart.
   */
  public double getLastTradePrice() {
    return lastTradePrice;
  }

  /**
   * 
   * @return The top ten pending buy orders sorted by price (highest) and time
   *         of order creation (most recent).
   */
  public ArrayList<Order> topBuyOrders() {
    ArrayList<Order> topBuyOrders = new ArrayList<Order>();
    for (Order o : allOrders) {
      if (o.isBuyOrder()) {
        if (topBuyOrders.size() < 10) {
          topBuyOrders.add(o);
          Collections.sort(topBuyOrders, Order.highestFirstComparator);
        } else if (o.compare(o, topBuyOrders.get(9)) < 0) {
          topBuyOrders.set(9, o);
          Collections.sort(topBuyOrders, Order.highestFirstComparator);
        }
      }
    }
    return topBuyOrders;
  }

  /**
   * @return The top ten sell orders sorted by price (lowest) and time of order
   *         creation (most recent).
   */
  public ArrayList<Order> topSellOrders() {
    ArrayList<Order> topSellOrders = new ArrayList<Order>();
    for (Order a : allOrders) {
      if (!a.isBuyOrder()) {
        if (topSellOrders.size() < 10) {
          topSellOrders.add(a);
          Collections.sort(topSellOrders, Order.lowestFirstComparator);
        } else if (Order.lowestFirstComparator.compare(a, topSellOrders.get(9)) < 0) {
          topSellOrders.set(9, a);
          Collections.sort(topSellOrders, Order.lowestFirstComparator);
        }
      }
    }
    return topSellOrders;
  }

  /**
   * Checks if an order will make cause a trade.
   * 
   * @param order
   *          The order to check for a trade
   * @return Orders that have the same price, if a trade can be made. Otherwise,
   *         null.
   */
  public ArrayList<Order> willTrade(Order order) {
    ArrayList<Order> samePrice = new ArrayList<Order>();
    if (order.isBuyOrder()) {
      // check for sell orders at the same sell price
      // must be sure to pick orders that were placed first.
      for (Order o : allOrders) {
        if (!o.isBuyOrder() && o.getPrice() == order.getPrice()) {
          samePrice.add(o);
        }
      }
    } else {
      for (Order o : allOrders) {
        if (o.isBuyOrder() && o.getPrice() == order.getPrice()) {
          samePrice.add(o);
        }
      }
    }
    if (samePrice.isEmpty()) {
      // trade was not made
      return null;
    } else {
      return samePrice;
    }
  }

  /**
   * Performs the trade for limit orders. If samePricedOrders is null, i.e. the
   * order will not make a trade, then routine simply exits.
   * 
   * @param order
   *          The order to be traded.
   * @param samePricedOrders
   *          Orders having the same price as the order to be traded.
   * 
   * @return True if the trade was made.
   */
  public boolean trade(Order order, ArrayList<Order> samePricedOrders) {
    if (samePricedOrders == null) {
      return false;
    }

    // save price for logging below
    int price = order.getPrice();

    if (startingPeriod) {
      cancelOrder(order, true);
      return false;
    }

    lastTradePrice = samePricedOrders.get(0).getPrice();

    // now select the orders that were made first.
    Collections.sort(samePricedOrders, Order.highestFirstComparator);

    Order orderToTrade = samePricedOrders.get(0);

    int volumeTraded = trade(order, orderToTrade);

    // log the action and ID of trade, with System.currentTimeMillis()
    // and volume traded.
    // notify both agents that a trade has occurred.

    if (order.isBuyOrder()) {
      lastAgVolumeBuySide += volumeTraded;
    } else {
      lastAgVolumeSellSide += volumeTraded;
    }
    // now get the agents (aggressor and non-aggressor) and notify them.
    agentMap.get(order.getCreatorID()).setLastOrderTraded(true, volumeTraded);
    agentMap.get(orderToTrade.getCreatorID()).setLastOrderTraded(true,
      volumeTraded);

    // System.out.print("LIMIT ORDER ");
    logTrade(order, price, volumeTraded);
    logAggressiveTrader(order, false, price, volumeTraded);
    return true;
  }

  /**
   * @return The order with the highest bid price.
   */
  public Order getBestBid() {
    ArrayList<Order> bids = new ArrayList<Order>();
    for (Order o : allOrders) {
      if (o.isBuyOrder()) {
        bids.add(o);
      }
    }
    if (bids.isEmpty()) {
      return null;
    }
    Collections.sort(bids, Order.highestFirstComparator);
    // want the highest bid price
    return bids.get(0);
  }

  /**
   * @return The order with the lowest ask price.
   */
  public Order getBestAsk() {
    ArrayList<Order> asks = new ArrayList<Order>();
    for (Order o : allOrders) {
      if (!o.isBuyOrder()) {
        asks.add(o);
      }
    }
    if (asks.isEmpty()) {
      return null;
    }
    Collections.sort(asks, Order.highestFirstComparator);
    // want the lowest asking price
    return asks.get(asks.size() - 1);
  }

  /**
   * @return All orders in the simulation.
   */
  public ArrayList<Order> getAllOrders() {
    return allOrders;
  }

  /**
   * Sets the matching to allow or disallow trades to occur based on whether or
   * not the simulation is still running in startup mode.
   * 
   * @param isStartingPeriod
   *          If true, then the simulation will remain under startup mode and
   *          trades will not be allowed. Otherwise, trades will be enabled.
   */
  public void setStartingPeriod(boolean isStartingPeriod) {
    startingPeriod = isStartingPeriod;
  }

  /**
   * @return The buy price for a share.
   */
  public int getBuyPrice() {
    return buyPrice;
  }

  /**
   * Stores lastAgVolumeBuySide and lastAgVolumeSellSideResets for the previous
   * millisecond of trading and resets currAgVolumeBuySide and
   * currAgVolumeSellSide.
   */
  public void reset() {
    lastAgVolumeBuySide = currAgVolumeBuySide;
    lastAgVolumeSellSide = currAgVolumeSellSide;
    currAgVolumeBuySide = 0;
    currAgVolumeSellSide = 0;
  }

  /**
   * A special method for opportunistic traders that returns the moving average
   * for the last n number of milliseconds.
   */
  public void storeMovingAverage(int n) {
    int midpoint =
      (getBestBid() == null || getBestAsk() == null) ? buyPrice + 12
        : ((getBestBid().getPrice() + getBestAsk().getPrice()) / 2);
    if (midpoints.size() > n) {
      movingSum -= midpoints.poll();
    }
    movingSum += midpoint;
    midpoints.add(midpoint);
  }

  /**
   * Provides agents with the moving average with length of time specified by
   * the calculateMovingAverage() method.
   */
  public int getMovingAverage() {
    return movingSum / midpoints.size();
  }

  /**
   * Logs all the required information into the order book and updates graph
   * when an order is created, modified, or deleted. The CSV to be logged will
   * have the following fields: Agent ID, Message Type (1 = new order, 2 =
   * modification , 3 = cancel, 105 = trade), Buy/Sell (1/2), Order ID, Original
   * Order Quantity, Price, Order Type (limit/market), Leaves Quantity.
   * 
   * @param order
   *          Order to log
   * @param messageType
   *          Message type
   * @param market
   *          True if logging a market order. False if logging a limit order
   */
  public void logOrder(Order order, int messageType, boolean market,
    int quantChanged, int priceChanged) {

    if (!market) {
      switch (messageType) {
        case 1:
          // if (!order.isBuyOrder()) {
          // System.out
          // .println(order.getPrice() + " " + order.getCurrentQuant());
          // }
          Controller.graphFrame.addOrder(order.isBuyOrder(),
            order.getCurrentQuant(), order.getPrice());
          break;
        case 2:
          if (priceChanged != 0) {
            // delete all orders from the old price point
            Controller.graphFrame.addOrder(order.isBuyOrder(),
              -order.getCurrentQuant(), order.getPrice() - priceChanged);
            // re-add the orders to the new price point
          } else {
            Controller.graphFrame.addOrder(order.isBuyOrder(), quantChanged,
              order.getPrice());
          }
          break;
        case 3:
          Controller.graphFrame.addOrder(order.isBuyOrder(), quantChanged,
            order.getPrice());
          break;
        default:
          System.out.println("Message type " + messageType + " is invalid.");
          System.exit(1);
          break;
      }
    }

    if (logBuffer.size() == 524288) {
      // write the stuff to the file.
      writeToLog();
    }
    logBuffer.add(order.getCreatorID() + "," + messageType + ","
      + (order.isBuyOrder() ? "1" : "2") + "," + order.getId() + ","
      + order.getOriginalQuant() + "," + order.getPrice() / 100.0 + ","
      + (market ? "Market" : "Limit") + "," + order.getCurrentQuant() + "\n");
  }

  /**
   * Logs all the required information into the orderbook and updates graph if
   * trade occurs. In addition to the fields above, this method will also add
   * fields for Price of Trade, Quantity Filled, Aggressor Indicator (Y/N), and
   * Trade ID. Calls updateGraph() if needed.
   * 
   * @param agOrder
   *          The order of the aggressive agent.
   * @param tradePrice
   *          The price that the trade occurred at.
   * @param volume
   *          The volume that was traded.
   */
  public void logTrade(Order agOrder, int tradePrice, int volume) {
    // TODO when a market order occurs, what is the last trade price that we
    // log? There is a case when a market order depletes a price point
    // TODO if logTrade only updates the graph now, we can just call addTrade in
    // the trade methods every time we want to update the trade price

    Controller.graphFrame.addTrade(Controller.time / 1000.0, tradePrice);
  }

  /**
   * A logging method that is called to log aggressive orders only.
   * 
   * @param agOrder
   *          The aggressive order to be logged.
   * @param market
   *          True if the aggressive order is a market order.
   * @param tradePrice
   *          The price that the trade occurred at.
   * @param volume
   *          The volume that was traded on this order.
   */
  public void logAggressiveTrader(Order agOrder, boolean market,
    int tradePrice, int volume) {

    if (agOrder.isBuyOrder()) {
      currAgVolumeBuySide += volume;
    } else {
      currAgVolumeSellSide += volume;
    }

    Controller.graphFrame.addOrder(agOrder.isBuyOrder(), -volume, tradePrice);
    if (logBuffer.size() == 524288) {
      // write the stuff to the file.
      // logging for the passive order
      writeToLog();
    }
    logBuffer.add(agOrder.getCreatorID() + ",105,"
      + (agOrder.isBuyOrder() ? "1" : "2") + "," + agOrder.getId() + ","
      + agOrder.getOriginalQuant() + "," + agOrder.getPrice() / 100.0 + ","
      + (market ? "Market," : "Limit,") + agOrder.getCurrentQuant() + ","
      + tradePrice + "," + volume + ",Y," + System.currentTimeMillis() + "\n");

  }

  /**
   * A logging method that is called to log passive orders only.
   * 
   * @param passOrder
   *          The passive order to be logged.
   * @param tradePrice
   *          The price that the trade occurred at.
   * @param volume
   *          The volume that was traded on this order.
   */
  public void logExtraTrades(Order passOrder, int tradePrice, int volume) {
    if (passOrder.isBuyOrder()) {
      currAgVolumeBuySide += volume;
    } else {
      currAgVolumeSellSide += volume;
    }

    Controller.graphFrame.addOrder(passOrder.isBuyOrder(), -volume, tradePrice);
    if (logBuffer.size() == 524288) {
      // write the stuff to the file.
      // logging for the passive order
      writeToLog();

    }
    logBuffer.add(passOrder.getCreatorID() + ",105,"
      + (passOrder.isBuyOrder() ? "1" : "2") + "," + passOrder.getId() + ","
      + passOrder.getOriginalQuant() + "," + passOrder.getPrice() / 100.0
      + ",Limit," + passOrder.getCurrentQuant() + "," + tradePrice + ","
      + volume + ",N," + System.currentTimeMillis() + "\n");

  }

  /**
   * Writes the logBuffer contents to the file and then clears the log buffer.
   */
  public void writeToLog() {
    try {
      FileWriter writer = new FileWriter(Controller.graphFrame.getDest(), true);
      for (int i = 0; i < logBuffer.size(); i++) {
        writer.append(logBuffer.get(i));
      }
      writer.flush();
      writer.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    logBuffer.clear();
  }

  /**
   * @return The volume of aggressive shares bought in the last millisecond of
   *         trading.
   */
  public int getLastAgVolumeBuySide() {
    return lastAgVolumeBuySide;
  }

  /**
   * @return The volume of aggressive shares sold in the last millisecond of
   *         trading.
   */
  public int getLastAgVolumeSellSide() {
    return lastAgVolumeSellSide;
  }

}