package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 * Trades more frequently than market makers.
 */
public class HFTPoisson extends PoissonAgent {

  /**
   * Creates a HFTPoisson trader.
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
  public HFTPoisson(MatchingEngine matchEng, double lambdaOrder,
    double lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
  }

  @Override
  void makeOrder() {
    double factor =
      (double) getBestBuyPrice() / (getBestBuyPrice() + getBestSellPrice());
    boolean willBuy = true;

    // Whether or not to skip factor checking
    boolean override = false;
    // if shares own 20 cancel all buys and P(buy) = 0%
    if (getInventory() > 19) {
      // cancel all buy orders orders
      cancelAllBuyOrders();
      willBuy = false;
      override = true;
    }

    // if shares own -20 cancel all sell and P(buy) = 100%
    if (getInventory() < -19) {
      // cancel all sell orders
      cancelAllSellOrders();
      willBuy = true;
      override = true;
    }

    // determine buy probability from the trend
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
    createPoissonOrder(willBuy, 0.02, 0.15, 0.20, 0.15, 0.15, 0.15, 0.13, 0.05);
  }
}
