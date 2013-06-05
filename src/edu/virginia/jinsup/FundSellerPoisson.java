package edu.virginia.jinsup;

public class FundSellerPoisson extends PoissonAgent {

  /**
   * Agent that only makes sell orders.
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
    createPoissonOrder(false, 0.14, 0.27, 0.11, 0.09, 0.07, 0.07, 0.07, 0.05,
      0.05, 0.05, 0.03);
  }
}
