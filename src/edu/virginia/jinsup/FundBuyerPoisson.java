package edu.virginia.jinsup;

public class FundBuyerPoisson extends PoissonAgent {

  public FundBuyerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
  }

  public void makeOrder() {
    createPoissonOrder(true, 0.14, 0.27, 0.11, 0.09, 0.07, 0.07, 0.07, 0.05,
      0.05, 0.05, 0.03);
  }
}
