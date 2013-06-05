package edu.virginia.jinsup;

/**
 * Holds implementation of a generic agent's acting procedures. All new agents
 * should inherit from this class since act() is not implemented, i.e. there is
 * no default act() action for an agent. Instead, any new agent that is created
 * will need to have its own unique implementation of the act() method.
 */

public abstract class Agent implements Comparable<Agent> {

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
   * Is true if an order in the last millisecond resulted in a trade with one of
   * the agent's orders. False otherwise.
   */
  private boolean lastOrderTraded;

  private long nextOrderTime;

  private long nextCancelTime;

  public enum Action {
    NULL, ORDER, CANCEL;
  }

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
    matchingEngine.cancelOrder(order, false);
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
      buyOrder), false);
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
    matchingEngine.tradeMarketOrder(new Order(this.id, 0, initialQuant,
      buyOrder));
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
    lastOrderTraded = traded;
    inventory += volume;
  }

  /**
   * @return The buy price (CENTS) that was specified by the user.
   */
  public int getBuyPrice() {
    return matchingEngine.getBuyPrice();
  }

  /**
   * @return Midpoint between the best ask price and best bid price
   */
  public int getMidPoint() {
    return (matchingEngine.getBestBid().getPrice() + matchingEngine
      .getBestAsk().getPrice()) / 2;
  }

  /**
   * @return The moving average calculated by the matching engine. See the
   *         getMovingAverage() method in the MatchingEngine class for more
   *         details.
   */
  public int getMovAvg() {
    return matchingEngine.getMovingAverage();
  }

  /**
   * @return The volume of aggressive shares bought in the last millisecond of
   *         trading.
   */
  public int getLastAgVolumeBuySide() {
    return matchingEngine.getLastAgVolumeBuySide();
  }

  /**
   * @return The volume of aggressive shares bought in the last millisecond of
   *         trading.
   */
  public int getLastAgVolumeSellSide() {
    return matchingEngine.getLastAgVolumeSellSide();
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
    return matchingEngine.getBestBid().getPrice();
  }

  /**
   * @return The lowest price an agent is willing to sell.
   */
  public int getBestSellPrice() {
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
  public int getStartupTime() {
    return matchingEngine.getStartupTime();
  }

  @Override
  public int compareTo(Agent a) {
    return getNextActTime() > a.getNextActTime() ? 1 : getNextActTime() < a
      .getNextActTime() ? -1 : 0;
  }
}
