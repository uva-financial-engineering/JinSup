package edu.virginia.jinsup;

public abstract class PoissonAgent extends Agent {

  private final int lambda;
  private final double expLambda;

  public PoissonAgent(MatchingEngine matchEng, int lambda) {
    super(matchEng);
    this.lambda = lambda;
    expLambda = Math.exp(-lambda);
  }

  @Override
  abstract void act();

  protected double calculatePMF(int trades) {
    int factorial = 1;
    for (int i = 2; i <= trades; ++i) {
      factorial *= i;
    }
    return Math.pow(lambda, trades) * expLambda / factorial;
  }

}
