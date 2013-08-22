package edu.virginia.jinsup;

/**
 * Agent whose behavior depends on the current best bid/ask ratio and inventory.
 * Trades more frequently than market makers.
 */
public class HFTPoisson extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT = 150;

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
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    overLimit = false;
  }

  @Override
  void makeOrder() {

    // Fetch qBuy once only
    double qBuy = getBestBidQuantity();
    double factor = qBuy / (qBuy + getBestAskQuantity());
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

    // determine buy probability from the trend
    if (!override && factor < 0.9) {
      willBuy = 10 * Math.random() < ((int) (factor * 10 + 1));
    }

    createPoissonOrder(willBuy,
      getOrderSize(0.57, 0.19, 0.04, 0.05, 0.05, 0.05, 0.05), 0.02, 0.15, 0.20,
      0.15, 0.15, 0.15, 0.13, 0.05);
  }
}
