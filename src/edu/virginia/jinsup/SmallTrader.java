package edu.virginia.jinsup;

import java.util.Random;

/**
 * Simple agent who makes buy orders on a 50/50 chance.
 */
public class SmallTrader extends PoissonAgent {

  private Random rand;

  /**
   * Creates a small trader.
   * 
   * @param matchEng
   *          Matching engine of the simulation.
   * @param lambdaOrder
   *          The mean order creation frequency.
   * @param lambdaCancel
   *          The mean order cancellation frequency.
   * @param initialActTime
   *          Startup time of the simulation.
   */
  public SmallTrader(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    rand = new Random();
  }

  @Override
  void makeOrder() {
    // Buy 50% of the time, sell 50% of the time
    boolean willBuy = rand.nextBoolean();

    createPoissonOrder(willBuy, getOrderSize(0.98, 0.02), 0.20, 0.11, 0.09,
      0.07, 0.07, 0.07, 0.07, 0.05, 0.05, 0.10, 0.12);
  }

}
