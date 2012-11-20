public class FundSeller extends Agent {

  public FundSeller(MatchingEngine matchEng) {
    super(matchEng);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      // request to purchase 2 shares for the 10 lowest sell prices
      createNewOrder(getBuyPrice() + (i + 1) * 25, 2, true);

      // request to purchase 1 share for the 10 second highest buy prices
      if (i < 9) {
        createNewOrder(getBuyPrice() - (i - 1) * 25, 1, true);
      }
    }

    // ten percent chance of issuing a market buy order...assuming that this can
    // occur only once per turn.
    if (Math.random() < 0.1) {
      createMarketOrder(2, false);
    }
    setWillAct(false);

    // calculate next act time (+/- 5 seconds) with variation of 10%
    setNextActTime((long) (Math.random() * 500 + 4750));
  }

}
