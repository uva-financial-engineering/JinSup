package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class OpporStratPoisson extends PoissonAgent {

  private double currBuyProbability = 0.0;

  public OpporStratPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, double initialBuyProbability, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    currBuyProbability = initialBuyProbability;
  }

  public void makeOrder() {
    boolean willBuy = (Math.random() < currBuyProbability);
    // calculate the probability for the next trade
    currBuyProbability =
      currBuyProbability + (new UniformRealDistribution(-0.2, 0.2)).sample();

    // prevent the probability from going over the limit
    if (currBuyProbability < 0.20) {
      currBuyProbability = 0.20;
    }
    if (currBuyProbability > 0.80) {
      currBuyProbability = 0.80;
    }
    createPoissonOrder(willBuy, 0.35, 0.20, 0.05, 0.05, 0.05, 0.05, 0.07, 0.05,
      0.05, 0.06, 0.04);
  }
}