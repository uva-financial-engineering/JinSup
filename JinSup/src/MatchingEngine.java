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
   */
  private int lastAgVolumeBuySide;

  /**
   * The volumes of shares sold that were initiated by an aggressive selling
   * agent in the last millisecond of trading.
   */
  private int lastAgVolumeSellSide;

  /**
   * The price that the share was last traded at. This should be plotted every
   * time it is updated (i.e. whenever a trade occurs).
   */
  private double lastTradePrice;

  /**
   * Remains true while the simulator is still in the starting period. This
   * means that all orders that my result in a trade will be cancelled.
   */
  private boolean startingPeriod;

  /**
   * The buy price for a share.
   */
  private final long buyPrice;

  /**
   * Queue containing the midpoints of best bid and best ask prices. The length
   * of the list is determined by the get moving average method.
   */
  private final LinkedList<Long> midpoints;

  /**
   * Creates a matching engine with empty fields. Everything is initialized to
   * zero.
   */
  public MatchingEngine(long buyPrice) {
    orderMap = new HashMap<Long, ArrayList<Order>>();
    allOrders = new ArrayList<Order>();
    agentMap = new HashMap<Long, Agent>();
    midpoints = new LinkedList<Long>();
    lastAgVolumeBuySide = 0;
    lastAgVolumeSellSide = 0;
    lastTradePrice = 0;
    startingPeriod = true;
    this.buyPrice = buyPrice;
  }

  /**
   * Deletes an order from the simulation (orderMap and allOrders).
   * 
   * @param o
   *          The order to be removed.
   * @param agentID
   *          ID of the agent whose order is to be removed.
   */
  public void cancelOrder(Order o) {
    allOrders.remove(o);
    orderMap.get(o.getCreatorID()).remove(o);
    // log the action.
    log(o, 3, false);
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
   * @param o
   *          New order to be inserted into the MatchingEngine.
   * @param agentID
   *          ID of the agent that initiated the order.
   */
  public boolean createOrder(Order o) {
    allOrders.add(o);
    if (orderMap.containsKey(o.getCreatorID())) {
      orderMap.get(o.getCreatorID()).add(o);
    } else {
      ArrayList<Order> orderList = new ArrayList<Order>();
      orderList.add(o);
      orderMap.put(o.getCreatorID(), orderList);
    }
    // log the action.
    // must then check if a trade can occur
    log(o, 1, false);
    return trade(o, willTrade(o));
  }

  // must account for the case when there are not enough orders to satisfy
  // a market order.
  public void tradeMarketOrder(Order o) {
    // have to add the order to the orderMap and all orders, otherwise the
    // trade method will not work.
    createOrder(o);
    long aggressorID = o.getCreatorID();
    int totalVolumeTraded = 0;
    int quantityToRid = o.getCurrentQuant();
    if (o.isBuyOrder()) {
      ArrayList<Order> topSells = topSellOrders();
      while (!topSells.isEmpty()) {
        int currentVolumeTraded = trade(o, topSells.get(0));
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
      while (!topBuys.isEmpty()) {
        int currentVolumeTraded = trade(o, topBuys.get(0));
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

      // log this trade

    }
  }

  // can use the code below to replace some code in checkMakeTrade()
  // we cannot use this to notify agents that a trade occurs because
  // tradeMarketOrder() is handled differently than checkMakeTrade().
  private int trade(Order o1, Order o2) {
    int volumeTraded;
    if (o1.getCurrentQuant() == o2.getCurrentQuant()) {
      volumeTraded = o1.getCurrentQuant();
      allOrders.remove(o1);
      allOrders.remove(o2);
      orderMap.get(o1.getCreatorID()).remove(o1);
      orderMap.get(o2.getCreatorID()).remove(o2);

    } else if (o1.getCurrentQuant() > o2.getCurrentQuant()) {
      // delete orderToTrade, decrease quantity of o
      volumeTraded = o1.getCurrentQuant() - o2.getCurrentQuant();
      o1.setQuant(volumeTraded);
      allOrders.remove(o2);
      orderMap.get(o2.getCreatorID()).remove(o2);
    } else {
      volumeTraded = o2.getCurrentQuant() - o1.getCurrentQuant();
      o2.setQuant(volumeTraded);
      allOrders.remove(o1);
      orderMap.get(o1.getCreatorID()).remove(o1);
    }
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
  public boolean modifyOrder(Order o, long newPrice, int newQuant) {
    o.setPrice(newPrice);
    o.setQuant(newQuant);
    // log the action
    log(o, 2, false);
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
        } else if (o.compare(o, topBuyOrders.get(9)) > 0) {
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
   * Performs the trade for non-market orders. If samePricedOrders is null, i.e.
   * the order will not make a trade, then routine simply exits.
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

    boolean aggressiveBuyer = order.isBuyOrder();

    if (startingPeriod) {
      cancelOrder(order);
    }

    lastTradePrice = samePricedOrders.get(0).getPrice();

    // now select the orders that were made first.
    Collections.sort(samePricedOrders, null);

    Order orderToTrade = samePricedOrders.get(0);

    int volumeTraded = trade(order, orderToTrade);

    // log the action and ID of trade, with System.currentTimeMillis()
    // and volume traded.
    // notify both agents that a trade has occurred.

    if (aggressiveBuyer) {
      lastAgVolumeBuySide += volumeTraded;
    } else {
      lastAgVolumeSellSide += volumeTraded;
    }
    // now get the agents (aggressor and non-aggressor) and notify them.
    agentMap.get(order.getCreatorID()).setLastOrderTraded(true, volumeTraded);
    agentMap.get(orderToTrade.getCreatorID()).setLastOrderTraded(true,
      volumeTraded);
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
  public long getBuyPrice() {
    return buyPrice;
  }

  /**
   * Resets lastAgVolumeBuySide and lastAgVolumeSellSide for the next
   * millisecond
   */
  public void reset() {
    lastAgVolumeBuySide = lastAgVolumeSellSide = 0;
  }

  /**
   * A special method for opportunistic traders that returns the moving average
   * for the last n number of milliseconds.
   */
  public void storeMovingAverage(int n) {
    long midpoint =
      (long) ((getBestBid().getPrice() + getBestAsk().getPrice()) / 2);
    if (midpoints.size() > n) {
      midpoints.poll();
    }
    midpoints.add(midpoint);
  }

  /**
   * Provides agents with the moving average with length of time specified by
   * the calculateMovingAverage() method.
   */
  public long getMovingAverage() {
    long sum = 0;
    for (Long mid : midpoints) {
      sum += mid;
    }
    return sum / midpoints.size();
  }

  /**
   * Logs all the required information into the orderbook and updates graph when
   * an order is created, modified, or deleted. The CSV to be logged will have
   * the following fields: Agent ID, Message Type (1 = new order, 2 =
   * modification , 3 = cancel, 105 = trade), Buy/Sell (1/2), Order ID, Original
   * Order Quantity, Price, Order Type (limit/market), Leaves Quantity.
   * 
   * @param o
   *          Order to log
   * @param code
   *          Message type
   * @param market
   *          True if logging a market order. False if logging a limit order
   */
  public void log(Order o, int code, boolean market) {

  }

  /**
   * Logs all the required information into the orderbook and updates graph if
   * trade occurs. In addition to the fields above, this method will also add
   * fields for Price of Trade, Quantity Filled, Aggressor Indicator (Y/N), and
   * Trade ID. Calls updateGraph() if needed.
   */
  public void logTrade(Order o) {

  }

  public void updateGraph() {

  }

}
