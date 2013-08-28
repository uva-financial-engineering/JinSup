package edu.virginia.jinsup;

/**
 * A Market Maker. The acting methodology for the agent deviates a bit from the
 * testing document given since the agent will buy 1 share for the ten highest
 * buy prices and sell 1 share for the ten lowest sell prices. We were not sure
 * whether Agents can simply cross the buy/ask boundary and buy shares that were
 * in the sell territory the first time around.
 * 
 */
public class MarketMaker extends Agent {

  /**
   * Constructs the Fundamental Buyer.
   * 
   * @param matchEng
   *          The MatchingEngine used in the simulator.
   */
  public MarketMaker(MatchingEngine matchEng) {
    super(matchEng);
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      while (createNewOrder(Settings.getBuyPrice() - i * 25, 1, true)) {
        createNewOrder(Settings.getBuyPrice() - i * 25, 1, true);
      }
      while (createNewOrder(Settings.getBuyPrice() + (i + 1) * 25, 1, false)) {
        createNewOrder(Settings.getBuyPrice() + (i + 1) * 25, 1, false);
      }
    }

    // if a trade occurs, must maintain structure.
    setWillAct(false);

    // calculate next act time (+/- 1 seconds) with variation of 10%
    setNextActTime(getNextActTime()
      + (long) (JinSup.rand.nextDouble() * 100 + 950));
    return;
  }
}
