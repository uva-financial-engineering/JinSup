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

  private static ThresholdState previousThresholdState =
    ThresholdState.BELOW_THRESHOLD;

  private static ThresholdState currentThresholdState =
    ThresholdState.BELOW_THRESHOLD;

  private static int previousTradePrice = 0;

  private static int currentTradePrice = 0;

  /**
   * List of order prices over time that were traded that may need to be covered
   * by the agent.
   */
  private ArrayList<ArrayList<Integer>> potentialOrdersToCover;

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
    switch (previousThresholdState) {
      case BELOW_THRESHOLD:
        switch (currentThresholdState) {
          case BELOW_THRESHOLD:
            // Make sure best bid/ask are covered.
            break;
          case BUY_ORDER_SURPLUS:
            // Cancel best ask if it exists.
            break;
          case SELL_ORDER_SURPLUS:
            // Cancel best bid if it exists.
            break;
          default:
            System.out.println("Error: Invalid threshold state...exiting.");
            System.exit(1);
        }
        break;
      case BUY_ORDER_SURPLUS:
        switch (currentThresholdState) {
          case BELOW_THRESHOLD:
            // Create best ask.
            break;
          case BUY_ORDER_SURPLUS:
            // Leave best ask alone and check that best bid is covered.
            break;
          case SELL_ORDER_SURPLUS:
            // Leave best bid alone and check that best ask is covered.
            break;
          default:
            System.out.println("Error: Invalid threshold state...exiting.");
            System.exit(1);
        }
        break;
      case SELL_ORDER_SURPLUS:
        switch (currentThresholdState) {
          case BELOW_THRESHOLD:
            // Create best bid.
            break;
          case BUY_ORDER_SURPLUS:
            // Leave best ask alone and check that best bid is covered.
            break;
          case SELL_ORDER_SURPLUS:
            // Leave best bid alone and check that best ask is covered.
            break;
          default:
            System.out.println("Error: Invalid threshold state...exiting.");
            System.exit(1);
        }
        break;
      default:
        System.out.println("Error: Invalid threshold state...exiting.");
        System.exit(1);
    }

    // Deal with orders at the edge
    int currentTradePriceDifference =
      intelligentAgentHelper.getTradePriceDifference();
    if (currentTradePriceDifference == 0) {
      // Make sure edges are filled.

    } else if (currentTradePriceDifference > 0) {
      // Price decreased, create more buy orders and remove sell orders.
    } else {
      // Price increased, create more sell orders and remove buy orders.
    }

    // Deal with all other orders
    // TODO Make sure that requested order is neither best bid/ask nor edge
    // otherwise we will process it twice. (Should remove from list first)

    setNextActTime(getNextActTime() + INTERVAL);
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
    previousThresholdState = currentThresholdState;
    currentThresholdState = newState;
  }

}
