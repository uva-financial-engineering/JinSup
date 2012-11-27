public class FundBuyer extends Agent {

  public FundBuyer(MatchingEngine matchEng) {
    super(matchEng);
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      // request to purchase 2 shares for the 10 highest buy prices
      createNewOrder(getBuyPrice() - i * 25, 2, true);

      // request to purchase 1 share for the 9 second lowest sell prices
      if (i < 9) {
        createNewOrder(getBuyPrice() + (i + 2) * 25, 1, true);
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
