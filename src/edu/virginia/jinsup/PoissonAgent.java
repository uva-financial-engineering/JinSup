package edu.virginia.jinsup;

import org.apache.commons.math3.distribution.PoissonDistribution;

public abstract class PoissonAgent extends Agent {

  private final PoissonDistribution poissonGeneratorOrder;
  private final PoissonDistribution poissonGeneratorCancel;

  // lambda specified in seconds
  public PoissonAgent(MatchingEngine matchEng, int lambdaOrder, int lambdaCancel) {
    super(matchEng);
    poissonGeneratorOrder = new PoissonDistribution(lambdaOrder);
    poissonGeneratorCancel = new PoissonDistribution(lambdaCancel);
  }

  public void act()
  {
    long oldOrderTime = getNextOrderTime();
    switch (getNextAction())
    {
      case CANCEL:
        // cancel a random order
        setNextCancelTime(getNextCancelTime());
        break;
      case ORDER:
        makeOrder();
        setNextOrderTime(getNextOrderTime());
        break;
      case NULL:
        System.out.println("Warning: NULL action type...have all agents been" +
        		"  properly initialized?");
        break;
      default:
        System.out.println("Fatal Error: Undefined action enum type...exiting");
        System.exit(1);
        break;
    }
    
    // make sure that both actions do not occur at the same time step
    if(getNextCancelTime() ==  getNextOrderTime())
    {
      while(getNextCancelTime() == getNextOrderTime())
      {
        setNextOrderTime(oldOrderTime);
      }
    }
    
    if (getNextCancelTime() > getNextOrderTime())
    {
      setNextAction(Action.ORDER);
      setNextActTime(getNextOrderTime());
    }
    else
    {
      setNextAction(Action.CANCEL);
      setNextActTime(getNextOrderTime());
    }
  }
  
  protected void setNextOrderTime(long currOrderTime) 
  {
    setNextOrderTime(currOrderTime + poissonGeneratorOrder.sample());
    
  }

  protected void setNextCancelTime(long currCancelTime) 
  {
    setNextOrderTime(currCancelTime + poissonGeneratorCancel.sample());
  }
  
  abstract void makeOrder();
}
