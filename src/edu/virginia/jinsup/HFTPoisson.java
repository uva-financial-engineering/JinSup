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

    if (getInventory() > 19) {
      // if shares own 20 cancel all buys and P(buy) = 0%
      cancelAllBuyOrders();
      willBuy = false;
      override = true;
    } else if (getInventory() < -19) {
      // if shares own -20 cancel all sell and P(buy) = 100%
      cancelAllSellOrders();
      willBuy = true;
      override = true;
    }

    // determine buy probability from the trend
    if (!override && factor < 0.9) {
      willBuy = 10 * Math.random() < ((int) (factor * 10 + 1));
    }

    // TODO Fix specs regarding order size probabilities for HFTs
    createPoissonOrder(willBuy, 1, 0.02, 0.15, 0.20, 0.15, 0.15, 0.15, 0.13,
      0.05);
  }
}
