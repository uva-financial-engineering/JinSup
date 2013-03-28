package edu.virginia.jinsup;

public abstract class PoissonAgent extends Agent {

  private int lambda;
  public PoissonAgent(MatchingEngine matchEng, int lambda) {
    super(matchEng);
    this.lambda = lambda;
  }

  abstract void act();
  
  protected double calculatePMF(int trades)
  {
    return (Math.pow(lambda, trades) * Math.exp(-lambda)) / factorial(trades);
  }
  
  protected int factorial(int num)
  {
    if(num == 0)
    {
      return 1;
    }
    int result = 1;
    for(int i = num; i > 0; --i)
    {
      result *= i;
    }
    return result;
  }

}
