public class MarketMaker extends Agent {

  public MarketMaker(MatchingEngine matchEng) {
    super(matchEng);
  }

  @Override
  public void act() {
    for (int i = 0; i < 10; i++) {
      while (createNewOrder(getBuyPrice() - i * 25, 1, true)) {
        createNewOrder(getBuyPrice() - i * 25, 1, true);
      }

      while (createNewOrder(getBuyPrice() + (i + 1) * 25, 1, true)) {
        createNewOrder(getBuyPrice() + (i + 1) * 25, 1, true);
      }
    }

    // if a trade occurs, must maintain structure.
    setWillAct(false);

    // calculate next act time (+/- 1 seconds) with variation of 10%
    setNextActTime(getNextActTime() + (long) (Math.random() * 100 + 950));
    return;
  }
}
