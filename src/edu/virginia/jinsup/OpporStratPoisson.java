package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.UniformRealDistribution;

public class OpporStratPoisson extends PoissonAgent {

  private double currBuyProbability = 0.0;

  public OpporStratPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, double initialBuyProbability) {
    super(matchEng, lambdaOrder, lambdaCancel);
    currBuyProbability = initialBuyProbability;
  }

  public void makeOrder() {
    boolean willBuy = (Math.random() < currBuyProbability);
    // calculate the probability for the next trade
    currBuyProbability =
      currBuyProbability
        + (new UniformRealDistribution(0.2 - currBuyProbability,
          currBuyProbability - 0.8)).sample();
    double probability = Math.random();
    // Market order 35% of the time
    if (probability < 0.35) {
      // Issue market order, assuming only one market order is
      createMarketOrder(1, willBuy);
    }
    // 1 tick of last trade price 20% of the time
    else if (probability < 0.55) {
      createNewOrder(getLastTradePrice(), 1, willBuy);
    }
    // 2 tick of last trade price 5% of the time
    else if (probability < 0.60) {
      createNewOrder(getLastTradePrice(), 2, willBuy);
    }
    // 3 tick of last trade price 5% of the time
    else if (probability < 0.65) {
      createNewOrder(getLastTradePrice(), 3, willBuy);
    }
    // 4 tick of last trade price 5% of the time
    else if (probability < 0.70) {
      createNewOrder(getLastTradePrice(), 4, willBuy);
    }
    // 5 tick of last trade price 5% of the time
    else if (probability < 0.75) {
      createNewOrder(getLastTradePrice(), 5, willBuy);
    }
    // 6 tick of last trade price 7% of the time
    else if (probability < 0.80) {
      createNewOrder(getLastTradePrice(), 6, willBuy);
    }
    // 7 tick of last trade price 5% of the time
    else if (probability < 0.85) {
      createNewOrder(getLastTradePrice(), 7, willBuy);
    }
    // 8 tick of last trade price 5% of the time
    else if (probability < 0.90) {
      createNewOrder(getLastTradePrice(), 8, willBuy);
    }
    // 9 tick of last trade price 6% of the time
    else if (probability < 0.96) {
      createNewOrder(getLastTradePrice(), 9, willBuy);
    }
    // 10 tick of last trade price 4% of the time
    else {
      createNewOrder(getLastTradePrice(), 10, willBuy);
    }
  }
}
