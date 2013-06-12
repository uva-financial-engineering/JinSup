package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 */
public class MarketMakerPoisson extends PoissonAgent {

  /**
   * Creates a Market Maker.
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
  public MarketMakerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
  }

  public void makeOrder() {
    // bestBuyOrder/(bestBuyOrder + bestSellOrder)
    double factor =
      (double) getBestBuyPrice() / (getBestBuyPrice() + getBestSellPrice());
    boolean willBuy = true;

    boolean override = false;
    // if Shares own 10 cancel all buys and P(buy) = 0%
    if (getInventory() > 9) {
      // cancel all buy orders orders
      cancelAllBuyOrders();
      willBuy = false;
      override = true;
    }

    // if shares own -10 cancel all sell and P(buy) = 100%
    if (getInventory() < -9) {
      // cancel all sell orders
      cancelAllSellOrders();
      willBuy = true;
      override = true;
    }
    if (!override) {
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
    }
    createPoissonOrder(willBuy, getOrderSize(0.76, 0.06, 0.06, 0.05, 0.07),
      0.30, 0.18, 0.12, 0.07, 0.06, 0.05, 0.04, 0.04, 0.04, 0.03, 0.07);
  }
}
