package edu.virginia.jinsup;

import java.util.ArrayList;
import java.util.HashSet;

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
    Integer bestBidPriceToFill = intelligentAgentHelper.getOldBestBidPrice();
    Integer bestAskPriceToFill = intelligentAgentHelper.getOldBestAskPrice();
    Integer previousBestBidPrice =
      intelligentAgentHelper.getPreviousOldBestBidPrice();
    Integer previousBestAskPrice =
      intelligentAgentHelper.getPreviousOldBestAskPrice();

    HashSet<Integer> pricesToOrder = new HashSet<Integer>();
    for (int i = 0; i < HALF_TICK_WIDTH; ++i) {
      pricesToOrder.add(bestBidPriceToFill - (i * TICK_SIZE));
      pricesToOrder.add(bestAskPriceToFill + (i * TICK_SIZE));
    }

    // Iterate through old interval, best bid and under
    for (int i = 0; i < HALF_TICK_WIDTH; ++i) {
      Integer currentPrice = previousBestBidPrice - (i * TICK_SIZE);
      if (!interestedList.contains(currentPrice)) {
        if (isInInterval(currentPrice, bestBidPriceToFill, bestAskPriceToFill)) {
          pricesToOrder.remove(currentPrice);
        } else {
          cancelOrder(currentPrice);
        }
      }
    }

    // Iterate through old interval, best ask and above
    for (int i = 0; i < HALF_TICK_WIDTH; ++i) {
      Integer currentPrice = previousBestAskPrice + (i * TICK_SIZE);
      if (!interestedList.contains(currentPrice)) {
        if (isInInterval(currentPrice, bestBidPriceToFill, bestAskPriceToFill)) {
          pricesToOrder.remove(currentPrice);
        } else {
          cancelOrder(currentPrice);
        }
      }
    }

    switch (currentThresholdState) {
      case BELOW_THRESHOLD:
        // Make sure best bid/ask are covered... they should be by default
        // TODO Remove this case
        break;

      case BUY_ORDER_SURPLUS:
        // Cancel best ask if it exists.
        pricesToOrder.remove(bestAskPriceToFill);
        if (isInInterval(bestAskPriceToFill, previousBestBidPrice,
          previousBestAskPrice) && !interestedList.contains(bestAskPriceToFill)) {
          cancelOrder(bestAskPriceToFill);
        }

        // Make sure best bid is still covered... should be covered already
        break;
      case SELL_ORDER_SURPLUS:
        // Cancel best bid if it exists.
        pricesToOrder.remove(bestBidPriceToFill);
        if (isInInterval(bestBidPriceToFill, previousBestBidPrice,
          previousBestAskPrice) && !interestedList.contains(bestBidPriceToFill)) {
          cancelOrder(bestBidPriceToFill);
        }

        // Make sure best ask is still covered... should be covered already
        break;
      default:
        System.err.println("Error: Invalid threshold state...exiting.");
        System.exit(1);
    }

    // Fill all remaining orders
    for (Integer i : pricesToOrder) {
      createNewOrder(i, ORDER_SIZE, i <= bestBidPriceToFill);
    }

    // Load buffer to the main array and clear it.
    pricesToOrder.clear();
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

  /**
   * Checks if the price is in the interval (bestBidPrice - HALF_TICK_WIDTH *
   * TICK_SIZE , bestBidPrice] or [bestAskPrice, bestAskPrice + HALF_TICK_WIDTH
   * * TICK_SIZE).
   * 
   * @param priceToCheck
   *          The price to check.
   * @param bestBidPrice
   *          Best bid price.
   * @param bestAskPrice
   *          Best ask price.
   * @return True if priceToCheck is in the interval. False otherwise.
   */
  public boolean isInInterval(int priceToCheck, int bestBidPrice,
    int bestAskPrice) {
    if (priceToCheck > bestBidPrice - (HALF_TICK_WIDTH * TICK_SIZE)
      && priceToCheck <= bestBidPrice) {
      return true;
    }

    if (priceToCheck >= bestAskPrice
      && priceToCheck < bestAskPrice + (HALF_TICK_WIDTH * TICK_SIZE)) {
      return true;
    }
    return false;
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
