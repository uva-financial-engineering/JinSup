package edu.virginia.jinsup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
   * Different inventory states the intelligent agent can be in.
   */
  public enum InventoryState {
    BALANCED, SHARE_SURPLUS, SHARE_DEFICIT
  };

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

  /**
   * The total profit this Intelligent Agent.
   */
  private int profit;

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
   * Helper class for all Intelligent Agents.
   */
  private IntelligentAgentHelper intelligentAgentHelper;

  /**
   * The threshold state at time t = now - delay.
   */
  private ThresholdState oldThresholdState = ThresholdState.BELOW_THRESHOLD;

  /**
   * List of order prices over time that were traded that may need to be covered
   * by the agent. No duplicates allowed. This means that this is only valid for
   * ORDER_SIZE = 1.
   */
  private final ArrayList<HashSet<Integer>> potentialOrdersPricesToCover;

  /**
   * Holds the list of orders to cover at time t = now - delay. Used to keep
   * track of trades that happen before the agent has a chance to act at time t
   * = now.
   * 
   * No duplicates allowed. This means that this is only valid for ORDER_SIZE =
   * 1.
   */
  private final Set<Integer> orderBuffer;

  /**
   * Set that holds the current order book (prices) of the intelligent agent.
   */
  private final Set<Integer> currentOrderBook;

  /**
   * How long the intelligent agent should wait before reacting to market
   * conditions.
   */
  private final int delayLength;

  /**
   * History of inventory states the intelligent agent has been in.
   */
  private final ArrayList<InventoryState> inventoryLimitHistory;

  /**
   * Holds the index to access the inventoryLimitHistory to get the inventory
   * state at time t = now - delay.
   */
  private int inventoryLimitIndex;

  /**
   * The inventory state of the agent at time t = now - delay - 1.
   */
  private InventoryState previousInventoryState = InventoryState.BALANCED;

  /**
   * Constructs an Intelligent Agent and initializes its order book so that
   * there is a single order with ORDER_SIZE quantity at each tick above and
   * below the buy price.
   * 
   * @param matchEng
   *          The MatchingEngine of the simulator.
   */
  public IntelligentAgent(MatchingEngine matchEng, IntelligentAgentHelper iah,
    int delayLength) {
    super(matchEng);
    this.profit = 0;
    this.intelligentAgentHelper = iah;
    this.delayLength = delayLength;
    potentialOrdersPricesToCover = new ArrayList<HashSet<Integer>>();
    for (int i = 0; i < delayLength; i++) {
      potentialOrdersPricesToCover.add(new HashSet<Integer>());
    }

    inventoryLimitHistory =
      new ArrayList<InventoryState>(Collections.nCopies(delayLength,
        InventoryState.BALANCED));
    inventoryLimitIndex = 0;

    orderBuffer = new HashSet<Integer>();
    currentOrderBook = new HashSet<Integer>();

    for (int i = 0; i < HALF_TICK_WIDTH; i++) {
      createNewOrder(Settings.getBuyPrice() - ((i + 1) * TICK_SIZE),
        ORDER_SIZE, true);
      createNewOrder(Settings.getBuyPrice() + ((i + 1) * TICK_SIZE),
        ORDER_SIZE, false);

      // Do not act until the market opens.
      setNextActTime(Settings.getStartTime());
    }
  }

  /**
   * First, the intelligent agent will assume that it will have to fill orders
   * at 10 ticks above and below the best bid and ask price. It will then
   * eliminate orders at prices it knows the order still has not traded at yet.
   * 
   * Next, it will deal with the threshold, filling and canceling orders
   * depending on the threshold state.
   * 
   * Finally, it will deal with the inventory limit, canceling and filling
   * orders as necessary.
   */
  @Override
  void act() {
    // TODO Refactor this nightmare without breaking anything.

    HashSet<Integer> interestedList =
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
      // Iterate through old interval, best bid and under (buy orders)
      currentPrice = previousBestBidPrice - (i * TICK_SIZE);
      if (!interestedList.contains(currentPrice)) {
        if (isInInterval(currentPrice, bestBidPriceToFill, bestAskPriceToFill)) {
          pricesToOrder.remove(currentPrice);
        } else {
          cancelOrder(currentPrice);
        }
      }

      // Iterate through old interval, best ask and above (sell orders)
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
        if (inventoryLimitHistory.get(inventoryLimitIndex) == InventoryState.BALANCED) {
          if (previousInventoryState == InventoryState.SHARE_DEFICIT) {
            pricesToOrder.add(bestAskPriceToFill);
          } else if (previousInventoryState == InventoryState.SHARE_SURPLUS) {
            pricesToOrder.add(bestBidPriceToFill);
          }
        }
        break;
      case BUY_ORDER_SURPLUS:
        // Cancel best ask if it exists.
        pricesToOrder.remove(bestAskPriceToFill);
        orderBuffer.add(bestAskPriceToFill);
        if (isInInterval(bestAskPriceToFill, previousBestBidPrice,
          previousBestAskPrice) && !interestedList.contains(bestAskPriceToFill)) {
          cancelOrder(bestAskPriceToFill);
        }

        // Make sure best bid is still covered... should be covered already
        if (inventoryLimitHistory.get(inventoryLimitIndex) == InventoryState.BALANCED
          && previousInventoryState == InventoryState.SHARE_SURPLUS) {
          pricesToOrder.add(bestBidPriceToFill);
        }
        break;
      case SELL_ORDER_SURPLUS:
        // Cancel best bid if it exists.
        pricesToOrder.remove(bestBidPriceToFill);
        orderBuffer.add(bestBidPriceToFill);
        if (isInInterval(bestBidPriceToFill, previousBestBidPrice,
          previousBestAskPrice) && !interestedList.contains(bestBidPriceToFill)) {
          cancelOrder(bestBidPriceToFill);
        }

        // Make sure best ask is still covered... should be covered already
        if (inventoryLimitHistory.get(inventoryLimitIndex) == InventoryState.BALANCED
          && previousInventoryState == InventoryState.SHARE_DEFICIT) {
          pricesToOrder.add(bestAskPriceToFill);
        }

        break;
      default:
        System.err.println("Error: Invalid threshold state...exiting.");
        System.exit(1);
    }

    // Deal with inventory limit

    switch (inventoryLimitHistory.get(inventoryLimitIndex)) {
      case BALANCED:
        if (previousInventoryState == InventoryState.SHARE_SURPLUS) {
          // Refill all buys except edge
          for (int i = 1; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.add(bestBidPriceToFill - (i * TICK_SIZE));
          }
        } else if (previousInventoryState == InventoryState.SHARE_DEFICIT) {
          for (int i = 1; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.add(bestAskPriceToFill + (i * TICK_SIZE));
          }
        } else {
          // Continue normally
        }
        break;
      case SHARE_SURPLUS:
        if (previousInventoryState == InventoryState.SHARE_SURPLUS) {
          // Remove buy prices from toOrder
          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestBidPriceToFill - (i * TICK_SIZE));
          }

        } else if (previousInventoryState == InventoryState.SHARE_DEFICIT) {
          // Fill sells and cancel buys
          for (int i = 1; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.add(bestAskPriceToFill + (i * TICK_SIZE));
          }

          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestBidPriceToFill - (i * TICK_SIZE));
            cancelOrder(bestBidPriceToFill - (i * TICK_SIZE));
          }
        } else {
          // Cancel ALL buys
          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestBidPriceToFill - (i * TICK_SIZE));
            cancelOrder(bestBidPriceToFill - (i * TICK_SIZE));
          }
        }
        break;
      case SHARE_DEFICIT:
        if (previousInventoryState == InventoryState.SHARE_SURPLUS) {
          // Cancel all sells and refill buys
          for (int i = 1; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.add(bestBidPriceToFill - (i * TICK_SIZE));
          }

          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestBidPriceToFill + (i * TICK_SIZE));
            cancelOrder(bestAskPriceToFill + (i * TICK_SIZE));
          }

        } else if (previousInventoryState == InventoryState.SHARE_DEFICIT) {
          // Remove sell prices from toOrder
          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestAskPriceToFill + (i * TICK_SIZE));
          }
        } else {
          // Cancel all sells
          for (int i = 0; i < HALF_TICK_WIDTH; i++) {
            pricesToOrder.remove(bestAskPriceToFill + (i * TICK_SIZE));
            cancelOrder(bestAskPriceToFill + (i * TICK_SIZE));
          }
        }
        break;
      default:
        break;
    }

    // Fill all remaining orders
    for (Integer i : pricesToOrder) {
      createNewOrder(i, ORDER_SIZE, i <= bestBidPriceToFill);
    }

    // Replace inventory limit history

    previousInventoryState = inventoryLimitHistory.get(inventoryLimitIndex);
    boolean[] inventoryResults =
      checkInventory(getInventory(), INVENTORY_LIMIT, overLimit);
    overLimit = inventoryResults[OVER_LIMIT];

    if (inventoryResults[OVERRIDE]) {
      inventoryLimitHistory.set(inventoryLimitIndex, inventoryResults[WILL_BUY]
        ? InventoryState.SHARE_DEFICIT : InventoryState.SHARE_SURPLUS);
    } else {
      inventoryLimitHistory.set(inventoryLimitIndex, InventoryState.BALANCED);
    }

    inventoryLimitIndex++;
    if (inventoryLimitIndex >= delayLength) {
      inventoryLimitIndex = 0;
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
    currentOrderBook.remove(priceOfOrderTraded);
    profit +=
      (buyOrderTraded ? -priceOfOrderTraded : priceOfOrderTraded)
        * volumeTraded;
  }

  /**
   * Implementation of createNewOrder that does not create an order if one
   * already exists in the agent's orderbook.
   */
  public boolean createNewOrder(int price, int initialQuant, boolean buyOrder) {
    if (currentOrderBook.add(price)) {
      return super.createNewOrder(price, initialQuant, buyOrder);
    }
    return false;
  }

  /**
   * Implementation of cancelOrder that updates the agent's orderbook.
   */
  public void cancelOrder(int price) {
    currentOrderBook.remove(price);
    super.cancelOrder(price);
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
   * Set the helper class for Intelligent Agents.
   * 
   * @param iah
   *          The IntelligentAgentHelper class.
   */
  public void setIntelligentAgentHelper(IntelligentAgentHelper iah) {
    this.intelligentAgentHelper = iah;
  }

  /**
   * Set the threshold state at time t = now - delayLength.
   * 
   * @param newState
   *          The new state at time t = now - delayLength.
   */
  public void setOldThresholdState(ThresholdState newState) {
    oldThresholdState = newState;
  }

  /**
   * @return The profit made by this intelligent agent.
   */
  public int getProfit() {
    return profit;
  }
}
