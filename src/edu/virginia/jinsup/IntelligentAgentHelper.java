package edu.virginia.jinsup;

/**
 * Keeps track of delay data for Intelligent Agents without cluttering
 * MatchingEngine.
 */
public class IntelligentAgentHelper {

  private int delayLength;
  private int[] volumeDifferenceData;
  private int[] tradePriceData;
  private int oldestIndex;
  private int threshold;
  private int currentTradePriceDifference;

  public enum ThresholdState {
    BELOW_THRESHOLD, BUY_ORDER_SURPLUS, SELL_ORDER_SURPLUS;
  }

  private ThresholdState currentThresholdState;

  public IntelligentAgentHelper(int delayLength, int threshold) {
    this.delayLength = delayLength;
    volumeDifferenceData = new int[delayLength];
    tradePriceData = new int[delayLength];
    oldestIndex = 0;
    this.threshold = threshold;
    currentThresholdState = ThresholdState.BELOW_THRESHOLD;
    currentTradePriceDifference = 0;
  }

  /**
   * Get the oldest volume difference data. Must ensure this is called first
   * before adding new data otherwise it will be overwritten.
   * 
   * @return The oldest data so far.
   */
  public int getOldVolumeDifferenceData() {
    return volumeDifferenceData[oldestIndex];
  }

  /**
   * Get the oldest tradePrice data. Must ensure this is called first before
   * adding new data otherwise it will be overwritten.
   * 
   * @return The oldest data so far.
   */
  public int getOldTradePriceData() {
    return tradePriceData[oldestIndex];
  }

  public int getPreviousOldTradePriceData() {
    return (oldestIndex == 0 ? tradePriceData[delayLength - 1]
      : tradePriceData[oldestIndex - 1]);
  }

  /**
   * Adds new data. Wraps around the array if necessary.
   * 
   * @param newData
   */
  public void addData(int newVolumeDifference, int newTradePrice) {
    volumeDifferenceData[oldestIndex] = newVolumeDifference;
    tradePriceData[oldestIndex] = newTradePrice;
    currentThresholdState = computeThresholdState(newVolumeDifference);
    oldestIndex++;
    if (oldestIndex >= delayLength) {
      oldestIndex = 0;
    }
    currentTradePriceDifference =
      getPreviousOldTradePriceData() - getOldTradePriceData();
  }

  private ThresholdState computeThresholdState(int volumeDifference) {
    if (Math.abs(volumeDifference) < threshold) {
      return ThresholdState.BELOW_THRESHOLD;
    } else if (volumeDifference < threshold) {
      return ThresholdState.SELL_ORDER_SURPLUS;
    } else {
      return ThresholdState.BUY_ORDER_SURPLUS;
    }
  }

  /**
   * @return TradePrice[i - 1] - TradePrice[i].
   */
  public int getTradePriceDifference() {
    return currentTradePriceDifference;
  }

  public ThresholdState getCurrentThresholdState() {
    return currentThresholdState;
  }

  public int getOldestIndex() {
    return oldestIndex;
  }
}