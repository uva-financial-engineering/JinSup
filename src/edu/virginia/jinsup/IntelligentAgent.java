package edu.virginia.jinsup;

import java.util.ArrayList;

import edu.virginia.jinsup.IntelligentAgentHelper.ThresholdState;

/**
 * Agent that maintains an order book with a width of 2 * HALF_TICK_WIDTH. The
 * agent acts on a delay and its actions depend on the difference between total
 * volume at the best bid/ask. See wiki for more details.
 */
public class IntelligentAgent extends Agent {

  /**
   * How often the agent should act, in milliseconds.
   */
  private static final int INTERVAL = 1;

  /**
   * Half of the size of the order book the agent maintains.
   */
  private static final int HALF_TICK_WIDTH = 10;

  /**
   * The quantity of each order the agent orders.
   */
  private static final int ORDER_SIZE = 1;

  /**
   * Maximum difference between the total volume at the best bid/ask allowed
   * before additional actions are taken.
   */
  private static int threshold = 200;

  /**
   * How long in the past the agent should look for market information, in
   * milliseconds.
   */
  private static int delay = 500;

  private static IntelligentAgentHelper intelligentAgentHelper;

  private static ThresholdState currentThresholdState =
    ThresholdState.BELOW_THRESHOLD;

  private static int previousTradePrice = 0;

  private static int currentTradePrice = 0;

  /**
   * List of order prices over time that were traded that may need to be covered
   * by the agent.
   */
  private final ArrayList<ArrayList<Integer>> potentialOrdersToCover;

  /**
   * Holds the list of orders to cover at time t = now - delay.
   */
  private ArrayList<Integer> orderBuffer;

  /**
   * Constructs an Intelligent Agent and initializes its order book so that
   * there is a single order with ORDER_SIZE quantity at each tick above and
   * below the buy price.
   * 
   * @param matchEng
   *          The MatchingEngine of the simulator.
   * @param delay
   *          How long in the past the agent should look for market information.
   * @param threshold
   *          Maximum difference between the total volume at the best bid/ask
   *          allowed before additional actions are taken.
   */
  public IntelligentAgent(MatchingEngine matchEng) {
    super(matchEng);

    potentialOrdersToCover = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < delay; i++) {
      potentialOrdersToCover.add(new ArrayList<Integer>());
    }

    orderBuffer = new ArrayList<Integer>();

    for (int i = 0; i < HALF_TICK_WIDTH; i++) {
      createNewOrder(matchEng.getBuyPrice() - ((i + 1) * TICK_SIZE),
        ORDER_SIZE, true);
      createNewOrder(matchEng.getBuyPrice() + ((i + 1) * TICK_SIZE),
        ORDER_SIZE, false);

      // Do not act until the market opens.
      setNextActTime(matchEng.getStartupTime());
    }
  }

  @Override
  void act() {
    // Deal with orders at the best bid/ask
    // TODO Refactor this nightmare without breaking anything.
    ArrayList<Integer> interestedList =
      potentialOrdersToCover.get(intelligentAgentHelper.getOldestIndex());
    int oldTradePrice = intelligentAgentHelper.getOldTradePriceData();
    Integer bestBidPrice = oldTradePrice - TICK_SIZE;
    Integer bestAskPrice = oldTradePrice + TICK_SIZE;
    Integer buyEdgePrice = oldTradePrice - (HALF_TICK_WIDTH * TICK_SIZE);
    Integer sellEdgePrice = oldTradePrice + (HALF_TICK_WIDTH * TICK_SIZE);

    switch (currentThresholdState) {
      case BELOW_THRESHOLD:
        // Make sure best bid/ask are covered.
        if (interestedList.remove(bestBidPrice)) {
          createNewOrder(bestBidPrice, ORDER_SIZE, true);
        }

        if (interestedList.remove(bestAskPrice)) {
          createNewOrder(bestAskPrice, ORDER_SIZE, false);
        }

        break;
      case BUY_ORDER_SURPLUS:
        // Cancel best ask if it exists.
        if (!interestedList.remove(bestAskPrice)) {
          cancelOrder(bestAskPrice);
        }

        // Make sure best bid is still covered.
        if (interestedList.remove(bestBidPrice)) {
          createNewOrder(bestBidPrice, ORDER_SIZE, true);
        }
        break;
      case SELL_ORDER_SURPLUS:
        // Cancel best bid if it exists.
        if (!interestedList.remove(bestBidPrice)) {
          cancelOrder(bestBidPrice);
        }

        // Make sure best ask is still covered.
        if (interestedList.remove(bestAskPrice)) {
          createNewOrder(bestAskPrice, ORDER_SIZE, false);
        }
        break;
      default:
        System.err.println("Error: Invalid threshold state...exiting.");
        System.exit(1);
    }

    // Deal with orders at the edge
    int currentTradePriceDifference =
      intelligentAgentHelper.getTradePriceDifference() / TICK_SIZE;
    Integer innerLoopPrice;
    if (currentTradePriceDifference == 0) {
      // Make sure edges are filled.
      if (interestedList.remove(sellEdgePrice)) {
        createNewOrder(sellEdgePrice, ORDER_SIZE, false);
      }
      if (interestedList.remove(buyEdgePrice)) {
        createNewOrder(buyEdgePrice, ORDER_SIZE, true);
      }
    } else if (currentTradePriceDifference > 0) {
      // Price decreased, create more buy orders and remove sell orders.
      int startOrderIndex = (interestedList.remove(buyEdgePrice)) ? 0 : 1;
      int startCancelIndex = (!interestedList.remove(sellEdgePrice)) ? 0 : 1;
      for (int i = startOrderIndex; i <= currentTradePriceDifference; i++) {
        createNewOrder(buyEdgePrice - (i * TICK_SIZE), ORDER_SIZE, true);
      }
      for (int i = startCancelIndex; i <= currentTradePriceDifference; i++) {
        innerLoopPrice = sellEdgePrice + (i * TICK_SIZE);
        // Prevent null pointer exceptions
        if (!interestedList.remove(innerLoopPrice)) {
          cancelOrder(innerLoopPrice);
        }
      }
    } else {
      // Price increased, create more sell orders and remove buy orders.
      int startOrderIndex = (interestedList.remove(sellEdgePrice) ? 0 : 1);
      int startCancelIndex = (!interestedList.remove(buyEdgePrice) ? 0 : 1);
      for (int i = startOrderIndex; i <= Math.abs(currentTradePriceDifference); i++) {
        createNewOrder(sellEdgePrice + (i * TICK_SIZE), ORDER_SIZE, false);
      }
      for (int i = startCancelIndex; i <= Math.abs(currentTradePriceDifference); i++) {
        innerLoopPrice = buyEdgePrice - (i * TICK_SIZE);
        // Prevent null pointer exceptions
        if (!interestedList.remove(innerLoopPrice)) {
          cancelOrder(innerLoopPrice);
        }
      }
    }

    // Deal with all other orders
    for (Integer i : interestedList) {
      createNewOrder(i, ORDER_SIZE, i < oldTradePrice);
    }

    // Load buffer to the main array and clear it.
    interestedList.clear();
    interestedList.addAll(orderBuffer);
    orderBuffer.clear();

    setNextActTime(getNextActTime() + INTERVAL);
    setWillAct(false);
  }

  /**
   * Notifies the agent that one of its orders has been traded.
   * 
   * @param arguments
   *          The first argument shall be the price of the order traded. The
   *          second argument shall be the time the order was traded.
   * 
   */
  @Override
  public void notify(Object... arguments) {
    long timeOfTrade = (Long) arguments[1];
    int priceOfOrderTraded = (Integer) arguments[0];
    // Check if acted yet
    if (getNextActTime() <= timeOfTrade) {
      orderBuffer.add(priceOfOrderTraded);
    } else {
      potentialOrdersToCover.get(intelligentAgentHelper.getOldestIndex()).add(
        priceOfOrderTraded);
    }
  }

  public static int getDelay() {
    return delay;
  }

  public static void setDelay(int delay) {
    IntelligentAgent.delay = delay;
  }

  public static int getThreshold() {
    return threshold;
  }

  public static void setThreshold(int threshold) {
    IntelligentAgent.threshold = threshold;
  }

  public static void setIntelligentAgentHelper(IntelligentAgentHelper iah) {
    intelligentAgentHelper = iah;
  }

  public static void updateThresholdState(ThresholdState newState) {
    currentThresholdState = newState;
  }

}
