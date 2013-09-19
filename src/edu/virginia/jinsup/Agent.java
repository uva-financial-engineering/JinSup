package edu.virginia.jinsup;

/**
 * Holds implementation of a generic agent's acting procedures. All new agents
 * should inherit from this class since act() is not implemented, i.e. there is
 * no default act() action for an agent. Instead, any new agent that is created
 * will need to have its own unique implementation of the act() method.
 */

public abstract class Agent {

  /**
   * Constant used to index the results of the checkInventory() method.
   * Indicates which side of the order book the agent should clear.
   */
  protected static final int WILL_BUY = 0;

  /**
   * Constant used to index the results of the checkInventory() method.
   * Indicates if an agent has broken the inventory limit.
   */
  protected static final int OVER_LIMIT = 1;

  /**
   * Constant used to index the results of the checkInventory() method.
   * Indicates if an agent should take further action after breaking the limit.
   */
  protected static final int OVERRIDE = 2;

  /**
   * The ID that should be assigned to the next agent created.
   */
  private static long nextAgentID = 0;

  /**
   * ID of the agent. Used by the Order class to track agents.
   */
  private final long id;
  /**
   * Matching engine that is used in the simulation. Should not be changed.
   */
  private final MatchingEngine matchingEngine;
  /**
   * Next time that the agent can act. This is determined in the act() method.
   */
  private long nextActTime;

  /**
   * Is true if the agent is chosen to act by the Controller. Will remain true
   * if the agent still wants to act during a turn.
   */
  private boolean willAct;
  /**
   * Number of shares bought or sold. If the agent has sold more than it has
   * bought, then this number will be negative.
   */
  private int inventory;

  /**
   * Minimum price interval between orders.
   */
  protected static final int TICK_SIZE = 25;

  /**
   * The next time the agent should place an order.
   */
  private long nextOrderTime;

  /**
   * The next time the agent should cancel an order.
   */
  private long nextCancelTime;

  /**
   * Enum holding the different types of action an agent can take.
   */
  public enum Action {
    NULL, ORDER, CANCEL;
  }

  /**
   * The next action the agent should take.
   */
  private Action nextAction;

  /**
   * Must pass in the MatchingEngine that is used for the simulation. Time is
   * set to a negative value so that the agent is not automatically picked to
   * act. The agent is also added automatically to the MatchingEngine's agentMap
   * so that it can keep track of the agent.
   * 
   * @param matchEng
   *          The MatchingEngine of the simulator.
   */
  public Agent(MatchingEngine matchEng) {
    this.id = nextAgentID;
    nextAgentID++;
    this.matchingEngine = matchEng;
    this.inventory = 0;
    this.nextActTime = -1;
    this.nextOrderTime = -1;
    this.nextCancelTime = -1;
    this.nextAction = Action.NULL;
    matchEng.addNewAgent(this.id, this);
  }

  /**
   * Method to be implemented by subclasses of Agent. Holds different trading
   * strategy code depending on the type of agent desired.
   */
  abstract void act();

  /**
   * Method used by some classes to allow messages to be passed from
   * MatchingEngine to agent. Should be overridden.
   * 
   * @param arguments
   *          The list of required arguments.
   */
  public void notify(Object... arguments) {
    return;
  }

  /**
   * @return The next time the agent needs to act. This is determined by the
   *         act() method.
   */
  public long getNextActTime() {
    return nextActTime;
  }

  public void setNextActTime(long nextTime) {
    nextActTime = nextTime;
  }

  /**
   * @return True if the agent still needs to act during its turn. False if it
   *         is done for its turn.
   */
  public boolean getWillAct() {
    return willAct;
  }

  /**
   * Enable the agent to act during its time slot. This is set by the controller
   * when it chooses agents to act for certain time slots.
   * 
   * @param act
   *          If true, then the agent will act.
   * 
   */
  public void setWillAct(boolean act) {
    willAct = act;
  }

  /**
   * Calls the MatchingEngine to cancel the agent's order.
   * 
   * @param order
   *          The order that needs to be cancelled.
   * 
   */
  public void cancelOrder(Order order) {
    matchingEngine.cancelOrder(order);
  }

  /**
   * Calls the MatchingEngine to cancel the all of the agent's orders at a
   * certain price.
   * 
   * @param price
   *          The price to wipe all orders out.
   */
  public void cancelOrder(int price) {
    matchingEngine.cancelOrder(this.id, price);
  }

  /**
   * Calls the MatchingEngine to create a new order for the agent.
   * 
   * @param price
   *          Price (CENTS) of the order (should be in 0.25 increments only).
   * @param initialQuant
   *          The quantity that the agent wants to buy/sell.
   * @param buyOrder
   *          True if the agent wants to buy. False otherwise.
   * 
   * @return True if the creation of the order resulted in a trade
   */
  public boolean createNewOrder(int price, int initialQuant, boolean buyOrder) {
    return matchingEngine.createOrder(new Order(this.id, price, initialQuant,
      buyOrder, false));
  }

  /**
   * A special method that is only used for creation of market orders. Since
   * they do not have a price, we set the price value to zero. Market orders
   * should be traded immediately.
   * 
   * @param initialQuant
   *          The quantity that the buy market order is for.
   * @param buyOrder
   *          True if this is a buy market order. False if this is a sell market
   *          order.
   */
  public void createMarketOrder(int initialQuant, boolean buyOrder) {
    matchingEngine.tradeMarketOrder(this.id, initialQuant, buyOrder);
  }

  /**
   * Calls the MatchingEngine to modify an order.
   * 
   * @param order
   *          The order to be modified.
   * @param newPrice
   *          The new price (CENTS) to set the order to.
   * @param newQuant
   *          The new quantity to set the order to.
   * 
   * @return True if the modification of this order resulted in a trade.
   */
  public boolean modifyOrder(Order order, int newPrice, int newQuant) {
    return matchingEngine.modifyOrder(order, newPrice, newQuant);
  }

  /**
   * Informs the agent whether an order resulted in a trade or not. This is set
   * from the MatchingEngine.
   * 
   * @param traded
   *          True if a trade happened. False otherwise.
   * @param volume
   *          Volume that was traded
   */
  public void setLastOrderTraded(boolean traded, int volume) {
    // lastOrderTraded = traded;
    inventory += volume;
  }

  /**
   * @return Midpoint between the best ask price and best bid price
   */
  public int getMidPoint() {
    return (matchingEngine.getBestBid().getPrice() + matchingEngine
      .getBestAsk().getPrice()) / 2;
  }

  /**
   * @return The next time the agent will issue an order.
   */
  protected long getNextOrderTime() {
    return nextOrderTime;
  }

  /**
   * @return The next time the agent will cancel an order.
   */
  protected long getNextCancelTime() {
    return nextCancelTime;
  }

  /**
   * Sets the agent's next order time. Should be overwritten depending on the
   * behavior of the agent.
   * 
   * @param nextOrderTime
   *          The next time the agent should issue an order.
   */
  protected void setNextOrderTime(long nextOrderTime) {
    this.nextOrderTime = nextOrderTime;
  }

  /**
   * Sets the agent's next cancel time. Should be overwritten depending on the
   * behavior of the agent.
   * 
   * @param nextCancelTime
   *          The next time the agent should cancel an order.
   */
  protected void setNextCancelTime(long nextCancelTime) {
    this.nextCancelTime = nextCancelTime;
  }

  /**
   * @return The next action the agent will perform.
   */
  public Action getNextAction() {
    return nextAction;
  }

  /**
   * @param nextAction
   *          The next action the agent will perform.
   */
  public void setNextAction(Action nextAction) {
    this.nextAction = nextAction;
  }

  /**
   * @return Returns a random order from the agent's order book.
   */
  public Order getRandomOrder() {
    return matchingEngine.getRandomOrder(this.id);
  }

  /**
   * @return The last price that a trade was made at.
   */
  public int getLastTradePrice() {
    return matchingEngine.getLastTradePrice();
  }

  /**
   * @return The highest price an agent is willing to buy.
   */
  public int getBestBuyPrice() {
    if (matchingEngine.isStartingPeriod()) {
      return Settings.getBuyPrice() - TICK_SIZE;
    }
    return matchingEngine.getBestBid().getPrice();
  }

  /**
   * @return The lowest price an agent is willing to sell.
   */
  public int getBestSellPrice() {
    if (matchingEngine.isStartingPeriod()) {
      return Settings.getBuyPrice() + TICK_SIZE;
    }
    return matchingEngine.getBestAsk().getPrice();
  }

  /**
   * @return The number of shares an agent has bought/sold.
   */
  public int getInventory() {
    return inventory;
  }

  /**
   * Cancels all sell orders in the agent's order book.
   */
  public void cancelAllSellOrders() {
    matchingEngine.cancelAllSellOrders(id);
  }

  /**
   * Cancels all buy orders in the agent's order book.
   */
  public void cancelAllBuyOrders() {
    matchingEngine.cancelAllBuyOrders(id);
  }

  /**
   * Gets the startup time in milliseconds from the matchingEngine.
   * 
   * @return The startup period of the simulation, in milliseconds.
   */
  public long getStartupTime() {
    return Settings.getStartTime();
  }

  /**
   * @return The sum of quantities of all orders at the best bid price.
   */
  public int getBestBidQuantity() {
    return matchingEngine.getBestBidQuantity();
  }

  /**
   * @return The sum of quantities of all orders at the best ask price.
   */
  public int getBestAskQuantity() {
    return matchingEngine.getBestAskQuantity();
  }

  /**
   * @return The agent's oldest order.
   */
  public Order getOldestOrder() {
    return matchingEngine.getOldestOrder(id);
  }

  /**
   * @return True if the agent has orders; false otherwise.
   */
  public boolean agentHasOrders() {
    return matchingEngine.agentHasOrders(id);
  }

  /**
   * @return The ID of the agent.
   */
  public long getID() {
    return id;
  }

  /**
   * Clears one side of the order book depending on whether which inventory
   * limit the agent has broke.
   * 
   * @param override
   *          True if the inventory limit was broken.
   * @param willBuy
   *          True if the negative limit was broken (too many shares sold).
   */
  protected void processInventory(boolean override, boolean willBuy) {
    if (override) {
      if (willBuy) {
        cancelAllSellOrders();
      } else {
        cancelAllBuyOrders();
      }
    }
  }

  /**
   * @param currentInventory
   *          The number of shares the agent has at the moment.
   * @param inventoryLimit
   *          The inventory limit of the agent.
   * @param previousOverLimitState
   *          True if the agent broke the limit on the last occasion of
   *          inventory checking.
   * @return An array of boolean values corresponding to whether the agent has
   *         broken the inventory limit, which side of the order book the agent
   *         should clear if it did break the limit, and whether the agent
   *         should perform extra tasks (override) as a result of crossing the
   *         limit. The array should be accessed using the constants OVER_LIMIT,
   *         OVER_LIMIT, and OVERRIDE respectively.
   */
  protected boolean[] checkInventory(int currentInventory, int inventoryLimit,
    boolean previousOverLimitState) {
    boolean[] results = {true, true, true};
    if (currentInventory > inventoryLimit) {
      results[OVER_LIMIT] = true;
      results[WILL_BUY] = false;
    } else if (currentInventory < -inventoryLimit) {
      results[OVER_LIMIT] = true;
      results[WILL_BUY] = true;
    } else if (currentInventory > inventoryLimit / 2 && previousOverLimitState) {
      results[WILL_BUY] = false;
    } else if (currentInventory < -inventoryLimit / 2 && previousOverLimitState) {
      results[WILL_BUY] = true;
    } else if (Math.abs(currentInventory) <= inventoryLimit / 2) {
      results[OVER_LIMIT] = false;
      results[OVERRIDE] = false;
    } else if (Math.abs(currentInventory) <= inventoryLimit
      && !previousOverLimitState) {
      results[OVERRIDE] = false;
      results[OVER_LIMIT] = false;
    } else {
      System.err.println("Error with inventory checking.");
      System.exit(1);
    }
    return results;
  }
}
