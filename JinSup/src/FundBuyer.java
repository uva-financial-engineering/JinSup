/**
 * A Fundamental Buyer. The acting methodology for the agent deviates a bit from
 * the testing document given since the agent will buy 2 shares for the 10
 * highest buy prices and will sell 1 share for the 9 second lowest sell prices.
 * We were not sure whether Agents can simply cross the buy/ask boundary and buy
 * shares that were in the sell territory the first time around.
 * 
 */
public class FundBuyer extends Agent {

  /**
   * Constructs the Fundamental Buyer.
   * 
   * @param matchEng
   *          The MatchingEngine used in the simulator.
   */
  public FundBuyer(MatchingEngine matchEng) {
    super(matchEng);
  }

  public void act() {
    for (int i = 0; i < 10; i++) {
      // request to BUY 2 shares for the 10 highest buy prices
      createNewOrder(getBuyPrice() - (i * 25), 2, true);

      // request to SELL 1 share for the 9 second lowest sell prices
      if (i < 9) {
        createNewOrder(getBuyPrice() + ((i + 1) * 25) + 25, 1, false);
      }
    }

    // ten percent chance of issuing a market buy order... assuming that this
    // can occur only once per turn.
    if (Math.random() < 0.1) {
      createMarketOrder(2, true);
    }
    setWillAct(false);

    // calculate next act time (+/- 5 seconds) with variation of 10%
    setNextActTime(getNextActTime() + (long) (Math.random() * 500 + 4750));
  }
}
