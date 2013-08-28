package edu.virginia.jinsup;

/**
 * A Fundamental Seller. The acting methodology for the agent deviates a bit
 * from the testing document given since the agent will sell 2 shares for the 10
 * lowest sell prices and will buy 1 share for the 9 second highest buy prices.
 * We were not sure whether Agents can simply cross the buy/ask boundary and buy
 * shares that were in the sell territory the first time around.
 */
public class FundSeller extends Agent {

  /**
   * Constructs the Fundamental Buyer.
   * 
   * @param matchEng
   *          The MatchingEngine used in the simulator.
   */
  public FundSeller(MatchingEngine matchEng) {
    super(matchEng);
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      // request to SELL 2 shares for the 10 lowest sell prices
      createNewOrder(Settings.getBuyPrice() + ((i + 1) * 25), 2, false);

      // request to BUY 1 share for the 9 second highest buy prices
      if (i < 9) {
        createNewOrder(Settings.getBuyPrice() - ((i) * 25) - 25, 1, true);
      }
    }

    // ten percent chance of issuing a market sell order...assuming that this
    // can occur only once per turn.
    if (JinSup.rand.nextFloat() < 0.1) {
      createMarketOrder(2, false);
    }
    setWillAct(false);

    // calculate next act time (+/- 5 seconds) with variation of 10%
    setNextActTime(getNextActTime()
      + (long) (JinSup.rand.nextDouble() * 500 + 4750));
  }
}
