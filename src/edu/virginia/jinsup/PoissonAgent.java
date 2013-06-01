package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.PoissonDistribution;

public abstract class PoissonAgent extends Agent {

  private final PoissonDistribution poissonGeneratorOrder;
  private final PoissonDistribution poissonGeneratorCancel;
  private static final int TICK_SIZE = 25;

  /**
   * Agent that implements a poisson trading distribution.
   * 
   * @param matchEng
   *          Matching engine of simulation.
   * @param lambdaOrder
   *          Mean order rate in seconds.
   * @param lambdaCancel
   *          Mean cancel rate in seconds.
   * @param initialActTime
   *          Length of startup period in milliseconds.
   */
  public PoissonAgent(MatchingEngine matchEng, double lambdaOrder,
    double lambdaCancel, long initialActTime) {
    super(matchEng);
    poissonGeneratorOrder = new PoissonDistribution(lambdaOrder * 1000);
    poissonGeneratorCancel = new PoissonDistribution(lambdaCancel * 1000);

    // set agents to create an order before canceling one
    setNextAction(Action.ORDER);
    setNextActTime(initialActTime);

    // no need for poisson determined act times for initial actions
    super.setNextOrderTime(initialActTime);
    super.setNextCancelTime(getStartupTime() + poissonGeneratorCancel.sample());
  }

  public void act() {
    long oldOrderTime = getNextOrderTime();
    switch (getNextAction()) {
      case CANCEL:
        // cancel a random order, if there are any available
        if (getRandomOrder() != null) {
          cancelOrder(getRandomOrder());
        }
        setNextCancelTime(getNextCancelTime());
        break;
      case ORDER:
        makeOrder();
        setNextOrderTime(getNextOrderTime());
        break;
      case NULL:
        System.out.println("Warning: NULL action type...have all agents been"
          + "  properly initialized?");
        break;
      default:
        System.out.println("Fatal Error: Undefined action enum type...exiting");
        System.exit(1);
        break;
    }

    // make sure that both actions do not occur at the same time step
    if (getNextCancelTime() == getNextOrderTime()) {
      while (getNextCancelTime() == getNextOrderTime()) {
        setNextOrderTime(oldOrderTime);
      }
    }

    // select the appropriate action to perform for the next act opportunity
    if (getNextCancelTime() > getNextOrderTime()) {
      setNextAction(Action.ORDER);
      setNextActTime(getNextOrderTime());
    } else {
      setNextAction(Action.CANCEL);
      setNextActTime(getNextCancelTime());
    }
    setWillAct(false);
  }

  protected void setNextOrderTime(long currOrderTime) {
    super.setNextOrderTime(currOrderTime + poissonGeneratorOrder.sample());
  }

  protected void setNextCancelTime(long currCancelTime) {
    super.setNextCancelTime(currCancelTime + poissonGeneratorCancel.sample());
  }

  /**
   * Manages the probabilities of creating orders. Implementations will differ
   * depending on type of agent.
   */
  abstract void makeOrder();

  /**
   * Deals with order creation given a list of probabilities. *** The first
   * probability supplied must be the probability of creating a market order
   * ***.
   * 
   * @param isBuying
   *          True if agent is issuing a buy order; false if issuing a sell
   *          order.
   * @param probabilities
   *          Probabilities of creating an order for a certain tick from the
   *          last trade price. The probabilities must be listed in ascending
   *          order with respect to the number of ticks of the last trade price,
   *          e.g. P[1 tick], P[2 tick], etc.
   */
  void createPoissonOrder(boolean isBuying, double... probabilities) {
    // make sure the probabilities add up close to 1.0
    // TODO remove probability sum check after debugging for performance
    // double sum = 0.0;
    // for (double i : probabilities) {
    // sum += i;
    // }
    // if (sum < 0.9999999999) {
    // System.out
    // .println("The probabilities do not add up close enough to 1.0. The sum of probabilities supplied was "
    // + sum);
    // System.exit(1);
    // }

    double probability = Math.random();

    if (probability < probabilities[0]) {
      // create a market order
      createMarketOrder(1, isBuying);
      return;
    }

    double cumulativeProb = probabilities[0] + probabilities[1];
    for (int i = 1; i < probabilities.length - 1; ++i) {
      if (probability < cumulativeProb) {
        // create a limit order; if the agent is buying, then as the tick
        // increases, the lower the buy price
        createNewOrder(getLastTradePrice()
          - ((isBuying ? 1 : -1) * TICK_SIZE * i), 1, isBuying);
        return;
      }
      cumulativeProb += probabilities[i + 1];
    }
  }
}
