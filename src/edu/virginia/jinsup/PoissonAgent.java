package edu.virginia.jinsup;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * Agent that implements a poisson trading distribution.
 */
public abstract class PoissonAgent extends Agent {

  /**
   * Mean time between orders.
   */
  private final double lambdaOrder;

  /**
   * Mean time between cancellations.
   */
  private final double lambdaCancel;

  /**
   * Exponential distribution with mean 1 / lambdaOrder.
   */
  private final ExponentialDistribution orderDist;

  /**
   * Exponential distribution with mean 1 / lambdaCancel.
   */
  private final ExponentialDistribution cancelDist;

  /**
   * Constructs a poisson trader (can only be called from a subclass).
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

    this.lambdaOrder = lambdaOrder * 1000;
    this.lambdaCancel = lambdaCancel * 1000;

    this.orderDist =
      new ExponentialDistribution(JinSup.randGen, 1 / this.lambdaOrder,
        ExponentialDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    this.cancelDist =
      new ExponentialDistribution(JinSup.randGen, 1 / this.lambdaCancel,
        ExponentialDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);

    // set agents to create an order before canceling one
    setNextAction(Action.ORDER);
    setNextActTime(initialActTime);

    // no need for poisson determined act times for initial actions
    super.setNextOrderTime(initialActTime);
    super.setNextCancelTime((long) (getStartupTime() + 1.0 - this.lambdaCancel
      * (cancelDist.sample() - 1.0)));
  }

  /**
   * Performs the agent's current action and then gets the next action time.
   * Chooses the next action to perform depending on whether order or
   * cancellation comes first in time.
   */
  @Override
  public void act() {
    long oldOrderTime = getNextOrderTime();
    switch (getNextAction()) {
      case CANCEL:
        // cancel the oldest order, if there are any available
        if (agentHasOrders()) {
          cancelOrder(getOldestOrder());
        }
        setNextCancelTime(getNextCancelTime());
        break;
      case ORDER:
        makeOrder();
        setNextOrderTime(getNextOrderTime());
        break;
      case NULL:
        System.err.println("Warning: NULL action type...have all agents been"
          + "  properly initialized?");
        break;
      default:
        System.err.println("Fatal Error: Undefined action enum type...exiting");
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

  /**
   * Calculates the next order time via a poisson distribution.
   * 
   * @param currOrderTime
   *          The current order time to add to.
   */
  @Override
  protected void setNextOrderTime(long currOrderTime) {
    super.setNextOrderTime((long) (currOrderTime + 1.0 - lambdaOrder
      * (orderDist.sample() - 1.0)));
  }

  /**
   * Calculates the next cancel time via a poisson distribution.
   * 
   * @param currCancelTime
   *          The current cancel time to add to.
   */
  @Override
  protected void setNextCancelTime(long currCancelTime) {
    super.setNextCancelTime((long) (currCancelTime + 1.0 - lambdaCancel
      * (cancelDist.sample() - 1.0)));
  }

  /**
   * Manages the probabilities of creating orders. Implementations will differ
   * depending on type of agent.
   */
  abstract void makeOrder();

  /**
   * Deals with order creation given a list of probabilities. *** The first
   * probability supplied must be the probability of creating a market order
   * ***. See wiki for more details.
   * 
   * @param isBuying
   *          True if agent is issuing a buy order; false if issuing a sell
   *          order.
   * @param quantity
   *          Number of shares to issue order for.
   * @param probabilities
   *          Probabilities of creating an order for a certain tick from the
   *          last trade price. The probabilities must be listed in ascending
   *          order with respect to the number of ticks of the last trade price,
   *          e.g. P[1 tick], P[2 tick], etc.
   */
  protected void createPoissonOrder(boolean isBuying, int quantity,
    ArrayList<Double> probabilities) {
    double probability = JinSup.rand.nextDouble();

    if (probability < probabilities.get(0)) {
      // create a market order
      createMarketOrder(quantity, isBuying);
      return;
    }

    double cumulativeProb = probabilities.get(0) + probabilities.get(1);
    for (int i = 1; i < probabilities.size(); ++i) {
      if (probability < cumulativeProb) {
        // create a limit order; if the agent is buying, then as the tick
        // increases, the lower the buy price
        createNewOrder(getLastTradePrice()
          - ((isBuying ? 1 : -1) * TICK_SIZE * i), quantity, isBuying);
        return;
      }
      cumulativeProb += probabilities.get(i + 1);
    }
  }

  /**
   * Calculates the probability of issuing an order for a certain quantity. See
   * PT Extension page for more details.
   * 
   * @param probabilities
   *          Probabilities of creating an order of a certain quantity. The
   *          probabilities must be listed in ascending order with respect to
   *          the order quantity, e.g. P[Q=1], P[Q=2], etc.
   * @return The quantity to issue an order for.
   */
  protected int getOrderSize(ArrayList<Double> probabilities) {
    double probability = JinSup.rand.nextDouble();
    double cumulativeProb = 0;
    int i = 0;
    do {
      cumulativeProb += probabilities.get(i);
      if (probability < cumulativeProb) {
        return i + 1;
      }
      ++i;
    } while (i < probabilities.size());
    System.err
      .println("Order size probabilites do not add up to 1.0 for poisson agent");
    return 0;
  }
}
