package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 * Trades more frequently than market makers.
 */
public class HFTPoisson extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT = Parameters.hftInventoryLimit;

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

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
    super(matchEng, "HFTPoisson", lambdaOrder, lambdaCancel, initialActTime);
    overLimit = false;
  }

  @Override
  void makeOrder() {
    boolean[] inventoryResults =
      checkInventory(getInventory(), INVENTORY_LIMIT, overLimit);
    processInventory(inventoryResults[OVERRIDE], inventoryResults[WILL_BUY]);
    overLimit = inventoryResults[OVER_LIMIT];
    // Fetch qBuy once only
    double qBuy = getBestBidQuantity();
    double factor = qBuy / (qBuy + getBestAskQuantity());
    boolean willBuy = inventoryResults[WILL_BUY];

    // determine buy probability from the trend
    if (!inventoryResults[OVERRIDE] && factor < 0.9) {
      willBuy = 10 * JinSup.rand.nextFloat() < ((int) (factor * 10 + 1));
    }

    createPoissonOrder(willBuy,
      getOrderSize(Parameters.hftOrderSizeProbabilities),
      Parameters.hftTickProbabilities);
  }
}
