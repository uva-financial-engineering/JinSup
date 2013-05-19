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

    // if Shares own 10 cancel all buys and P(buy) = 0%
    if (getInventory() > 9) {
      // cancel all buy orders orders
      cancelAllBuyOrders();
      willBuy = false;
    }

    // if shares own -10 cancel all sell and P(buy) = 100%
    if (getInventory() < -9) {
      // cancel all sell orders
      cancelAllSellOrders();
      willBuy = true;
    }
    if (factor < 0.10) {
      willBuy = (Math.random() < 0.1);
    } else if (factor < 0.20) {
      willBuy = (Math.random() < 0.2);
    } else if (factor < 0.30) {
      willBuy = (Math.random() < 0.3);
    } else if (factor < 0.40) {
      willBuy = (Math.random() < 0.4);
    } else if (factor < 0.50) {
      willBuy = (Math.random() < 0.5);
    } else if (factor < 0.60) {
      willBuy = (Math.random() < 0.6);
    } else if (factor < 0.70) {
      willBuy = (Math.random() < 0.7);
    } else if (factor < 0.80) {
      willBuy = (Math.random() < 0.8);
    } else if (factor < 0.90) {
      willBuy = (Math.random() < 0.9);
    }
    createPoissonOrder(willBuy, 0.30, 0.18, 0.12, 0.07, 0.06, 0.05, 0.04, 0.04,
      0.04, 0.03, 0.07);
  }
}
