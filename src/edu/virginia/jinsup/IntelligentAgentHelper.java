package edu.virginia.jinsup;

import java.util.Arrays;

/**
 * Keeps track of delay data for Intelligent Agents without cluttering
 * MatchingEngine.
 */
public class IntelligentAgentHelper {

  private int delayLength;
  private int[] volumeDifferenceData;
  private int[] bestBidPriceData;
  private int[] bestAskPriceData;
  private int previousOldBestBidPrice;
  private int previousOldBestAskPrice;
  private int oldestIndex;
  private int threshold;
  private boolean thresholdEnabled;

  public enum ThresholdState {
    BELOW_THRESHOLD, BUY_ORDER_SURPLUS, SELL_ORDER_SURPLUS;
  }

  private ThresholdState pastThresholdState;

  public IntelligentAgentHelper(int delayLength, int threshold,
    int initialTradePrice, boolean thresholdEnabled) {
    this.delayLength = delayLength;
    volumeDifferenceData = new int[delayLength];
    bestBidPriceData = new int[delayLength];
    bestAskPriceData = new int[delayLength];
    Arrays.fill(bestBidPriceData, initialTradePrice - Agent.TICK_SIZE);
    Arrays.fill(bestAskPriceData, initialTradePrice + Agent.TICK_SIZE);
    previousOldBestBidPrice = initialTradePrice - Agent.TICK_SIZE;
    previousOldBestAskPrice = initialTradePrice + Agent.TICK_SIZE;
    oldestIndex = 0;
    this.threshold = threshold;
    pastThresholdState = ThresholdState.BELOW_THRESHOLD;
    this.thresholdEnabled = thresholdEnabled;
  }

  /**
   * Get the oldest best bid price. Must ensure this is called first before
   * adding new data otherwise it will be overwritten.
   * 
   * @return The oldest best bid price so far.
   */
  public int getOldBestBidPrice() {
    return bestBidPriceData[oldestIndex];
  }

  /**
   * Get the oldest best ask price. Must ensure this is called first before
   * adding new data otherwise it will be overwritten.
   * 
   * @return The oldest best ask price so far.
   */
  public int getOldBestAskPrice() {
    return bestAskPriceData[oldestIndex];
  }

  /**
   * Adds new data. Wraps around the array if necessary.
   * 
   * @param newData
   */
  public void addData(int newVolumeDifference, int newBestBidPrice,
    int newBestAskPrice) {
    previousOldBestBidPrice = bestBidPriceData[oldestIndex];
    previousOldBestAskPrice = bestAskPriceData[oldestIndex];

    // Overwrite
    bestBidPriceData[oldestIndex] = newBestBidPrice;
    bestAskPriceData[oldestIndex] = newBestAskPrice;

    if (thresholdEnabled) {
      updateOldIndexWithThreshold(newVolumeDifference);
    } else {
      updateOldIndex();
    }
  }

  public void updateOldIndex() {
    oldestIndex++;
    if (oldestIndex >= delayLength) {
      oldestIndex = 0;
    }
  }

  public void updateOldIndexWithThreshold(int newVolumeDifference) {
    volumeDifferenceData[oldestIndex] = newVolumeDifference;
    updateOldIndex();
    pastThresholdState =
      computeThresholdState(volumeDifferenceData[oldestIndex]);
  }

  /**
   * Determines the threshold state from the sum of orders at the best bid/ask.
   * 
   * @param volumeDifference
   *          Total volume at best bid - total volume at best ask.
   * @return The ThresholdState.
   */
  private ThresholdState computeThresholdState(int volumeDifference) {
    if (Math.abs(volumeDifference) < threshold) {
      return ThresholdState.BELOW_THRESHOLD;
    } else if (volumeDifference < threshold) {
      return ThresholdState.SELL_ORDER_SURPLUS;
    } else {
      return ThresholdState.BUY_ORDER_SURPLUS;
    }
  }

  public ThresholdState getPastThresholdState() {
    return pastThresholdState;
  }

  public int getOldestIndex() {
    return oldestIndex;
  }

  public int getPreviousOldBestBidPrice() {
    return previousOldBestBidPrice;
  }

  public int getPreviousOldBestAskPrice() {
    return previousOldBestAskPrice;
  }
}