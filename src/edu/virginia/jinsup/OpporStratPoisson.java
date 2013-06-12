package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * Agent whose behavior depends on a global buy probability (acting as a news
 * feed).
 */
public class OpporStratPoisson extends PoissonAgent {

  /**
   * Global buy probability for all poisson opportunistic traders
   */
  private static double currBuyProbability = 0.0;

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
    int lambdaCancel, double initialBuyProbability, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    currBuyProbability = initialBuyProbability;
  }

  public void makeOrder() {
    boolean willBuy = (Math.random() < currBuyProbability);
    createPoissonOrder(willBuy,
      getOrderSize(0.66, 0.16, 0.05, 0.04, 0.03, 0.03, 0.03), 0.35, 0.20, 0.05,
      0.05, 0.05, 0.05, 0.07, 0.05, 0.05, 0.06, 0.04);
  }

  /**
   * Calculates the new global buy probability.
   */
  static public void calcNewBuyProbability() {
    currBuyProbability =
      currBuyProbability + (new UniformRealDistribution(-0.2, 0.2)).sample();

    // prevent the probability from going over the limit
    if (currBuyProbability < 0.20) {
      currBuyProbability = 0.20;
    }
    if (currBuyProbability > 0.80) {
      currBuyProbability = 0.80;
    }
    System.out.println(currBuyProbability);
  }
}
