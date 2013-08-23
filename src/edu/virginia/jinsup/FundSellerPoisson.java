package edu.virginia.jinsup;

/**
 * Agent that only makes sell orders.
 */
public class FundSellerPoisson extends PoissonAgent {

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
  public FundSellerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
  }

  // probabilities are the same as in the fundamental buyer's method...for now
  public void makeOrder() {
    createPoissonOrder(false,
      getOrderSize(Parameters.fundamentalOrderSizeProbabilities),
      Parameters.fundamentalTickProbabilities);
  }
}
