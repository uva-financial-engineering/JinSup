package edu.virginia.jinsup;

/**
 * Agent that only makes buy orders.
 */
public class FundBuyerPoisson extends PoissonAgent {

  /**
   * Creates a fundamental buyer.
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
  public FundBuyerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
  }

  public void makeOrder() {
    createPoissonOrder(true,
      getOrderSize(Parameters.fundamentalOrderSizeProbabilities),
      Parameters.fundamentalTickProbabilities);
  }
}
