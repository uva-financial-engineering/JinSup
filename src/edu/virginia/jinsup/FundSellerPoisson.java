package edu.virginia.jinsup;

public class FundSellerPoisson extends PoissonAgent {

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
