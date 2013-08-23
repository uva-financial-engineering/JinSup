package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 */
public class MarketMakerPoisson extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT =
    Parameters.marketMakerInventoryLimit;

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

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
    overLimit = false;
  }

  @Override
  public void makeOrder() {
    // bestBuyOrder/(bestBuyOrder + bestSellOrder)
    double bestBuyPrice = getBestBuyPrice();
    double factor = bestBuyPrice / (bestBuyPrice + getBestSellPrice());
    boolean willBuy = true;

    // Whether or not to skip factor checking
    boolean override = true;

    if (getInventory() > INVENTORY_LIMIT) {
      overLimit = true;
      willBuy = false;
      cancelAllBuyOrders();
    } else if (getInventory() < -INVENTORY_LIMIT) {
      overLimit = true;
      willBuy = true;
      cancelAllSellOrders();
    } else if (getInventory() > INVENTORY_LIMIT / 2 && overLimit) {
      willBuy = false;
    } else if (getInventory() < -INVENTORY_LIMIT / 2 && overLimit) {
      willBuy = true;
    } else if (getInventory() < Math.abs(INVENTORY_LIMIT) / 2) {
      overLimit = false;
      override = false;
    }

    if (!override && factor < 0.9) {
      willBuy = 10 * Math.random() < ((int) (factor * 10 + 1));
    }
    createPoissonOrder(willBuy,
      getOrderSize(Parameters.marketMakerOrderSizeProbabilities),
      Parameters.marketMakerTickProbabilities);
  }
}
