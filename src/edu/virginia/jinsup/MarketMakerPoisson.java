package edu.virginia.jinsup;

import java.util.Random;

public class MarketMakerPoisson extends PoissonAgent {

  private Random rand;

  public MarketMakerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel) {
    super(matchEng, lambdaOrder, lambdaCancel);
    rand = new Random();
  }

  public void makeOrder() {
    // bestBuyOrder/(bestBuyOrder + bestSellOrder)
    double factor =
      getBestBuyPrice() / (getBestBuyPrice() + getBestSellPrice());
    boolean willBuy = true;
    if (getInventory() > 9) {
      // cancel all buy orders orders
      cancelAllBuyOrders();
      willBuy = false;
    }
    if (getInventory() < -9) {
      // cancel all sell orders
      cancelAllSellOrders();
      willBuy = true;
    }
    if (factor < 0.10) {
      willBuy = (Math.random() < 0.1);
    }
    else if (factor < 0.20) {
      willBuy = (Math.random() < 0.2);
    }
    else if (factor < 0.30) {
      willBuy = (Math.random() < 0.3);
    }
    else if (factor < 0.40) {
      willBuy = (Math.random() < 0.4);
    }
    else if (factor < 0.50) {
      willBuy = (Math.random() < 0.5);
    }
    else if (factor < 0.60) {
      willBuy = (Math.random() < 0.6);
    }
    else if (factor < 0.70) {
      willBuy = (Math.random() < 0.7);
    }
    else if (factor < 0.80) {
      willBuy = (Math.random() < 0.8);
    }
    else if (factor < 0.90) {
      willBuy = (Math.random() < 0.9);
    }

    // calculate the probability
    double probability = Math.random();
    // Market order 30% of the time
    if (probability < 0.30) {
      // Issue market order, assuming only one market order is
      createMarketOrder(1, willBuy);
    }
    // 1 tick of last trade price 18% of the time
    else if (probability < 0.48) {
      createNewOrder(getLastTradePrice(), 1, willBuy);
    }
    // 2 tick of last trade price 12% of the time
    else if (probability < 0.60) {
      createNewOrder(getLastTradePrice(), 2, willBuy);
    }
    // 3 tick of last trade price 7% of the time
    else if (probability < 0.67) {
      createNewOrder(getLastTradePrice(), 3, willBuy);
    }
    // 4 tick of last trade price 6% of the time
    else if (probability < 0.73) {
      createNewOrder(getLastTradePrice(), 4, willBuy);
    }
    // 5 tick of last trade price 5% of the time
    else if (probability < 0.78) {
      createNewOrder(getLastTradePrice(), 5, willBuy);
    }
    // 6 tick of last trade price 4% of the time
    else if (probability < 0.82) {
      createNewOrder(getLastTradePrice(), 6, willBuy);
    }
    // 7 tick of last trade price 4% of the time
    else if (probability < 0.86) {
      createNewOrder(getLastTradePrice(), 7, willBuy);
    }
    // 8 tick of last trade price 4% of the time
    else if (probability < 0.90) {
      createNewOrder(getLastTradePrice(), 8, willBuy);
    }
    // 9 tick of last trade price 3% of the time
    else if (probability < 0.93) {
      createNewOrder(getLastTradePrice(), 9, willBuy);
    }
    // 10 tick of last trade price 7% of the time
    else {
      createNewOrder(getLastTradePrice(), 10, willBuy);
    }
  }
}
