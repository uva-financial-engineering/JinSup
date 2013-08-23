package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * Agent whose behavior depends on a global buy probability (acting as a news
 * feed).
 */
public class OpporStratPoisson extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT =
    Parameters.opporStratInventoryLimit;

  private static final double LOWER_UNIFORM_BOUND =
    Parameters.lowerUniformBound;
  private static final double UPPER_UNIFORM_BOUND =
    Parameters.upperUniformBound;
  private static final double MIN_BUY_PROBABILITY =
    Parameters.minBuyProbability;
  private static final double MAX_BUY_PROBABILITY =
    Parameters.maxBuyProbability;

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

  /**
   * Global buy probability for all poisson opportunistic traders. Initially set
   * at 50 %.
   */
  private static double currBuyProbability = 0.50;

  /**
   * Creates an opportunistic trader.
   * 
   * @param matchEng
   *          Matching engine of the simulation.
   * @param lambdaOrder
   *          The mean order creation frequency.
   * @param lambdaCancel
   *          The mean order cancellation frequency.
   * @param initialBuyProbability
   *          Initial probability of making a buy order.
   * @param initialActTime
   *          Startup time of the simulation.
   */
  public OpporStratPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    overLimit = false;
  }

  public void makeOrder() {
    boolean willBuy = true;
    // Whether or not to skip factor checking
    boolean override = true;

    if (getInventory() > INVENTORY_LIMIT) {
      overLimit = true;
      willBuy = false;
      cancelAllBuyOrders();
    } else if (getInventory() < -INVENTORY_LIMIT) {
      overLimit = true;
      willBuy = true;
      cancelAllSellOrders();
    } else if (getInventory() > INVENTORY_LIMIT / 2 && overLimit) {
      willBuy = false;
    } else if (getInventory() < -INVENTORY_LIMIT / 2 && overLimit) {
      willBuy = true;
    } else if (getInventory() < Math.abs(INVENTORY_LIMIT) / 2) {
      overLimit = false;
      override = false;
    }
    if (!override) {
      willBuy = (Math.random() < currBuyProbability);
    }

    createPoissonOrder(willBuy,
      getOrderSize(Parameters.opporStratOrderSizeProbabilities),
      Parameters.opporStratTickProbabilities);
  }

  /**
   * Calculates the new global buy probability.
   */
  public static void calcNewBuyProbability() {
    currBuyProbability =
      currBuyProbability
        + (new UniformRealDistribution(LOWER_UNIFORM_BOUND, UPPER_UNIFORM_BOUND))
          .sample();

    // prevent the probability from going over the limit
    if (currBuyProbability < MIN_BUY_PROBABILITY) {
      currBuyProbability = MIN_BUY_PROBABILITY;
    }
    if (currBuyProbability > MAX_BUY_PROBABILITY) {
      currBuyProbability = MAX_BUY_PROBABILITY;
    }
  }

  /**
   * Manually set the global buy probability.
   * 
   * @param buyProbability
   *          The probability of making a buy order.
   */
  public static void setBuyProbability(double buyProbability) {
    currBuyProbability = buyProbability;
  }

  public static double getCurrBuyProbability() {
    return currBuyProbability;
  }

}
