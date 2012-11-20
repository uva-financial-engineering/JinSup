public class MarketMaker extends Agent {

  public MarketMaker(MatchingEngine matchEng) {
    super(matchEng);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      createNewOrder(getBuyPrice() - i * 25, 1, true);
      createNewOrder(getBuyPrice() + (i + 1) * 25, 1, true);
    }

    // if a trade occurs, must maintain structure.

    // calculate next act time (+/- 1 seconds) with variation of 10%
    setNextActTime((long) (Math.random() * 100 + 950));
    return;
  }

}
