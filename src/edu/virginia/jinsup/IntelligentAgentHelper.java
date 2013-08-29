package edu.virginia.jinsup;

import java.util.Arrays;

/**
 * Keeps track of delay data for Intelligent Agents without cluttering
 * MatchingEngine.
 */
public class IntelligentAgentHelper {

  /**
   * How far into the past Intelligent Agents should look for market data in
   * milliseconds.
   */
  private int delayLength;

  /**
   * History of the Best bid price minus best ask price.
   */
  private int[] volumeDifferenceData;

  /**
   * History of the best bid price.
   */
  private int[] bestBidPriceData;

  /**
   * History of the best ask price.
   */
  private int[] bestAskPriceData;

  /**
   * The best bid price at time t = now - delayLength - 1.
   */
  private int previousOldBestBidPrice;

  /**
   * The best ask price at time t = now - delayLength - 1.
   */
  private int previousOldBestAskPrice;

  /**
   * The index corresponding to time t = now - delayLength.
   */
  private int oldestIndex;

  /**
   * Maximum difference between the total volume at the best bid/ask allowed
   * before additional actions are taken.
   */
  private int threshold;

  /**
   * True if threshold checking should be done. False otherwise.
   */
  private boolean thresholdEnabled;

  /**
   * Different threshold states the agent can be in.
   */
  public enum ThresholdState {
    BELOW_THRESHOLD, BUY_ORDER_SURPLUS, SELL_ORDER_SURPLUS;
  }

  /**
   * The threshold state corresponding to time t = now - delayLength.
   */
  private ThresholdState oldThresholdState;

  /**
   * Constructs the helper class for Intelligent Agents. Fills history with
   * initial values.
   * 
   * @param delayLength
   *          How long in the past the agent should look for market information.
   * @param threshold
   *          Maximum difference between the total volume at the best bid/ask
   *          allowed before additional actions are taken.
   * @param initialTradePrice
   *          The initial trade price of the simulation. Defined to be one tick
   *          below best ask and one tick above best bid.
   * @param thresholdEnabled
   *          Whether or not threshold checking should be done.
   */
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
    oldThresholdState = ThresholdState.BELOW_THRESHOLD;
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
   * Adds new data by overwriting the oldest data. Wraps around the array if
   * necessary.
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

  /**
   * Update the old index to point at oldest data so far.
   */
  public void updateOldIndex() {
    oldestIndex++;
    if (oldestIndex >= delayLength) {
      oldestIndex = 0;
    }
  }

  /**
   * Update both the old index and the volume difference data.
   * 
   * @param newVolumeDifference
   *          The new value of best bid - best ask total volume.
   */
  public void updateOldIndexWithThreshold(int newVolumeDifference) {
    volumeDifferenceData[oldestIndex] = newVolumeDifference;
    updateOldIndex();
    oldThresholdState =
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
    if (Math.abs(volumeDifference) <= threshold) {
      return ThresholdState.BELOW_THRESHOLD;
    } else if (volumeDifference < threshold) {
      return ThresholdState.SELL_ORDER_SURPLUS;
    } else {
      return ThresholdState.BUY_ORDER_SURPLUS;
    }
  }

  /**
   * Gets the threshold state at time t = now - delayLength.
   * 
   * @return The threshold state.
   */
  public ThresholdState getOldThresholdState() {
    return oldThresholdState;
  }

  /**
   * Gets the value of the index that is pointing at data at time t = now -
   * delayLength.
   * 
   * @return The value of the index that is pointing at data at time t = now -
   *         delayLength.
   */
  public int getOldestIndex() {
    return oldestIndex;
  }

  /**
   * Get the best bid price at time t = now - delayLength - 1.
   * 
   * @return The best bid price at time t = now - delayLength - 1.
   */
  public int getPreviousOldBestBidPrice() {
    return previousOldBestBidPrice;
  }

  /**
   * Get the best ask price at time t = now - delayLength - 1.
   * 
   * @return The best ask price at time t = now - delayLength - 1.
   */
  public int getPreviousOldBestAskPrice() {
    return previousOldBestAskPrice;
  }
}