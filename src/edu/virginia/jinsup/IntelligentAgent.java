package edu.virginia.jinsup;

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
    int bidAskDifference = intelligentAgentHelper.getOldVolumeDifferenceData();
    if (Math.abs(bidAskDifference) < threshold) {
      // Ensure that all tick levels have orders.
    } else {
      // Deal with threshold crossing.
    }
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
}
