/**
 * Holds implementation of Agent's acting procedures. All new agents should
 * inherit from this class since act() is not implemented, i.e. there is no
 * default act() action for an agent.
 */

public abstract class Agent {

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
   * Is true if an order in the last millisecond resulted in a trade with one of
   * the agent's order. False otherwise.
   */
  private boolean lastOrderTraded;

  /**
   * Creates an agent object with the ID as a time stamp in milliseconds. Must
   * pass in the MatchingEngine that is used for the simulation. Time is set to
   * a negative value so that the agent is not automatically picked to act. The
   * agent is also added automatically to the MatchingEngine's agentMap so that
   * it can keep track of the agent.
   * 
   * @param matchEng
   *          The MatchingEngine of the simulator.
   */
  public Agent(MatchingEngine matchEng) {
    this.id = System.currentTimeMillis();
    this.matchingEngine = matchEng;
    this.inventory = 0;
    this.nextActTime = -1;
    matchEng.addNewAgent(this.id, this);
  }

  /**
   * Method to be implemented by subclasses of Agent. Holds different trading
   * strategy code depending on the type of agent desired.
   */
  abstract void act();

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
   * @param If
   *          true, then the agent will act.
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
   * Calls the MatchingEngine to create a new order for the agent.
   * 
   * @param price
   *          Price of the order (should be in 0.25 increments only).
   * @param initialQuant
   *          The quantity that the agent wants to buy/sell.
   * @param buyOrder
   *          True if the agent wants to buy. False otherwise.
   * 
   * @return True if the creation of the order resulted in a trade
   */
  public boolean createNewOrder(long price, int initialQuant, boolean buyOrder) {
    return matchingEngine.createOrder(new Order(this.id, price, initialQuant,
      buyOrder), false);
  }

  // assuming that market orders will always have a price of 0.
  // marketOrders should be traded immediately.
  public void createMarketOrder(int initialQuant, boolean buyOrder) {
    matchingEngine.tradeMarketOrder(new Order(this.id, 0, initialQuant,
      buyOrder));
  }

  /**
   * Calls the MatchingEngine to modify an order
   * 
   * @param order
   *          The order to be modified.
   * @param newPrice
   *          The new price to set the order to.
   * @param newQuant
   *          The new quantity to set the order to.
   * 
   * @return True if the modification of this order resulted in a trade.
   */
  public boolean modifyOrder(Order order, long newPrice, int newQuant) {
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
    lastOrderTraded = traded;
    inventory += volume;
  }

  public long getBuyPrice() {
    return matchingEngine.getBuyPrice();
  }

  /**
   * @return Midpoint between the best ask price and best bid price
   */
  public long getMidPoint() {
    return (long) (matchingEngine.getBestBid().getPrice() + matchingEngine
      .getBestAsk().getPrice()) / 2;
  }

  /**
   * @return The moving average calculated by the matching engine. See the
   *         getMovingAverage() method in the MatchingEngine class for more
   *         details.
   */
  public long getMovAvg() {
    return matchingEngine.getMovingAverage();
  }

}