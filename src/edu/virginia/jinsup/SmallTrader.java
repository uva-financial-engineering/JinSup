package edu.virginia.jinsup;

import java.util.Random;

public class SmallTrader extends PoissonAgent {

  private Random rand;

  public SmallTrader(MatchingEngine matchEng, int lambdaOrder, int lambdaCancel) {
    super(matchEng, lambdaOrder, lambdaCancel);
    rand = new Random();
  }

  @Override
  void makeOrder() {
    // Buy 50% of the time, sell 50% of the time
    boolean willBuy = rand.nextBoolean();
    // calculate the probability
    double probability = Math.random();
    // Market order 20% of the time
    if (probability < 0.20) {
      // Issue market order, assuming only one market order is
      createMarketOrder(1, willBuy);
    }
    // 1 tick of last trade price 11% of the time
    else if (probability < 0.31) {
      createNewOrder(getLastTradePrice(), 1, willBuy);
    }
    // 2 tick of last trade price 9% of the time
    else if (probability < 0.42) {
      createNewOrder(getLastTradePrice(), 2, willBuy);
    }
    // 3 tick of last trade price 7% of the time
    else if (probability < 0.51) {
      createNewOrder(getLastTradePrice(), 3, willBuy);
    }
    // 4 tick of last trade price 7% of the time
    else if (probability < 0.58) {
      createNewOrder(getLastTradePrice(), 4, willBuy);
    }
    // 5 tick of last trade price 7% of the time
    else if (probability < 0.65) {
      createNewOrder(getLastTradePrice(), 5, willBuy);
    }
    // 6 tick of last trade price 7% of the time
    else if (probability < 0.72) {
      createNewOrder(getLastTradePrice(), 6, willBuy);
    }
    // 7 tick of last trade price 5% of the time
    else if (probability < 0.77) {
      createNewOrder(getLastTradePrice(), 7, willBuy);
    }
    // 8 tick of last trade price 5% of the time
    else if (probability < 0.82) {
      createNewOrder(getLastTradePrice(), 8, willBuy);
    }
    // 9 tick of last trade price 10% of the time
    else if (probability < 0.92) {
      createNewOrder(getLastTradePrice(), 9, willBuy);
    }
    // 10 tick of last trade price 12% of the time
    else {
      createNewOrder(getLastTradePrice(), 10, willBuy);
    }
  }

}
