package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 */
public class MarketMakerPoisson extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT = 30;

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
    boolean[] inventoryResults =
      checkInventory(getInventory(), INVENTORY_LIMIT, overLimit);
    processInventory(inventoryResults[OVERRIDE], inventoryResults[WILL_BUY]);
    overLimit = inventoryResults[OVER_LIMIT];
    // bestBuyOrder/(bestBuyOrder + bestSellOrder)
    double bestBuyPrice = getBestBuyPrice();
    double factor = bestBuyPrice / (bestBuyPrice + getBestSellPrice());
    boolean willBuy = inventoryResults[WILL_BUY];

    // Whether or not to skip factor checking

    if (!inventoryResults[OVERRIDE] && factor < 0.9) {
      willBuy = 10 * JinSup.rand.nextFloat() < ((int) (factor * 10 + 1));
    }
    createPoissonOrder(willBuy,
      getOrderSize(Parameters.marketMakerOrderSizeProbabilities),
      Parameters.marketMakerTickProbabilities);
  }
}
