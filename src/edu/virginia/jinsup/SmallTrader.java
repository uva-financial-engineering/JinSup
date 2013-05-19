package edu.virginia.jinsup;

import java.util.Random;

public class SmallTrader extends PoissonAgent {

  private Random rand;

  public SmallTrader(MatchingEngine matchEng, int lambdaOrder, int lambdaCancel) {
    super(matchEng, lambdaOrder, lambdaCancel);
    rand = new Random();
  }

  @Override
  void makeOrder() {
    // Buy 50% of the time, sell 50% of the time
    boolean willBuy = rand.nextBoolean();
    createPoissonOrder(willBuy, 0.20, 0.11, 0.09, 0.07, 0.07, 0.07, 0.07, 0.05,
      0.05, 0.10, 0.12);
  }

}
