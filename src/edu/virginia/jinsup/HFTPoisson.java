package edu.virginia.jinsup;

public class HFTPoisson extends PoissonAgent {

  public HFTPoisson(MatchingEngine matchEng, int lambdaOrder, int lambdaCancel) {
    super(matchEng, lambdaOrder, lambdaCancel);
  }

  @Override
  void makeOrder() {
    double factor =
      getBestBuyPrice() / (getBestBuyPrice() + getBestSellPrice());
    boolean willBuy = true;
    if (getInventory() > 19) {
      // cancel all buy orders orders
      cancelAllBuyOrders();
      willBuy = false;
    }
    if (getInventory() < -19) {
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
    // Market order 2% of the time
    if (probability < 0.02) {
      // Issue market order, assuming only one market order is
      createMarketOrder(1, willBuy);
    }
    // 1 tick of last trade price 15% of the time
    else if (probability < 0.17) {
      createNewOrder(getLastTradePrice(), 1, willBuy);
    }
    // 2 tick of last trade price 20% of the time
    else if (probability < 0.37) {
      createNewOrder(getLastTradePrice(), 2, willBuy);
    }
    // 3 tick of last trade price 15% of the time
    else if (probability < 0.52) {
      createNewOrder(getLastTradePrice(), 3, willBuy);
    }
    // 4 tick of last trade price 15% of the time
    else if (probability < 0.67) {
      createNewOrder(getLastTradePrice(), 4, willBuy);
    }
    // 5 tick of last trade price 15% of the time
    else if (probability < 0.82) {
      createNewOrder(getLastTradePrice(), 5, willBuy);
    }
    // 6 tick of last trade price 13% of the time
    else if (probability < 0.95) {
      createNewOrder(getLastTradePrice(), 6, willBuy);
    }
    else {
      createNewOrder(getLastTradePrice(), 7, willBuy);
    }
  }
}
