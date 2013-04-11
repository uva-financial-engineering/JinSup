package edu.virginia.jinsup;

public class FundSellerPoisson extends PoissonAgent {

  public FundSellerPoisson(MatchingEngine matchEng, int lambdaOrder,
    int lambdaCancel) {
    super(matchEng, lambdaOrder, lambdaCancel);
  }

  // probabilities are the same as in the fundamental buyer's method...for now
  public void makeOrder() {
    // calculate the probability
    double probability = Math.random();
    // Market order 14% of the time
    if (probability < 0.14) {
      // Issue market order, assuming only one market order is
      createMarketOrder(1, false);
    }
    // 1 tick of last trade price 27% of the time
    else if (probability < 0.41) {
      createNewOrder(getLastTradePrice(), 1, false);
    }
    // 2 tick of last trade price 11% of the time
    else if (probability < 0.52) {
      createNewOrder(getLastTradePrice(), 2, false);
    }
    // 3 tick of last trade price 9% of the time
    else if (probability < 0.61) {
      createNewOrder(getLastTradePrice(), 3, false);
    }
    // 4 tick of last trade price 7% of the time
    else if (probability < 0.68) {
      createNewOrder(getLastTradePrice(), 4, false);
    }
    // 5 tick of last trade price 7% of the time
    else if (probability < 0.75) {
      createNewOrder(getLastTradePrice(), 5, false);
    }
    // 6 tick of last trade price 7% of the time
    else if (probability < 0.82) {
      createNewOrder(getLastTradePrice(), 6, false);
    }
    // 7 tick of last trade price 5% of the time
    else if (probability < 0.87) {
      createNewOrder(getLastTradePrice(), 7, false);
    }
    // 8 tick of last trade price 5% of the time
    else if (probability < 0.92) {
      createNewOrder(getLastTradePrice(), 8, false);
    }
    // 9 tick of last trade price 5% of the time
    else if (probability < 0.97) {
      createNewOrder(getLastTradePrice(), 9, false);
    }
    // 10 tick of last trade price 3% of the time
    else {
      createNewOrder(getLastTradePrice(), 10, false);
    }
  }
}
