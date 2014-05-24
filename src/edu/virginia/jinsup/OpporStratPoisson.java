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

  @Override
  public void makeOrder() {
    boolean[] inventoryResults =
      checkInventory(getInventory(), INVENTORY_LIMIT, overLimit);
    processInventory(inventoryResults[OVERRIDE], inventoryResults[WILL_BUY]);
    overLimit = inventoryResults[OVER_LIMIT];
    boolean willBuy = inventoryResults[WILL_BUY];
    // Whether or not to skip factor checking

    if (!inventoryResults[OVERRIDE]) {
      willBuy = (JinSup.rand.nextFloat() < currBuyProbability);
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
        + (new UniformRealDistribution(JinSup.randGen,
          Parameters.lowerUniformBound, Parameters.upperUniformBound)).sample();

    // prevent the probability from going over the limit
    if (currBuyProbability < Parameters.minBuyProbability) {
      currBuyProbability = Parameters.minBuyProbability;
    }
    if (currBuyProbability > Parameters.maxBuyProbability) {
      currBuyProbability = Parameters.maxBuyProbability;
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
