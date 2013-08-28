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
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT = 30;

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

  /**
   * The total profit of all intelligent agents in the simulation.
   */
  private static int totalProfit = 0;

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
  private static int delayLength = 500;

  /**
   * Helper class for all Intelligent Agents.
   */
  private static IntelligentAgentHelper intelligentAgentHelper;

  /**
   * The threshold state at time t = now - delay.
   */
  private static ThresholdState oldThresholdState =
    ThresholdState.BELOW_THRESHOLD;

  /**
   * List of order prices over time that were traded that may need to be covered
   * by the agent.
   */
  private final ArrayList<ArrayList<Integer>> potentialOrdersPricesToCover;

  /**
   * Holds the list of orders to cover at time t = now - delay. Used to keep
   * track of trades that happen before the agent has a chance to act at time t
   * = now.
   */
  private final ArrayList<Integer> orderBuffer;

  /**
   * Constructs an Intelligent Agent and initializes its order book so that
   * there is a single order with ORDER_SIZE quantity at each tick above and
   * below the buy price.
   * 
   * @param matchEng
   *          The MatchingEngine of the simulator.
   */
  public IntelligentAgent(MatchingEngine matchEng) {
    super(matchEng);

    potentialOrdersPricesToCover = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < delayLength; i++) {
      potentialOrdersPricesToCover.add(new ArrayList<Integer>());
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

  /**
   * See the wiki for details on the algorithm.
   */
  @Override
  void act() {
    // TODO Refactor this nightmare without breaking anything.
    ArrayList<Integer> interestedList =
      potentialOrdersPricesToCover.get(intelligentAgentHelper.getOldestIndex());
    Integer bestBidPriceToFill = intelligentAgentHelper.getOldBestBidPrice();
    Integer bestAskPriceToFill = intelligentAgentHelper.getOldBestAskPrice();
    Integer previousBestBidPrice =
      intelligentAgentHelper.getPreviousOldBestBidPrice();
    Integer previousBestAskPrice =
      intelligentAgentHelper.getPreviousOldBestAskPrice();

    // Assume all prices have to be ordered covered first.
    HashSet<Integer> pricesToOrder = new HashSet<Integer>();
    for (int i = 0; i < HALF_TICK_WIDTH; ++i) {
      pricesToOrder.add(bestBidPriceToFill - (i * TICK_SIZE));
      pricesToOrder.add(bestAskPriceToFill + (i * TICK_SIZE));
    }

    // Cancel all orders that exist (i.e. have not been traded) but have prices
    // that are no longer in the new best bid/ask interval.
    Integer currentPrice;
    for (int i = 0; i < HALF_TICK_WIDTH; ++i) {
      // Iterate through old interval, best bid and under
      currentPrice = previousBestBidPrice - (i * TICK_SIZE);
      if (!interestedList.contains(currentPrice)) {
        if (isInInterval(currentPrice, bestBidPriceToFill, bestAskPriceToFill)) {
          pricesToOrder.remove(currentPrice);
        } else {
          cancelOrder(currentPrice);
        }
      }

      // Iterate through old interval, best ask and above
      currentPrice = previousBestAskPrice + (i * TICK_SIZE);
      if (!interestedList.contains(currentPrice)) {
        if (isInInterval(currentPrice, bestBidPriceToFill, bestAskPriceToFill)) {
          pricesToOrder.remove(currentPrice);
        } else {
          cancelOrder(currentPrice);
        }
      }
    }

    // Deal with threshold. Update orders to cover accordingly.
    switch (oldThresholdState) {
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

    // Deal with inventory limit
    boolean[] inventoryResults =
      checkInventory(getInventory(), INVENTORY_LIMIT, overLimit);
    overLimit = inventoryResults[OVER_LIMIT];

    if (inventoryResults[OVERRIDE]) {
      ArrayList<Integer> pricesToRemove = new ArrayList<Integer>();
      if (inventoryResults[WILL_BUY]) {
        // Do not sell orders
        for (Integer i : pricesToOrder) {
          if (i >= bestAskPriceToFill) {
            pricesToRemove.add(i);
          }
        }
      } else {
        // Do not buy orders
        for (Integer i : pricesToOrder) {
          if (i <= bestBidPriceToFill) {
            pricesToRemove.add(i);
          }
        }
      }
      pricesToOrder.removeAll(pricesToRemove);
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
   *          1) Price of the order traded. 2) Time the order was traded, in
   *          milliseconds. 3) Volume traded (for profit logging). 4) True if
   *          buy order was traded.
   * 
   */
  @Override
  public void notify(Object... arguments) {
    long timeOfTrade = (Long) arguments[1];
    int priceOfOrderTraded = (Integer) arguments[0];
    int volumeTraded = (Integer) arguments[2];
    boolean buyOrderTraded = (Boolean) arguments[3];
    // Check if acted yet
    if (getNextActTime() <= timeOfTrade) {
      orderBuffer.add(priceOfOrderTraded);
    } else {
      potentialOrdersPricesToCover.get(intelligentAgentHelper.getOldestIndex())
        .add(priceOfOrderTraded);
    }
    // Update total profit
    totalProfit +=
      (buyOrderTraded ? -priceOfOrderTraded : priceOfOrderTraded)
        * volumeTraded;
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
    return (
    // Price within bid interval
    (priceToCheck > bestBidPrice - (HALF_TICK_WIDTH * TICK_SIZE) && priceToCheck <= bestBidPrice)
    // Price within ask interval
    || (priceToCheck >= bestAskPrice && priceToCheck < bestAskPrice
      + (HALF_TICK_WIDTH * TICK_SIZE)));
  }

  /**
   * Set the amount of time from the present that all Intelligent Agents should
   * look for market information, in milliseconds.
   * 
   * @param delayLength
   *          The delay time, in milliseconds.
   */
  public static void setDelay(int delayLength) {
    IntelligentAgent.delayLength = delayLength;
  }

  /**
   * Set the maximum difference between the total volume at the best bid/ask
   * allowed before additional actions are taken by all Intelligent Agents.
   * 
   * @param threshold
   *          The difference between the total volume at the best bid/ask
   *          allowed.
   */
  public static void setThreshold(int threshold) {
    IntelligentAgent.threshold = threshold;
  }

  /**
   * Set the helper class for Intelligent Agents.
   * 
   * @param iah
   *          The IntelligentAgentHelper class.
   */
  public static void setIntelligentAgentHelper(IntelligentAgentHelper iah) {
    intelligentAgentHelper = iah;
  }

  /**
   * Set the threshold state at time t = now - delayLength.
   * 
   * @param newState
   *          The new state at time t = now - delayLength.
   */
  public static void setOldThresholdState(ThresholdState newState) {
    oldThresholdState = newState;
  }

  public static int getTotalProfit() {
    return totalProfit;
  }

  public static void setTotalProfit(int newTotalProfit) {
    totalProfit = newTotalProfit;
  }

}
