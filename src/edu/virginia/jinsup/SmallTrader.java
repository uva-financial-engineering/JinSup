package edu.virginia.jinsup;

import java.util.Random;

/**
 * Simple agent who makes buy orders on a 50/50 chance.
 */
public class SmallTrader extends PoissonAgent {

  /**
   * Limits the number of shares owned by the agent.
   */
  private static final int INVENTORY_LIMIT = 10;

  /**
   * Whether or not agent owns more shares than INVENTORY_LIMIT or has a deficit
   * of more than -INVENTORY_LIMIT.
   */
  private boolean overLimit;

  /**
   * Creates a small trader.
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
  public SmallTrader(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel, long initialActTime) {
    super(matchEng, lambdaOrder, lambdaCancel, initialActTime);
    overLimit = false;
  }

  @Override
  void makeOrder() {
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

    createPoissonOrder(willBuy, getOrderSize(0.98, 0.02), 0.20, 0.11, 0.09,
      0.07, 0.07, 0.07, 0.07, 0.05, 0.05, 0.10, 0.12);
  }

}
