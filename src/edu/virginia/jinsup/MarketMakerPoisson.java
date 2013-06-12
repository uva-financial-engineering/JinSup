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

  @Override
  public void makeOrder() {
    // bestBuyOrder/(bestBuyOrder + bestSellOrder)
    double factor =
      (double) getBestBuyPrice() / (getBestBuyPrice() + getBestSellPrice());
    boolean willBuy = true;

    boolean override = false;
    if (getInventory() > 9) {
      // if Shares own 10 cancel all buys and P(buy) = 0%
      cancelAllBuyOrders();
      willBuy = false;
      override = true;
    } else if (getInventory() < -9) {
      // if shares own -10 cancel all sell and P(buy) = 100%
      // cancel all sell orders
      cancelAllSellOrders();
      willBuy = true;
      override = true;
    }
    if (!override && factor < 0.9) {
      willBuy = 10 * Math.random() < ((int) (factor * 10 + 1));
    }
    createPoissonOrder(willBuy, getOrderSize(0.76, 0.06, 0.06, 0.05, 0.07),
      0.30, 0.18, 0.12, 0.07, 0.06, 0.05, 0.04, 0.04, 0.04, 0.03, 0.07);
  }
}
