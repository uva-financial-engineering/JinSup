package edu.virginia.jinsup;

/**
 * A Market Maker. The acting methodology for the agent is exactly the same as
 * was specified in the testing document given.
 * 
 */
public class OpporStrat extends Agent {

  /**
   * Constructs the Fundamental Buyer.
   * 
   * @param matchEng
   *          The MatchingEngine used in the simulator.
   */
  public OpporStrat(MatchingEngine matchEng) {
    super(matchEng);
  }

  @Override
  public void act() {
    // if current midpoint below moving average, issue sell market order
    // but for what amount? Lets say two for now

    // TODO what if the currentMidpoint is equal to the moving average?
    createMarketOrder(2, getMidPoint() > getMovAvg());

    setWillAct(false);
    // calculate next act time (+/- 500 ms) with variation of 10%
    setNextActTime(getNextActTime()
      + (long) (JinSup.rand.nextDouble() * 50 + 475));
    return;
  }
}
