import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
   * Creates a matching engine with empty fields. Everything is initialized to
   * zero.
   */
  public MatchingEngine() {
    orderMap = new HashMap<Long, ArrayList<Order>>();
    allOrders = new ArrayList<Order>();
    agentMap = new HashMap<Long, Agent>();
    lastAgVolumeBuySide = 0;
    lastAgVolumeSellSide = 0;
    lastTradePrice = 0;
  }

  /**
   * Deletes an order from the simulation (orderMap and allOrders).
   * 
   * @param o
   *          The order to be removed.
   * @param agentID
   *          ID of the agent whose order is to be removed.
   */
  public void cancelOrder(Order o, long agentID) {
    allOrders.remove(o);
    orderMap.get(agentID).remove(o);
    // log the action.
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
  public void createOrder(Order o, long agentID) {
    allOrders.add(o);
    if (orderMap.containsKey(agentID)) {
      orderMap.get(agentID).add(o);
    } else {
      ArrayList<Order> orderList = new ArrayList<Order>();
      orderList.add(o);
      orderMap.put(agentID, orderList);
    }
    // log the action.
    // must then check if a trade can occur
    checkMakeTrade(o);
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
  public void modifyOrder(Order o, long newPrice, int newQuant) {
    o.setPrice(newPrice);
    o.setQuant(newQuant);
    // log the action
    // must then check if a trade can occur
    checkMakeTrade(o);
  }

  /**
   * @return The last price that a share was traded for. Used primarily as data
   *         for the bar chart.
   */
  public double getLastTradePrice() {
    return lastTradePrice;
  }

  // should verify again that this method should get the top ten, and
  // not the most recent orders in the orderbook.

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
          Collections.sort(topBuyOrders, null);
        } else {
          if (o.compare(o, topBuyOrders.get(9)) > 0) {
            topBuyOrders.set(9, o);
            Collections.sort(topBuyOrders, null);
          }
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
        } else {
          if (a.compare(a, (topSellOrders.get(9))) < 0) {
            topSellOrders.set(9, a);
            Collections.sort(topSellOrders, Order.lowestFirstComparator);
          }
        }
      }
    }
    return topSellOrders;
  }

  // method to check for and make trades
  public void checkMakeTrade(Order order) {
    boolean aggressiveBuyer = true;
    ArrayList<Order> samePrice = new ArrayList<Order>();
    if (order.isBuyOrder()) {
      // check for sell orders at the same sell price
      // must be sure to pick orders that were placed first.
      // TODO: potential error due to double precision
      for (Order o : allOrders) {
        if (!o.isBuyOrder() && o.getPrice() == order.getPrice()) {
          samePrice.add(o);
        }
      }
    } else {
      for (Order o : allOrders) {
        if (o.isBuyOrder() && o.getPrice() == order.getPrice()) {
          samePrice.add(o);
          aggressiveBuyer = false;
        }
      }
    }

    // trade was not made
    if (samePrice.isEmpty()) {
      return;
    }

    lastTradePrice = samePrice.get(0).getPrice();

    // now select the orders that were made first.
    Collections.sort(samePrice, null);

    Order orderToTrade = samePrice.get(0);

    int volumeTraded = 0;

    if (order.getCurrentQuant() == orderToTrade.getCurrentQuant()) {
      volumeTraded = order.getCurrentQuant();
      allOrders.remove(order);
      allOrders.remove(orderToTrade);
      orderMap.get(order.getCreatorID()).remove(order);
      orderMap.get(orderToTrade.getCreatorID()).remove(orderToTrade);

    } else if (order.getCurrentQuant() > orderToTrade.getCurrentQuant()) {
      // delete orderToTrade, decrease quantity of o
      volumeTraded = order.getCurrentQuant() - orderToTrade.getCurrentQuant();
      order.setQuant(volumeTraded);
      allOrders.remove(orderToTrade);
      orderMap.get(orderToTrade.getCreatorID()).remove(orderToTrade);
    } else {
      volumeTraded = orderToTrade.getCurrentQuant() - order.getCurrentQuant();
      orderToTrade.setQuant(volumeTraded);
      allOrders.remove(order);
      orderMap.get(order.getCreatorID()).remove(order);
    }
    // log the action and ID of trade, with System.currentTimeMillis()
    // and volume traded.
    // notify both agents that a trade has occurred.

    if (aggressiveBuyer) {
      lastAgVolumeBuySide += 1;
    } else {
      lastAgVolumeSellSide += 1;
    }
    // now get the agents and notify them.
    Agent aggressor = agentMap.get(order.getCreatorID());
    Agent nonAgress = agentMap.get(orderToTrade.getCreatorID());
    aggressor.setLastOrderTraded(true, volumeTraded);
    nonAgress.setLastOrderTraded(true, volumeTraded);
  }

  public Order getBestBid() {
    ArrayList<Order> bids = new ArrayList<Order>();
    for (Order o : allOrders) {
      if (o.isBuyOrder()) {
        bids.add(o);
      }
    }
    Collections.sort(bids, null);
    // want the highest bid price
    return bids.get(0);
  }

  public Order getBestAsk() {
    ArrayList<Order> asks = new ArrayList<Order>();
    for (Order o : allOrders) {
      if (!o.isBuyOrder()) {
        asks.add(o);
      }
    }
    Collections.sort(asks, null);
    // want the lowest asking price
    return asks.get(asks.size() - 1);
  }

  // the following code is for testing purposes only!
  public ArrayList<Order> getAllOrders() {
    return allOrders;
  }

}
