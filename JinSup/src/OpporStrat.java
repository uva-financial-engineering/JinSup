public class OpporStrat extends Agent {

  public OpporStrat(MatchingEngine matchEng) {
    super(matchEng);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void act() {
    // if current midpoint below moving average, issue sell market order
    // but for what amount? Lets say two for now

    // TODO what if the currentMidpoint is equal to the moving average?
    createMarketOrder(2, getMidPoint() > getMovAvg());

    // calculate next act time (+/- 500 ms) with variation of 10%
    setNextActTime((long) (Math.random() * 50 + 475));
    return;
  }
}
